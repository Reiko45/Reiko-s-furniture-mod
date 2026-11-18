package com.reiko45.furniture.bedroom;

import static com.reiko45.furniture.bedroom.BedroomFurniture.DESK;
import static com.reiko45.furniture.bedroom.BedroomFurniture.DRESSER;
import static com.reiko45.furniture.bedroom.BedroomFurniture.NIGHTSTAND;
import static com.reiko45.furniture.bedroom.BedroomFurniture.TABLE_LAMP;
import static com.reiko45.furniture.bedroom.BedroomFurniture.WARDROBE;

import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Models and recipes for the bedroom. P = planks, L = log, C = chest, W = white wool, T = torch. */
public class BedroomData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacingOpenable(NIGHTSTAND, "nightstand");
        models.woodFacingOpenable(DRESSER, "dresser");
        models.woodTallOpenable(WARDROBE, "wardrobe");
        models.woodFacing(DESK, "desk");
        models.woodFacingLit(TABLE_LAMP, "table_lamp");
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        var chest = Map.of('C', Ingredient.of(Items.CHEST));
        var lamp = Map.of('W', Ingredient.of(Items.WHITE_WOOL), 'T', Ingredient.of(Items.TORCH));

        recipes.woodShaped(output, NIGHTSTAND, 1, wood -> chest, "PPP", "PCP");
        recipes.woodShaped(output, DRESSER, 1, wood -> chest, "PPP", "PCP", "PPP");
        recipes.woodShaped(output, WARDROBE, 1, wood -> chest, "PCP", "PPP", "PCP");
        recipes.woodShaped(output, DESK, 1, wood -> Map.of(), "PPP", "P P");
        recipes.woodShaped(output, TABLE_LAMP, 1, wood -> lamp, "W", "T", "P");
    }
}
