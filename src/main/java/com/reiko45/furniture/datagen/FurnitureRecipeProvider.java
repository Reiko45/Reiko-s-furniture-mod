package com.reiko45.furniture.datagen;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.WoodSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Crafting recipes. In patterns, rooms usually use {@code P} for the wood's planks, {@code L} for its
 * log and {@code S} for its slab; see {@link #woodKeys}.
 */
public class FurnitureRecipeProvider extends RecipeProvider {
    public FurnitureRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        DataGenerators.ROOMS.forEach(room -> room.recipes(this, output));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike item) {
        return has(item);
    }

    /** Standard keys for a wood type: P = planks, L = log, S = slab. */
    public static Map<Character, Ingredient> woodKeys(FurnitureWood wood) {
        return Map.of(
                'P', Ingredient.of(wood.planks()),
                'L', Ingredient.of(wood.log()),
                'S', Ingredient.of(wood.slab()));
    }

    /**
     * One shaped recipe per wood type. {@code extraKeys} adds or overrides keys for a wood type,
     * on top of {@link #woodKeys}. Only keys used in the pattern are added to the recipe.
     */
    public void woodShaped(RecipeOutput output, WoodSet<?> set, int count,
                           Function<FurnitureWood, Map<Character, Ingredient>> extraKeys, String... pattern) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, set.get(wood).get(), count);
            for (String row : pattern) {
                builder.pattern(row);
            }
            Map<Character, Ingredient> keys = new java.util.HashMap<>(woodKeys(wood));
            keys.putAll(extraKeys.apply(wood));
            String joined = String.join("", pattern);
            keys.forEach((key, ingredient) -> {
                if (joined.indexOf(key) >= 0) {
                    builder.define(key, ingredient);
                }
            });
            builder.unlockedBy("has_planks", has(wood.planks())).save(output);
        }
    }

    /** A single shaped recipe. {@code keys} alternates characters and ingredients. */
    public ShapedRecipeBuilder shaped(ItemLike result, int count, String... pattern) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result, count);
        for (String row : pattern) {
            builder.pattern(row);
        }
        return builder;
    }
}
