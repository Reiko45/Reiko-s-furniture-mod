package com.reiko45.furniture.datagen;

import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeOutput;

/**
 * Data generation for one room section: models, recipes and any extra tags or translations.
 * Names, loot tables and mining tags are generated automatically for every block.
 */
public interface RoomData {
    void models(FurnitureBlockStateProvider models);

    void recipes(FurnitureRecipeProvider recipes, RecipeOutput output);

    default void tags(FurnitureBlockTagsProvider tags) {
    }

    /** Extra translations as (key, English text). */
    default void translations(BiConsumer<String, String> add) {
    }
}
