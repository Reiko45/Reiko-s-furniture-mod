package com.reiko45.furniture.core.mail;

import com.mojang.authlib.GameProfile;
import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The mailbox screen's slots: the owner's inbox (only for the owner), nine slots for the parcel to
 * send, and the player's inventory. What is left in the parcel slots goes back to the player when the
 * screen closes.
 */
public class MailboxMenu extends AbstractContainerMenu {
    public static final int PARCEL_SIZE = 9;
    public static final int INBOX_Y = 18;
    public static final int PARCEL_Y = 108;
    public static final int INVENTORY_Y = 140;

    private final boolean owner;
    private final String ownerName;
    private final Container inbox;
    private final SimpleContainer parcel = new SimpleContainer(PARCEL_SIZE);
    private final ContainerLevelAccess access;
    private final int parcelStart;

    /** Client side, from the data the server sends when the screen opens. */
    public MailboxMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(id, inventory, buf.readBoolean(), buf.readUtf(), new SimpleContainer(PostOffice.INBOX_SIZE), ContainerLevelAccess.NULL);
    }

    public MailboxMenu(int id, Inventory inventory, boolean owner, String ownerName, Container inbox, ContainerLevelAccess access) {
        super(Mail.MENU.get(), id);
        this.owner = owner;
        this.ownerName = ownerName;
        this.inbox = inbox;
        this.access = access;
        if (owner) {
            for (int i = 0; i < PostOffice.INBOX_SIZE; i++) {
                addSlot(new Slot(inbox, i, 8 + (i % 9) * 18, INBOX_Y + (i / 9) * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false; // mail comes in by post only
                    }
                });
            }
        }
        parcelStart = slots.size();
        for (int i = 0; i < PARCEL_SIZE; i++) {
            addSlot(new Slot(parcel, i, 8 + i * 18, PARCEL_Y));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, INVENTORY_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, INVENTORY_Y + 58));
        }
    }

    public boolean isOwner() {
        return owner;
    }

    public String ownerName() {
        return ownerName;
    }

    /** Sends the parcel to the player called {@code name}. Server side; the sender is told how it went. */
    public void send(ServerPlayer sender, String name) {
        MinecraftServer server = sender.server;
        String recipientName = name.trim();
        ServerPlayer online = server.getPlayerList().getPlayerByName(recipientName);
        Optional<GameProfile> profile = online != null ? Optional.of(online.getGameProfile())
                : server.getProfileCache() == null ? Optional.empty() : server.getProfileCache().get(recipientName);
        if (profile.isEmpty()) {
            sender.displayClientMessage(Component.translatable("message.reikos_furniture.mail_unknown_player", recipientName), true);
            return;
        }
        PostOffice office = PostOffice.get(server);
        GameProfile recipient = profile.get();
        if (!office.isOwner(recipient.getId())) {
            sender.displayClientMessage(Component.translatable("message.reikos_furniture.mail_no_mailbox", recipient.getName()), true);
            return;
        }
        if (parcel.isEmpty()) {
            sender.displayClientMessage(Component.translatable("message.reikos_furniture.mail_nothing"), true);
            return;
        }
        boolean all = office.deliver(recipient.getId(), parcel);
        broadcastChanges();
        sender.displayClientMessage(Component.translatable(all ? "message.reikos_furniture.mail_sent"
                : "message.reikos_furniture.mail_partly_sent", recipient.getName()), true);
        sender.level().playSound(null, sender.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        ServerPlayer receiver = server.getPlayerList().getPlayer(recipient.getId());
        if (receiver != null) {
            receiver.displayClientMessage(Component.translatable("message.reikos_furniture.mail_received",
                    sender.getGameProfile().getName()), false);
            FurnitureActionTrigger.award(receiver, FurnitureActionTrigger.MAIL);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerStart = parcelStart + PARCEL_SIZE;
        if (index < playerStart) {
            // From the inbox or the parcel into the player's inventory.
            if (!moveItemStackTo(stack, playerStart, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, parcelStart, playerStart, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof MailboxBlock
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, parcel);
    }
}
