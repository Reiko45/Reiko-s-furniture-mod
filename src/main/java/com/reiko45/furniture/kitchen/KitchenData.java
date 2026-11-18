package com.reiko45.furniture.kitchen;

import static com.reiko45.furniture.kitchen.KitchenFurniture.COUNTER;
import static com.reiko45.furniture.kitchen.KitchenFurniture.FRIDGE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.GLASS_UPPER_CABINET;
import static com.reiko45.furniture.kitchen.KitchenFurniture.KITCHEN_ISLAND;
import static com.reiko45.furniture.kitchen.KitchenFurniture.MICROWAVE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.SINK;
import static com.reiko45.furniture.kitchen.KitchenFurniture.STOVE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.UPPER_CABINET;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.core.FurnitureWood;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Models and recipes for the kitchen. P = planks, S = wooden slab, C = chest, T = smooth stone slab,
 * I = iron ingot, B = bucket, F = furnace, G = glass pane, R = redstone.
 */
public class KitchenData implements RoomData {
    private static final Room ROOM = Room.KITCHEN;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        counters(models);
        models.woodFacingOpenable(UPPER_CABINET, "upper_cabinet");
        models.woodFacing(SINK, "sink");

        models.facingLitBlock(STOVE.get(), models.existingTemplate(ROOM, "stove"), models.existingTemplate(ROOM, "stove_on"));
        models.blockItem(STOVE.get(), models.template(ROOM, "stove"));

        models.facingLitBlock(MICROWAVE.get(), models.existingTemplate(ROOM, "microwave"), models.existingTemplate(ROOM, "microwave_on"));
        models.blockItem(MICROWAVE.get(), models.template(ROOM, "microwave"));
        models.woodFacingOpenable(KITCHEN_ISLAND, "kitchen_island");
        models.woodFacingOpenable(GLASS_UPPER_CABINET, "glass_upper_cabinet");

        models.tallOpenableBlock(FRIDGE.get(), models.existingTemplate(ROOM, "fridge_lower"), models.existingTemplate(ROOM, "fridge_upper"),
                models.existingTemplate(ROOM, "fridge_lower_open"), models.existingTemplate(ROOM, "fridge_upper_open"));
        models.blockItem(FRIDGE.get(), models.template(ROOM, "fridge_item"));
    }

    /** Counters have a model for every corner shape, closed and open. */
    private static void counters(FurnitureBlockStateProvider models) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            CounterBlock block = COUNTER.get(wood).get();
            String name = wood.id() + "_counter";
            Map<String, ModelFile> files = new HashMap<>();
            for (String variant : List.of("", "_open", "_inner", "_outer_left", "_outer_left_open",
                    "_outer_right", "_outer_right_open")) {
                files.put(variant, models.woodModel(name + variant, models.template(ROOM, "counter" + variant), wood));
            }
            models.facingBlock(block, state -> {
                boolean open = state.getValue(CounterBlock.OPEN);
                return switch (state.getValue(CounterBlock.SHAPE)) {
                    case STRAIGHT -> files.get(open ? "_open" : "");
                    case INNER -> files.get("_inner");
                    case OUTER_LEFT -> files.get(open ? "_outer_left_open" : "_outer_left");
                    case OUTER_RIGHT -> files.get(open ? "_outer_right_open" : "_outer_right");
                };
            });
            models.blockItem(block, models.modLoc("block/" + name));
        }
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
        recipes.woodShaped(output, KITCHEN_ISLAND, 1, wood -> chest, "TTT", "CPC", "PPP");
        recipes.woodShaped(output, GLASS_UPPER_CABINET, 1,
                wood -> Map.of('G', Ingredient.of(Items.GLASS_PANE), 'C', Ingredient.of(Items.CHEST)), "SSS", "GCG");
        recipes.shaped(MICROWAVE.get(), 1, "III", "GRI", "III")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('R', Items.REDSTONE)
                .unlockedBy("has_redstone", FurnitureRecipeProvider.hasItem(Items.REDSTONE))
                .save(output);
        recipes.shaped(FRIDGE.get(), 1, "III", "ICI", "III")
                .define('I', Items.IRON_INGOT).define('C', Items.CHEST)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT))
                .save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("container.reikos_furniture.stove", "Stove");
        add.accept("container.reikos_furniture.microwave", "Microwave");
    }
}
