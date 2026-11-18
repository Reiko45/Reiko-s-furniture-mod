package com.reiko45.furniture.dining;

import static com.reiko45.furniture.dining.DiningFurniture.BENCH;
import static com.reiko45.furniture.dining.DiningFurniture.CHANDELIER;
import static com.reiko45.furniture.dining.DiningFurniture.PLATE;
import static com.reiko45.furniture.dining.DiningFurniture.DINING_CHAIR;
import static com.reiko45.furniture.dining.DiningFurniture.DINING_TABLE;
import static com.reiko45.furniture.dining.DiningFurniture.GLASS_CUPBOARD;
import static com.reiko45.furniture.dining.DiningFurniture.SIDEBOARD;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Models and recipes for the dining room. P = planks, L = log, C = chest, G = glass pane. */
public class DiningData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodTable(DINING_TABLE, "dining_table");
        models.woodFacing(DINING_CHAIR, "dining_chair");
        models.woodFacing(BENCH, "bench");
        models.woodFacingOpenable(SIDEBOARD, "sideboard");
        models.woodTallOpenable(GLASS_CUPBOARD, "glass_cupboard");
        models.singleFacing(PLATE.get(), Room.DINING_ROOM, "plate");
        models.singleFacingLit(CHANDELIER.get(), Room.DINING_ROOM, "chandelier");
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, DINING_TABLE, 1, wood -> Map.of(), "PPP", "L L", "L L");
        recipes.woodShaped(output, DINING_CHAIR, 2, wood -> Map.of(), "P  ", "PPP", "L L");
        recipes.woodShaped(output, BENCH, 2, wood -> Map.of(), "PPP", "L L");
        recipes.woodShaped(output, SIDEBOARD, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "PPP", "PCP", "L L");
        recipes.shaped(PLATE.get(), 4, "QQ")
                .define('Q', Items.WHITE_TERRACOTTA)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.WHITE_TERRACOTTA)).save(output);
        recipes.shaped(CHANDELIER.get(), 1, " N ", "GNG", "T T")
                .define('N', Items.CHAIN).define('G', Items.GOLD_INGOT).define('T', ItemTags.CANDLES)
                .unlockedBy("has_gold_ingot", FurnitureRecipeProvider.hasItem(Items.GOLD_INGOT)).save(output);
        recipes.woodShaped(output, GLASS_CUPBOARD, 1,
                wood -> Map.of('C', Ingredient.of(Items.CHEST), 'G', Ingredient.of(Items.GLASS_PANE)), "PGP", "PGP", "PCP");
    }
}
