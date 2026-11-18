package com.reiko45.furniture.dining;

import static com.reiko45.furniture.dining.DiningFurniture.BENCH;
import static com.reiko45.furniture.dining.DiningFurniture.DINING_CHAIR;
import static com.reiko45.furniture.dining.DiningFurniture.DINING_TABLE;
import static com.reiko45.furniture.dining.DiningFurniture.SIDEBOARD;

import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Models and recipes for the dining room. P = planks, L = log, C = chest. */
public class DiningData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodTable(DINING_TABLE, "dining_table");
        models.woodFacing(DINING_CHAIR, "dining_chair");
        models.woodFacing(BENCH, "bench");
        models.woodFacing(SIDEBOARD, "sideboard");
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, DINING_TABLE, 1, wood -> Map.of(), "PPP", "L L", "L L");
        recipes.woodShaped(output, DINING_CHAIR, 2, wood -> Map.of(), "P  ", "PPP", "L L");
        recipes.woodShaped(output, BENCH, 2, wood -> Map.of(), "PPP", "L L");
        recipes.woodShaped(output, SIDEBOARD, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "PPP", "PCP", "L L");
    }
}
