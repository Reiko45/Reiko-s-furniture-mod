package com.reiko45.furniture.core.catalog;

import com.reiko45.furniture.core.advancement.FurnitureAdvancements;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

/** Item model, recipe and names for the furniture catalog, and the names of the advancements. */
public class CatalogData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.itemModels().basicItem(Catalog.CATALOG.get());
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Catalog.CATALOG.get())
                .requires(Items.BOOK)
                .requires(ItemTags.PLANKS)
                .unlockedBy("has_book", FurnitureRecipeProvider.hasItem(Items.BOOK))
                .save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("item.reikos_furniture.furniture_catalog", "Furniture Catalog");
        add.accept("item.reikos_furniture.furniture_catalog.tooltip", "Every piece of furniture, by room and style");
        add.accept("catalog.reikos_furniture.extras", "Tools & Food");
        add.accept("catalog.reikos_furniture.pieces", "%s pieces");
        add.accept("catalog.reikos_furniture.variants", "Variant %s of %s");
        add.accept("catalog.reikos_furniture.hint", "Pick a section on the left, then click a piece to see how it is made. Scroll over the recipe for its other woods and colors.");
        add.accept("catalog.reikos_furniture.no_recipe", "Not craftable");
        add.accept("catalog.reikos_furniture.made_in", "Made in: %s");
        add.accept("catalog.reikos_furniture.workbench", "Furniture Workbench: %s planks");
        add.accept("catalog.reikos_furniture.craft", "Craft");
        add.accept("catalog.reikos_furniture.craft_all", "Craft all");
        add.accept("catalog.reikos_furniture.missing", "You don't have the materials for this");
        FurnitureAdvancements.NAMES.forEach((id, text) -> {
            add.accept(FurnitureAdvancements.titleKey(id), text[0]);
            add.accept(FurnitureAdvancements.descriptionKey(id), text[1]);
        });
    }
}
