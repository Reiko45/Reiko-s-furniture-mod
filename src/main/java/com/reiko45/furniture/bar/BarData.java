package com.reiko45.furniture.bar;

import static com.reiko45.furniture.bar.BarFurniture.BAR_COUNTER;
import static com.reiko45.furniture.bar.BarFurniture.BAR_STOOL;
import static com.reiko45.furniture.bar.BarFurniture.CHALKBOARD;
import static com.reiko45.furniture.bar.BarFurniture.ESPRESSO;
import static com.reiko45.furniture.bar.BarFurniture.ESPRESSO_MACHINE;
import static com.reiko45.furniture.bar.BarFurniture.LATTE;
import static com.reiko45.furniture.bar.BarFurniture.WINE_RACK;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models and recipes for the home bar. P = planks, L = log, S = wooden slab, C = chest, W = wool,
 * I = iron ingot, N = iron nugget, B = glass bottle / bucket, G = glass pane, R = redstone, D = dye.
 */
public class BarData implements RoomData {
    private static final Room ROOM = Room.BAR;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            CounterBlock block = BAR_COUNTER.get(wood).get();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            Map<String, ModelFile> files = new HashMap<>();
            for (String variant : List.of("", "_inner", "_outer_left", "_outer_right")) {
                files.put(variant, models.woodModel(name + variant, models.template(ROOM, "bar_counter" + variant), wood));
            }
            // The storage doors are on the bartender's side, so opening doesn't change how it looks.
            models.facingBlock(block, state -> files.get(switch (state.getValue(CounterBlock.SHAPE)) {
                case STRAIGHT -> "";
                case INNER -> "_inner";
                case OUTER_LEFT -> "_outer_left";
                case OUTER_RIGHT -> "_outer_right";
            }), CounterBlock.OPEN);
            models.blockItem(block, models.modLoc("block/" + name));
        }
        models.colorFacing(BAR_STOOL, "bar_stool");
        models.woodFacing(WINE_RACK, "wine_rack");
        ModelFile off = models.existingTemplate(ROOM, "espresso_machine");
        ModelFile on = models.existingTemplate(ROOM, "espresso_machine_on");
        models.facingBlock(ESPRESSO_MACHINE.get(), state -> state.getValue(ApplianceBlock.RUNNING) ? on : off);
        models.blockItem(ESPRESSO_MACHINE.get(), models.template(ROOM, "espresso_machine"));
        // The chalkboard is drawn by the vanilla sign renderer; its block model only gives the break particles
        // (from a block texture: the sign texture isn't on the block atlas).
        models.simpleBlock(CHALKBOARD.get(), models.models().sign("chalkboard", ResourceLocation.withDefaultNamespace("block/spruce_planks")));
        models.itemModels().basicItem(CHALKBOARD.get().asItem());
        models.itemModels().basicItem(ESPRESSO.get());
        models.itemModels().basicItem(LATTE.get());
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, BAR_COUNTER, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "LLL", "PCP", "PPP");
        recipes.woodShaped(output, WINE_RACK, 1, wood -> Map.of('B', Ingredient.of(Items.GLASS_BOTTLE)), "PSP", "SBS", "PSP");
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(BAR_STOOL.get(color).get(), 1, "W", "I", "N")
                    .define('W', wool).define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
        recipes.shaped(ESPRESSO_MACHINE.get(), 1, "III", "IBG", "IRI")
                .define('I', Items.IRON_INGOT).define('B', Items.BUCKET).define('G', Items.GLASS_PANE).define('R', Items.REDSTONE)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        recipes.shaped(CHALKBOARD.get(), 1, "PPP", "PDP", "PPP")
                .define('P', net.minecraft.tags.ItemTags.PLANKS).define('D', Items.BLACK_DYE)
                .unlockedBy("has_black_dye", FurnitureRecipeProvider.hasItem(Items.BLACK_DYE)).save(output);

        Ingredient bottle = Ingredient.of(Items.GLASS_BOTTLE);
        Ingredient cocoa = Ingredient.of(Items.COCOA_BEANS);
        appliance(output, "espresso", ESPRESSO.get(), 80, cocoa, cocoa, bottle);
        appliance(output, "latte", LATTE.get(), 120, cocoa, Ingredient.of(Items.MILK_BUCKET), Ingredient.of(Items.SUGAR), bottle);
    }

    private static void appliance(RecipeOutput output, String name, Item result, int time, Ingredient... ingredients) {
        output.accept(ReikosFurniture.id("appliance/" + name),
                new ApplianceRecipe(ApplianceType.ESPRESSO_MACHINE.id(), List.of(ingredients), new ItemStack(result), time), null);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept(ESPRESSO.get().getDescriptionId(), "Espresso");
        add.accept(LATTE.get().getDescriptionId(), "Latte");
    }
}
