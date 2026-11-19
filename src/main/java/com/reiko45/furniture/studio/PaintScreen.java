package com.reiko45.furniture.studio;

import com.reiko45.furniture.core.network.ModNetwork;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Client only: paints the canvas on an easel. Left-click (or drag) paints with the chosen color,
 * right-click rubs out; the fill tool fills the touching area of one color. The painting is saved when
 * the screen closes. Signing gives it a title and finishes it for good.
 */
public class PaintScreen extends Screen {
    private static final int CELL = 9;
    private static final int GRID = Painting.SIZE * CELL;
    private static final int SWATCH = 12;
    private static final int PANEL_WIDTH = 232;
    private static final int PANEL_HEIGHT = 214;

    private final BlockPos easel;
    private final char[] pixels;
    private char brush = Painting.of(DyeColor.BLACK);
    private boolean fill;
    private boolean signing;
    private boolean dirty;
    private boolean sent;
    /** The last cell painted while dragging, so a quick drag paints every cell in between. */
    private int lastCell = -1;
    /** Set when signing starts: the title box takes the focus on the next tick, after the click is done with. */
    private boolean focusTitle;
    private int left;
    private int top;
    private Button fillButton;
    private Button doneButton;
    private Button signButton;
    private EditBox titleBox;
    private Button confirmButton;
    private Button cancelButton;

    public PaintScreen(BlockPos easel, Painting painting) {
        super(Component.translatable("container.reikos_furniture.easel"));
        this.easel = easel;
        this.pixels = painting.pixels().toCharArray();
    }

    @Override
    protected void init() {
        left = (width - PANEL_WIDTH) / 2;
        top = (height - PANEL_HEIGHT) / 2;
        int paletteX = left + 16 + GRID;
        fillButton = addRenderableWidget(Button.builder(fillLabel(), button -> {
            fill = !fill;
            button.setMessage(fillLabel());
        }).bounds(paletteX, top + 156, 60, 16).build());
        doneButton = addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left + 8, top + PANEL_HEIGHT - 26, 70, 20).build());
        signButton = addRenderableWidget(Button.builder(Component.translatable("container.reikos_furniture.easel.sign"),
                button -> setSigning(true)).bounds(left + 82, top + PANEL_HEIGHT - 26, 70, 20).build());
        titleBox = addRenderableWidget(new EditBox(font, left + 8, top + PANEL_HEIGHT - 26, 112, 20,
                Component.translatable("container.reikos_furniture.easel.title")));
        titleBox.setMaxLength(Painting.MAX_TITLE);
        titleBox.setHint(Component.translatable("container.reikos_furniture.easel.title"));
        confirmButton = addRenderableWidget(Button.builder(Component.translatable("container.reikos_furniture.easel.sign"), button -> {
            if (!titleBox.getValue().isBlank()) {
                send(titleBox.getValue());
                super.onClose();
            }
        }).bounds(left + 124, top + PANEL_HEIGHT - 26, 50, 20).build());
        cancelButton = addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> setSigning(false))
                .bounds(left + 178, top + PANEL_HEIGHT - 26, 46, 20).build());
        setSigning(signing);
    }

    private Component fillLabel() {
        return Component.translatable(fill ? "container.reikos_furniture.easel.fill" : "container.reikos_furniture.easel.brush");
    }

    private void setSigning(boolean signing) {
        this.signing = signing;
        doneButton.visible = !signing;
        fillButton.visible = !signing;
        signButton.visible = !signing;
        titleBox.visible = signing;
        confirmButton.visible = signing;
        cancelButton.visible = signing;
        focusTitle = signing;
    }

    @Override
    public void tick() {
        if (focusTitle) {
            focusTitle = false;
            setFocused(titleBox);
        }
        confirmButton.active = !titleBox.getValue().isBlank();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(font, title, left + 8, top + 7, 0x404040, false);
        int gridX = left + 8;
        int gridY = top + 18;
        graphics.fill(gridX - 1, gridY - 1, gridX + GRID + 1, gridY + GRID + 1, 0xFF6B5A44);
        for (int i = 0; i < pixels.length; i++) {
            int x = gridX + (i % Painting.SIZE) * CELL;
            int y = gridY + (i / Painting.SIZE) * CELL;
            graphics.fill(x, y, x + CELL, y + CELL, Painting.color(pixels[i]));
        }
        int hovered = cellAt(mouseX, mouseY);
        if (hovered >= 0 && !signing) {
            int x = gridX + (hovered % Painting.SIZE) * CELL;
            int y = gridY + (hovered / Painting.SIZE) * CELL;
            graphics.renderOutline(x, y, CELL, CELL, 0xFFFFFFFF);
        }
        // The palette: 16 dye colors in two columns, then the eraser (bare canvas).
        for (int i = 0; i <= DyeColor.values().length; i++) {
            int x = swatchX(i);
            int y = swatchY(i);
            char color = i < DyeColor.values().length ? Painting.of(DyeColor.byId(i)) : Painting.BARE;
            graphics.fill(x, y, x + SWATCH, y + SWATCH, Painting.color(color));
            if (color == Painting.BARE) {
                graphics.drawString(font, "x", x + 3, y + 1, 0xFF8B3A3A, false);
            }
            graphics.renderOutline(x - 1, y - 1, SWATCH + 2, SWATCH + 2, color == brush ? 0xFFFFFFFF : 0xFF3A3A3A);
        }
        if (signing) {
            graphics.drawWordWrap(font, Component.translatable("container.reikos_furniture.easel.signing"),
                    left + 8, top + 167, PANEL_WIDTH - 16, 0x404040);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xFFC6C6C6);
        graphics.renderOutline(left, top, PANEL_WIDTH, PANEL_HEIGHT, 0xFF555555);
    }

    private int swatchX(int index) {
        return left + 18 + GRID + (index % 2) * (SWATCH + 4);
    }

    private int swatchY(int index) {
        return top + 18 + (index / 2) * (SWATCH + 3);
    }

    /** The grid cell under the mouse, or -1. */
    private int cellAt(double mouseX, double mouseY) {
        int x = (int) Math.floor((mouseX - (left + 8)) / CELL);
        int y = (int) Math.floor((mouseY - (top + 18)) / CELL);
        return x >= 0 && y >= 0 && x < Painting.SIZE && y < Painting.SIZE ? y * Painting.SIZE + x : -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!signing) {
            int cell = cellAt(mouseX, mouseY);
            lastCell = cell;
            if (cell >= 0) {
                char color = button == 1 ? Painting.BARE : brush;
                if (fill) {
                    fill(cell, color);
                } else {
                    paint(cell, color);
                }
                return true;
            }
            for (int i = 0; i <= DyeColor.values().length; i++) {
                if (mouseX >= swatchX(i) && mouseX < swatchX(i) + SWATCH && mouseY >= swatchY(i) && mouseY < swatchY(i) + SWATCH) {
                    brush = i < DyeColor.values().length ? Painting.of(DyeColor.byId(i)) : Painting.BARE;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int cell = cellAt(mouseX, mouseY);
        if (!signing && !fill && cell >= 0) {
            char color = button == 1 ? Painting.BARE : brush;
            if (lastCell >= 0) {
                line(lastCell, cell, color);
            } else {
                paint(cell, color);
            }
            lastCell = cell;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /** Paints the cells on a straight line between two cells. */
    private void line(int from, int to, char color) {
        int x0 = from % Painting.SIZE;
        int y0 = from / Painting.SIZE;
        int x1 = to % Painting.SIZE;
        int y1 = to / Painting.SIZE;
        int steps = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        for (int i = 0; i <= steps; i++) {
            int x = steps == 0 ? x0 : Math.round(x0 + (x1 - x0) * (float) i / steps);
            int y = steps == 0 ? y0 : Math.round(y0 + (y1 - y0) * (float) i / steps);
            paint(y * Painting.SIZE + x, color);
        }
    }

    private void paint(int cell, char color) {
        if (pixels[cell] != color) {
            pixels[cell] = color;
            dirty = true;
        }
    }

    /** Fills the area of one color that touches the cell (sideways and up and down). */
    private void fill(int start, char color) {
        char replaced = pixels[start];
        if (replaced == color) {
            return;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            int cell = queue.poll();
            if (pixels[cell] != replaced) {
                continue;
            }
            pixels[cell] = color;
            int x = cell % Painting.SIZE;
            int y = cell / Painting.SIZE;
            if (x > 0) {
                queue.add(cell - 1);
            }
            if (x < Painting.SIZE - 1) {
                queue.add(cell + 1);
            }
            if (y > 0) {
                queue.add(cell - Painting.SIZE);
            }
            if (y < Painting.SIZE - 1) {
                queue.add(cell + Painting.SIZE);
            }
        }
        dirty = true;
    }

    private void send(String title) {
        sent = true;
        PacketDistributor.sendToServer(new ModNetwork.PaintCanvas(easel, new String(pixels), title));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (signing && keyCode == 256) { // Escape leaves the title, not the painting
            setSigning(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        if (dirty && !sent) {
            send("");
        }
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
