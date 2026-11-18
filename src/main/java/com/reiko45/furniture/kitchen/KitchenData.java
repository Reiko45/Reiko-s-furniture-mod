package com.reiko45.furniture.kitchen;

import static com.reiko45.furniture.kitchen.KitchenFurniture.BLENDER;
import static com.reiko45.furniture.kitchen.KitchenFurniture.COFFEE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.COFFEE_MACHINE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.COUNTER;
import static com.reiko45.furniture.kitchen.KitchenFurniture.FRUIT_SMOOTHIE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.HOT_COCOA;
import static com.reiko45.furniture.kitchen.KitchenFurniture.TOAST;
import static com.reiko45.furniture.kitchen.KitchenFurniture.TOASTER;
import static com.reiko45.furniture.kitchen.KitchenFurniture.CUTTING_BOARD;
import static com.reiko45.furniture.kitchen.KitchenFurniture.PENDANT_LAMP;
import static com.reiko45.furniture.kitchen.KitchenFurniture.TRASH_CAN;
import static com.reiko45.furniture.kitchen.KitchenFurniture.FRIDGE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.GLASS_UPPER_CABINET;
import static com.reiko45.furniture.kitchen.KitchenFurniture.KITCHEN_ISLAND;
import static com.reiko45.furniture.kitchen.KitchenFurniture.MICROWAVE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.SINK;
import static com.reiko45.furniture.kitchen.KitchenFurniture.STOVE;
import static com.reiko45.furniture.kitchen.KitchenFurniture.UPPER_CABINET;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import net.minecraft.world.item.ItemStack;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.core.FurnitureWood;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
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
        models.woodFacing(CUTTING_BOARD, "cutting_board");
        models.colorFacingLit(PENDANT_LAMP, "pendant_lamp");
        models.singleFacingOpenable(TRASH_CAN.get(), ROOM, "trash_can");
        for (var appliance : List.of(COFFEE_MACHINE, TOASTER, BLENDER)) {
            String name = appliance.getId().getPath();
            ModelFile off = models.existingTemplate(ROOM, name);
            ModelFile on = models.existingTemplate(ROOM, name + "_on");
            models.facingBlock(appliance.get(), state -> state.getValue(ApplianceBlock.RUNNING) ? on : off);
            models.blockItem(appliance.get(), models.template(ROOM, name));
        }
        for (var food : List.of(COFFEE, HOT_COCOA, FRUIT_SMOOTHIE, TOAST)) {
            models.itemModels().basicItem(food.get());
        }

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
        recipes.woodShaped(output, CUTTING_BOARD, 2, wood -> Map.of(), "PS");
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(PENDANT_LAMP.get(color).get(), 1, " C ", "WLW")
                    .define('C', Items.CHAIN).define('W', wool).define('L', Items.GLOWSTONE_DUST)
                    .unlockedBy("has_glowstone_dust", FurnitureRecipeProvider.hasItem(Items.GLOWSTONE_DUST)).save(output);
        }
        recipes.shaped(TRASH_CAN.get(), 1, "INI", "I I", "III")
                .define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        appliances(output);
        recipes.shaped(COFFEE_MACHINE.get(), 1, "III", "IBG", "III")
                .define('I', Items.IRON_INGOT).define('B', Items.BUCKET).define('G', Items.GLASS_PANE)
                .unlockedBy("has_cocoa_beans", FurnitureRecipeProvider.hasItem(Items.COCOA_BEANS)).save(output);
        recipes.shaped(TOASTER.get(), 1, "I I", "IRI")
                .define('I', Items.IRON_INGOT).define('R', Items.REDSTONE)
                .unlockedBy("has_bread", FurnitureRecipeProvider.hasItem(Items.BREAD)).save(output);
        recipes.shaped(BLENDER.get(), 1, "G G", "G G", "IRI")
                .define('G', Items.GLASS_PANE).define('I', Items.IRON_INGOT).define('R', Items.REDSTONE)
                .unlockedBy("has_glass_pane", FurnitureRecipeProvider.hasItem(Items.GLASS_PANE)).save(output);
        recipes.shaped(FRIDGE.get(), 1, "III", "ICI", "III")
                .define('I', Items.IRON_INGOT).define('C', Items.CHEST)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT))
                .save(output);
    }

    /** What the coffee machine, toaster and blender make. */
    private static void appliances(RecipeOutput output) {
        Ingredient bottle = Ingredient.of(Items.GLASS_BOTTLE);
        Ingredient fruit = Ingredient.of(Items.APPLE, Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.MELON_SLICE);
        appliance(output, "coffee", ApplianceType.COFFEE_MACHINE, COFFEE.get(), 100, Ingredient.of(Items.COCOA_BEANS), bottle);
        appliance(output, "hot_cocoa", ApplianceType.COFFEE_MACHINE, HOT_COCOA.get(), 120,
                Ingredient.of(Items.COCOA_BEANS), Ingredient.of(Items.MILK_BUCKET), bottle);
        appliance(output, "toast", ApplianceType.TOASTER, TOAST.get(), 80, Ingredient.of(Items.BREAD));
        appliance(output, "fruit_smoothie", ApplianceType.BLENDER, FRUIT_SMOOTHIE.get(), 100, fruit, fruit, bottle);
    }

    private static void appliance(RecipeOutput output, String name, ApplianceType type, Item result, int time,
                                  Ingredient... ingredients) {
        output.accept(ReikosFurniture.id("appliance/" + name),
                new ApplianceRecipe(type.id(), List.of(ingredients), new ItemStack(result), time), null);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept(COFFEE.get().getDescriptionId(), "Coffee");
        add.accept(HOT_COCOA.get().getDescriptionId(), "Hot Cocoa");
        add.accept(FRUIT_SMOOTHIE.get().getDescriptionId(), "Fruit Smoothie");
        add.accept(TOAST.get().getDescriptionId(), "Toast");
        add.accept("container.reikos_furniture.stove", "Stove");
        add.accept("container.reikos_furniture.microwave", "Microwave");
    }
}
