package com.reiko45.furniture.laundry;

import static com.reiko45.furniture.laundry.LaundryFurniture.DRYER;
import static com.reiko45.furniture.laundry.LaundryFurniture.LAUNDRY_BASKET;
import static com.reiko45.furniture.laundry.LaundryFurniture.WASHING_MACHINE;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models and recipes for the laundry room. I = iron ingot, G = glass pane, B = bucket, F = furnace, S = wooden slab. */
public class LaundryData implements RoomData {
    private static final Room ROOM = Room.LAUNDRY;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        machine(models, WASHING_MACHINE.get(), "washing_machine");
        machine(models, DRYER.get(), "dryer");
        models.woodFacing(LAUNDRY_BASKET, "laundry_basket");
    }

    /** The machine shows its spinning drum while {@link LaundryMachineBlock#RUNNING}. */
    private static void machine(FurnitureBlockStateProvider models, Block block, String template) {
        ModelFile off = models.existingTemplate(ROOM, template);
        ModelFile on = models.existingTemplate(ROOM, template + "_on");
        models.facingBlock(block, state -> state.getValue(LaundryMachineBlock.RUNNING) ? on : off, FurnitureStorageBlock.OPEN);
        models.blockItem(block, models.template(ROOM, template));
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.shaped(WASHING_MACHINE.get(), 1, "III", "IGI", "IBI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('B', Items.BUCKET)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        recipes.shaped(DRYER.get(), 1, "III", "IGI", "IFI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('F', Items.FURNACE)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        recipes.woodShaped(output, LAUNDRY_BASKET, 1, wood -> Map.of(), "S S", "SSS");
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("message.reikos_furniture.laundry_busy", "Wait for the cycle to finish");
    }
}
