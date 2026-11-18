package com.reiko45.furniture.kitchen;

import static com.reiko45.furniture.kitchen.KitchenFurniture.COUNTER;
import static com.reiko45.furniture.kitchen.KitchenFurniture.FRIDGE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.SINK;
import static com.reiko45.furniture.kitchen.KitchenFurniture.STOVE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.UPPER_CABINET;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Models and recipes for the kitchen. P = planks, S = wooden slab, C = chest, T = smooth stone slab,
 * I = iron ingot, B = bucket, F = furnace.
 */
public class KitchenData implements RoomData {
    private static final Room ROOM = Room.KITCHEN;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacingOpenable(COUNTER, "counter");
        models.woodFacingOpenable(UPPER_CABINET, "upper_cabinet");
        models.woodFacing(SINK, "sink");

        models.facingLitBlock(STOVE.get(), models.existingTemplate(ROOM, "stove"), models.existingTemplate(ROOM, "stove_on"));
        models.blockItem(STOVE.get(), models.template(ROOM, "stove"));

        models.tallOpenableBlock(FRIDGE.get(), models.existingTemplate(ROOM, "fridge_lower"), models.existingTemplate(ROOM, "fridge_upper"),
                models.existingTemplate(ROOM, "fridge_lower_open"), models.existingTemplate(ROOM, "fridge_upper_open"));
        models.blockItem(FRIDGE.get(), models.template(ROOM, "fridge_item"));
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        var chest = Map.of('C', Ingredient.of(Items.CHEST), 'T', Ingredient.of(Items.SMOOTH_STONE_SLAB));
        var sink = Map.of('I', Ingredient.of(Items.IRON_INGOT), 'B', Ingredient.of(Items.BUCKET));

        recipes.woodShaped(output, COUNTER, 1, wood -> chest, "TTT", "PCP", "PPP");
        recipes.woodShaped(output, UPPER_CABINET, 1, wood -> chest, "SSS", "PCP");
        recipes.woodShaped(output, SINK, 1, wood -> sink, "IBI", "PPP");

        recipes.shaped(STOVE.get(), 1, "III", "IFI", "III")
                .define('I', Items.IRON_INGOT).define('F', Items.FURNACE)
                .unlockedBy("has_furnace", FurnitureRecipeProvider.hasItem(Items.FURNACE))
                .save(output);
        recipes.shaped(FRIDGE.get(), 1, "III", "ICI", "III")
                .define('I', Items.IRON_INGOT).define('C', Items.CHEST)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT))
                .save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("container.reikos_furniture.stove", "Stove");
    }
}
