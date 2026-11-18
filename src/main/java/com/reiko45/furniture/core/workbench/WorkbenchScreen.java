package com.reiko45.furniture.core.workbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Client screen for the furniture workbench. Uses the vanilla stonecutter background; recipes the
 * planks in the input slot can't pay for are drawn faded, and the tooltip shows the plank cost.
 */
public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/stonecutter.png");
    private static final ResourceLocation SCROLLER = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller_disabled");
    private static final ResourceLocation RECIPE_SELECTED = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_selected");
    private static final ResourceLocation RECIPE_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    private static final ResourceLocation RECIPE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe");
    private static final int COLUMNS = 4;
    private static final int VISIBLE = 12;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;

    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public WorkbenchScreen(WorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        menu.registerUpdateListener(this::containerChanged);
        this.titleLabelY--;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int scroll = (int) (41.0F * scrollOffset);
        graphics.blitSprite(isScrollBarActive() ? SCROLLER : SCROLLER_DISABLED, leftPos + 119, topPos + 15 + scroll, 12, 15);
        int x = leftPos + RECIPES_X;
        int y = topPos + RECIPES_Y;
        List<RecipeHolder<CarpentryRecipe>> recipes = menu.getRecipes();
        for (int i = startIndex; i < startIndex + VISIBLE && i < recipes.size(); i++) {
            int slot = i - startIndex;
            int bx = x + slot % COLUMNS * 16;
            int by = y + slot / COLUMNS * 18 + 2;
            ResourceLocation sprite;
            if (i == menu.getSelectedRecipeIndex()) {
                sprite = RECIPE_SELECTED;
            } else if (mouseX >= bx && mouseY >= by && mouseX < bx + 16 && mouseY < by + 18) {
                sprite = RECIPE_HIGHLIGHTED;
            } else {
                sprite = RECIPE;
            }
            graphics.blitSprite(sprite, bx, by - 1, 16, 18);
            ItemStack result = recipes.get(i).value().result();
            graphics.renderItem(result, bx, by);
            if (!menu.canAfford(recipes.get(i))) {
                // Fade pieces there aren't enough planks for.
                graphics.fill(bx, by, bx + 16, by + 16, 200, 0x99000000);
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (!displayRecipes) {
            return;
        }
        int x = leftPos + RECIPES_X;
        int y = topPos + RECIPES_Y;
        List<RecipeHolder<CarpentryRecipe>> recipes = menu.getRecipes();
        for (int i = startIndex; i < startIndex + VISIBLE && i < recipes.size(); i++) {
            int slot = i - startIndex;
            int bx = x + slot % COLUMNS * 16;
            int by = y + slot / COLUMNS * 18 + 2;
            if (mouseX >= bx && mouseX < bx + 16 && mouseY >= by && mouseY < by + 18) {
                CarpentryRecipe recipe = recipes.get(i).value();
                List<Component> lines = new ArrayList<>(Screen.getTooltipFromItem(minecraft, recipe.result()));
                lines.add(Component.translatable("container.reikos_furniture.furniture_workbench.cost", recipe.count())
                        .withStyle(menu.canAfford(recipes.get(i)) ? ChatFormatting.GRAY : ChatFormatting.RED));
                graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrolling = false;
        if (displayRecipes) {
            int x = leftPos + RECIPES_X;
            int y = topPos + RECIPES_Y;
            for (int i = startIndex; i < startIndex + VISIBLE; i++) {
                int slot = i - startIndex;
                double dx = mouseX - (x + slot % COLUMNS * 16);
                double dy = mouseY - (y + slot / COLUMNS * 18);
                if (dx >= 0 && dy >= 0 && dx < 16 && dy < 18 && menu.clickMenuButton(minecraft.player, i)) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                    return true;
                }
            }
            int sx = leftPos + 119;
            int sy = topPos + 9;
            if (mouseX >= sx && mouseX < sx + 12 && mouseY >= sy && mouseY < sy + 54) {
                scrolling = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling && isScrollBarActive()) {
            int top = topPos + RECIPES_Y;
            int bottom = top + 54;
            scrollOffset = Mth.clamp(((float) mouseY - top - 7.5F) / ((bottom - top) - 15.0F), 0.0F, 1.0F);
            startIndex = (int) (scrollOffset * offscreenRows() + 0.5) * COLUMNS;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isScrollBarActive()) {
            int rows = offscreenRows();
            scrollOffset = Mth.clamp(scrollOffset - (float) scrollY / rows, 0.0F, 1.0F);
            startIndex = (int) (scrollOffset * rows + 0.5) * COLUMNS;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return displayRecipes && menu.getRecipes().size() > VISIBLE;
    }

    private int offscreenRows() {
        return (menu.getRecipes().size() + COLUMNS - 1) / COLUMNS - 3;
    }

    private void containerChanged() {
        displayRecipes = menu.hasInputItem();
        if (!displayRecipes) {
            scrollOffset = 0.0F;
            startIndex = 0;
        }
    }
}
