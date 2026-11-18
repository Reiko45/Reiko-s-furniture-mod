package com.reiko45.furniture.garage;

import static com.reiko45.furniture.garage.GarageFurniture.METAL_SHELVING;
import static com.reiko45.furniture.garage.GarageFurniture.OIL_DRUM;
import static com.reiko45.furniture.garage.GarageFurniture.PEGBOARD;
import static com.reiko45.furniture.garage.GarageFurniture.TOOLBOX;
import static com.reiko45.furniture.garage.GarageFurniture.WORKSHOP_BENCH;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models and recipes for the garage. P = planks, S = wooden slab, I = iron ingot, N = iron nugget, D = dye. */
public class GarageData implements RoomData {
    private static final Room ROOM = Room.GARAGE;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacing(PEGBOARD, "pegboard");
        models.woodFacing(WORKSHOP_BENCH, "workshop_bench");
        painted(models, TOOLBOX, "toolbox");
        painted(models, OIL_DRUM, "oil_drum");
        models.singleFacingOpenable(METAL_SHELVING.get(), ROOM, "metal_shelving");
    }

    /** Painted metal: the {@code #paint} texture is the color's concrete. */
    private static void painted(FurnitureBlockStateProvider models, ColorSet<? extends FurnitureStorageBlock> set, String template) {
        for (DyeColor color : DyeColor.values()) {
            Block block = set.get(color).get();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ResourceLocation paint = ResourceLocation.withDefaultNamespace("block/" + color.getName() + "_concrete");
            ModelFile closed = models.models().withExistingParent(name, models.template(ROOM, template)).texture("paint", paint);
            ModelFile open = models.models().withExistingParent(name + "_open", models.template(ROOM, template + "_open")).texture("paint", paint);
            models.facingBlock(block, state -> state.getValue(FurnitureStorageBlock.OPEN) ? open : closed);
            models.blockItem(block, ReikosFurniture.id("block/" + name));
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, PEGBOARD, 2, wood -> Map.of('N', Ingredient.of(Items.IRON_NUGGET)), "PNP", "PPP");
        recipes.woodShaped(output, WORKSHOP_BENCH, 1, wood -> Map.of('C', Ingredient.of(Items.CRAFTING_TABLE)), "SCS", "P P");
        for (DyeColor color : DyeColor.values()) {
            recipes.shaped(TOOLBOX.get(color).get(), 1, "INI", "IDI")
                    .define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET).define('D', DyeItem.byColor(color))
                    .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
            recipes.shaped(OIL_DRUM.get(color).get(), 1, "IDI", "I I", "III")
                    .define('I', Items.IRON_INGOT).define('D', DyeItem.byColor(color))
                    .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        }
        recipes.shaped(METAL_SHELVING.get(), 1, "I I", "III", "I I")
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
    }
}
