package com.reiko45.furniture.core.catalog;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModRecipes;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

/**
 * The furniture catalog. Sections (rooms, styles, extras) on the left; the pieces of the chosen
 * section in the middle, cycling through their woods and colors; the chosen piece's recipe on the right.
 * Scroll the lists with the mouse wheel; scroll over the recipe to step through a piece's variants.
 */
public class CatalogScreen extends Screen {
    private static final int WIDTH = 340;
    private static final int HEIGHT = 204;
    private static final int LIST_W = 100;
    private static final int ROW_H = 11;
    private static final int COLS = 7;
    private static final int SLOT = 18;
    private static final int GRID_ROWS = 9;

    private static final int PAPER = 0xFFF3E9D2;
    private static final int PAPER_DARK = 0xFFE4D5B4;
    private static final int COVER = 0xFF6B4226;
    private static final int INK = 0xFF3B2A1A;
    private static final int INK_LIGHT = 0xFF7A6248;
    private static final int HIGHLIGHT = 0xFFD9C08F;

    private final List<CatalogEntries.Section> sections = CatalogEntries.sections();
    private int section;
    private int listScroll;
    private int gridScroll;
    @Nullable
    private CatalogEntries.Entry selected;
    private int variant;

    public CatalogScreen() {
        super(Component.translatable("item.reikos_furniture.furniture_catalog"));
    }

    private int left() {
        return (width - WIDTH) / 2;
    }

    private int top() {
        return (height - HEIGHT) / 2;
    }

    private int listRows() {
        return (HEIGHT - 28) / ROW_H;
    }

    private int gridX() {
        return left() + LIST_W + 12;
    }

    private int detailX() {
        return gridX() + COLS * SLOT + 8;
    }

    private static int cycle(int size) {
        return size == 0 ? 0 : (int) ((System.currentTimeMillis() / 1000) % size);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int x = left();
        int y = top();
        g.fill(x - 4, y - 4, x + WIDTH + 4, y + HEIGHT + 4, COVER);
        g.fill(x, y, x + WIDTH, y + HEIGHT, PAPER);
        g.fill(x + LIST_W + 6, y + 18, x + LIST_W + 7, y + HEIGHT - 6, PAPER_DARK);
        g.fill(detailX() - 4, y + 18, detailX() - 3, y + HEIGHT - 6, PAPER_DARK);
        g.drawString(font, title, x + (WIDTH - font.width(title)) / 2, y + 6, INK, false);

        renderSections(g, mouseX, mouseY);
        ItemStack hoveredItem = renderGrid(g, mouseX, mouseY);
        ItemStack hoveredRecipe = renderDetail(g, mouseX, mouseY);
        if (hoveredItem != null) {
            g.renderTooltip(font, hoveredItem, mouseX, mouseY);
        } else if (hoveredRecipe != null) {
            g.renderTooltip(font, hoveredRecipe, mouseX, mouseY);
        }
    }

    /** The section list; long names are shortened to fit. */
    private void renderSections(GuiGraphics g, int mouseX, int mouseY) {
        int x = left() + 6;
        int y = top() + 20;
        int rows = Math.min(listRows(), sections.size() - listScroll);
        for (int i = 0; i < rows; i++) {
            int index = listScroll + i;
            int rowY = y + i * ROW_H;
            boolean over = mouseX >= x && mouseX < x + LIST_W && mouseY >= rowY && mouseY < rowY + ROW_H;
            if (index == section) {
                g.fill(x - 2, rowY - 1, x + LIST_W - 2, rowY + ROW_H - 1, HIGHLIGHT);
            } else if (over) {
                g.fill(x - 2, rowY - 1, x + LIST_W - 2, rowY + ROW_H - 1, PAPER_DARK);
            }
            String name = sections.get(index).title().getString();
            if (font.width(name) > LIST_W - 6) {
                name = font.plainSubstrByWidth(name, LIST_W - 6 - font.width("..")) + "..";
            }
            g.drawString(font, name, x, rowY + 1, index == section ? INK : INK_LIGHT, false);
        }
        if (listScroll > 0) {
            g.drawString(font, "^", x + LIST_W - 10, y - 9, INK_LIGHT, false);
        }
        if (listScroll + listRows() < sections.size()) {
            g.drawString(font, "v", x + LIST_W - 10, y + listRows() * ROW_H, INK_LIGHT, false);
        }
    }

    /** The pieces of the chosen section; returns the item under the mouse, if any. */
    @Nullable
    private ItemStack renderGrid(GuiGraphics g, int mouseX, int mouseY) {
        List<CatalogEntries.Entry> entries = sections.get(section).entries();
        int x = gridX();
        int y = top() + 20;
        ItemStack hovered = null;
        for (int i = 0; i < GRID_ROWS * COLS; i++) {
            int index = gridScroll * COLS + i;
            if (index >= entries.size()) {
                break;
            }
            CatalogEntries.Entry entry = entries.get(index);
            int sx = x + (i % COLS) * SLOT;
            int sy = y + (i / COLS) * SLOT;
            boolean over = mouseX >= sx && mouseX < sx + SLOT && mouseY >= sy && mouseY < sy + SLOT;
            g.fill(sx, sy, sx + SLOT - 1, sy + SLOT - 1, entry == selected ? HIGHLIGHT : over ? PAPER_DARK : 0xFFEADFC4);
            ItemStack stack = entry.variants().get(cycle(entry.variants().size()));
            g.renderItem(stack, sx + 1, sy + 1);
            if (over) {
                hovered = stack;
            }
        }
        int pages = Mth.positiveCeilDiv(entries.size(), COLS) - GRID_ROWS;
        if (pages > 0) {
            String scroll = (gridScroll + 1) + "/" + (pages + 1);
            g.drawString(font, scroll, x + COLS * SLOT - font.width(scroll), top() + HEIGHT - 11, INK_LIGHT, false);
        }
        g.drawString(font, Component.translatable("catalog.reikos_furniture.pieces", entries.size()), x, top() + HEIGHT - 11,
                INK_LIGHT, false);
        return hovered;
    }

    /** The chosen piece: its name, recipe and workbench cost; returns the ingredient under the mouse. */
    @Nullable
    private ItemStack renderDetail(GuiGraphics g, int mouseX, int mouseY) {
        int x = detailX();
        int y = top() + 20;
        int w = left() + WIDTH - 6 - x;
        if (selected == null) {
            lines(g, Component.translatable("catalog.reikos_furniture.hint"), x, y, w, INK_LIGHT);
            return null;
        }
        ItemStack stack = selected.variants().get(variant % selected.variants().size());
        g.renderItem(stack, x, y);
        for (FormattedCharSequence line : font.split(stack.getHoverName(), w - 20)) {
            g.drawString(font, line, x + 20, y + 1, INK, false);
            y += 10;
        }
        y = Math.max(y + 2, top() + 40);
        if (stack.getItem() instanceof FurnitureBlockItem furniture && furniture.room() != null) {
            y = lines(g, Component.translatable(furniture.room().translationKey()), x, y, w, INK_LIGHT);
            if (furniture.style() != null) {
                y = lines(g, Component.translatable(furniture.style().translationKey()), x, y, w, 0xFF9A6A00);
            }
        }
        if (selected.variants().size() > 1) {
            y = lines(g, Component.translatable("catalog.reikos_furniture.variants", variant % selected.variants().size() + 1,
                    selected.variants().size()), x, y, w, INK_LIGHT);
        }
        y += 4;
        ItemStack hovered = null;
        Recipe recipe = recipeFor(stack);
        if (recipe != null) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int sx = x + col * SLOT;
                    int sy = y + row * SLOT;
                    g.fill(sx, sy, sx + SLOT - 1, sy + SLOT - 1, 0xFFEADFC4);
                    Ingredient ingredient = recipe.grid().get(row * 3 + col);
                    ItemStack[] options = ingredient.getItems();
                    if (options.length > 0) {
                        ItemStack shown = options[cycle(options.length)];
                        g.renderItem(shown, sx + 1, sy + 1);
                        if (mouseX >= sx && mouseX < sx + SLOT && mouseY >= sy && mouseY < sy + SLOT) {
                            hovered = shown;
                        }
                    }
                }
            }
            g.drawString(font, "→", x + 3 * SLOT + 3, y + SLOT + 5, INK, false);
            g.renderItem(recipe.result(), x + 3 * SLOT + 12, y + SLOT + 1);
            g.renderItemDecorations(font, recipe.result(), x + 3 * SLOT + 12, y + SLOT + 1);
            y += 3 * SLOT + 3;
            if (recipe.note() != null) {
                y = lines(g, recipe.note(), x, y, w, INK_LIGHT);
            }
        } else {
            y = lines(g, Component.translatable("catalog.reikos_furniture.no_recipe"), x, y, w, INK_LIGHT) + 2;
        }
        Integer cost = carpentryCost(stack);
        if (cost != null) {
            lines(g, Component.translatable("catalog.reikos_furniture.workbench", cost), x, y, w, INK);
        }
        return hovered;
    }

    /** Draws text wrapped to {@code width}; returns the y below it. */
    private int lines(GuiGraphics g, Component text, int x, int y, int width, int color) {
        for (FormattedCharSequence line : font.split(text, width)) {
            g.drawString(font, line, x, y, color, false);
            y += 10;
        }
        return y;
    }

    /** A recipe laid out on a 3x3 grid, with an optional note (where it is made). */
    private record Recipe(List<Ingredient> grid, ItemStack result, @Nullable Component note) {
    }

    @Nullable
    private Recipe recipeFor(ItemStack stack) {
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        RecipeManager recipes = minecraft.level.getRecipeManager();
        RegistryAccess access = minecraft.level.registryAccess();
        for (RecipeHolder<CraftingRecipe> holder : recipes.getAllRecipesFor(RecipeType.CRAFTING)) {
            CraftingRecipe recipe = holder.value();
            if (recipe.isSpecial() || !recipe.getResultItem(access).is(stack.getItem())) {
                continue;
            }
            List<Ingredient> grid = emptyGrid();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            if (recipe instanceof ShapedRecipe shaped) {
                for (int row = 0; row < shaped.getHeight(); row++) {
                    for (int col = 0; col < shaped.getWidth(); col++) {
                        grid.set(row * 3 + col, ingredients.get(row * shaped.getWidth() + col));
                    }
                }
            } else {
                for (int i = 0; i < Math.min(9, ingredients.size()); i++) {
                    grid.set(i, ingredients.get(i));
                }
            }
            return new Recipe(grid, recipe.getResultItem(access), null);
        }
        for (RecipeHolder<ApplianceRecipe> holder : recipes.getAllRecipesFor(ModRecipes.APPLIANCE.get())) {
            ApplianceRecipe recipe = holder.value();
            if (recipe.result().is(stack.getItem())) {
                List<Ingredient> grid = emptyGrid();
                for (int i = 0; i < Math.min(9, recipe.ingredients().size()); i++) {
                    grid.set(i, recipe.ingredients().get(i));
                }
                Block appliance = BuiltInRegistries.BLOCK.get(ReikosFurniture.id(recipe.appliance()));
                Component where = appliance == Blocks.AIR ? Component.literal(recipe.appliance()) : appliance.getName();
                return new Recipe(grid, recipe.result(), Component.translatable("catalog.reikos_furniture.made_in", where));
            }
        }
        return null;
    }

    private static List<Ingredient> emptyGrid() {
        List<Ingredient> grid = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            grid.add(Ingredient.EMPTY);
        }
        return grid;
    }

    @Nullable
    private Integer carpentryCost(ItemStack stack) {
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        for (RecipeHolder<CarpentryRecipe> holder : minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipes.CARPENTRY.get())) {
            if (holder.value().result().is(stack.getItem())) {
                return holder.value().count();
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = left() + 6;
        int y = top() + 20;
        if (mouseX >= x && mouseX < x + LIST_W && mouseY >= y && mouseY < y + listRows() * ROW_H) {
            int index = listScroll + (int) ((mouseY - y) / ROW_H);
            if (index < sections.size()) {
                section = index;
                gridScroll = 0;
                return true;
            }
        }
        int gx = gridX();
        if (mouseX >= gx && mouseX < gx + COLS * SLOT && mouseY >= y && mouseY < y + GRID_ROWS * SLOT) {
            int index = gridScroll * COLS + (int) ((mouseY - y) / SLOT) * COLS + (int) ((mouseX - gx) / SLOT);
            List<CatalogEntries.Entry> entries = sections.get(section).entries();
            if (index < entries.size()) {
                selected = entries.get(index);
                variant = cycle(selected.variants().size());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int step = scrollY > 0 ? -1 : scrollY < 0 ? 1 : 0;
        if (mouseX < gridX() - 4) {
            listScroll = Mth.clamp(listScroll + step, 0, Math.max(0, sections.size() - listRows()));
        } else if (mouseX < detailX() - 4) {
            int rows = Mth.positiveCeilDiv(sections.get(section).entries().size(), COLS);
            gridScroll = Mth.clamp(gridScroll + step, 0, Math.max(0, rows - GRID_ROWS));
        } else if (selected != null) {
            variant = Math.floorMod(variant + step, selected.variants().size());
        }
        return true;
    }
}
