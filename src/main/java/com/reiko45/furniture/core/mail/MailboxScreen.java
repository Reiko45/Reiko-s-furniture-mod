package com.reiko45.furniture.core.mail;

import com.reiko45.furniture.core.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/**
 * The mailbox screen: the owner's mail at the top, then the parcel to send with the recipient's name
 * and a Send button, then the player's inventory. Drawn like a vanilla container.
 */
public class MailboxScreen extends AbstractContainerScreen<MailboxMenu> {
    private static final int PANEL = 0xFFC6C6C6;
    private static final int LIGHT = 0xFFFFFFFF;
    private static final int DARK = 0xFF555555;
    private static final int SLOT = 0xFF8B8B8B;
    private static final int TEXT = 0xFF404040;

    private EditBox recipient;

    public MailboxScreen(MailboxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 222;
        inventoryLabelY = MailboxMenu.INVENTORY_Y - 11;
    }

    @Override
    protected void init() {
        super.init();
        recipient = new EditBox(font, leftPos + 8, topPos + 88, 104, 16, Component.translatable("container.reikos_furniture.mailbox.send_to"));
        recipient.setMaxLength(32);
        if (!menu.isOwner()) {
            recipient.setValue(menu.ownerName());
        }
        addRenderableWidget(recipient);
        addRenderableWidget(Button.builder(Component.translatable("container.reikos_furniture.mailbox.send"), button -> send())
                .bounds(leftPos + 116, topPos + 86, 52, 20).build());
    }

    private void send() {
        if (!recipient.getValue().isBlank()) {
            PacketDistributor.sendToServer(new ModNetwork.SendMail(recipient.getValue()));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (recipient.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                send();
                return true;
            }
            // Typing a name must not close the screen ("e") or drop items ("q").
            return recipient.keyPressed(keyCode, scanCode, modifiers) || recipient.canConsumeInput();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL);
        graphics.fill(x, y, x + imageWidth - 1, y + 1, LIGHT);
        graphics.fill(x, y, x + 1, y + imageHeight - 1, LIGHT);
        graphics.fill(x + imageWidth - 1, y + 1, x + imageWidth, y + imageHeight, DARK);
        graphics.fill(x + 1, y + imageHeight - 1, x + imageWidth, y + imageHeight, DARK);
        for (Slot slot : menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            graphics.fill(sx, sy, sx + 18, sy + 18, DARK);
            graphics.fill(sx + 1, sy + 1, sx + 18, sy + 18, LIGHT);
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, SLOT);
        }
        if (!menu.isOwner()) {
            int lineY = y + MailboxMenu.INBOX_Y + 14;
            for (FormattedCharSequence line : font.split(Component.translatable("container.reikos_furniture.mailbox.not_yours",
                    menu.ownerName()), imageWidth - 16)) {
                graphics.drawString(font, line, x + 8, lineY, TEXT, false);
                lineY += 10;
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, TEXT, false);
        graphics.drawString(font, Component.translatable("container.reikos_furniture.mailbox.send_to"), 8, 77, TEXT, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }
}
