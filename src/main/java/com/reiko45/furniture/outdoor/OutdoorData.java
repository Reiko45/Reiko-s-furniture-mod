package com.reiko45.furniture.outdoor;

import static com.reiko45.furniture.outdoor.OutdoorFurniture.GARDEN_BENCH;
import static com.reiko45.furniture.outdoor.OutdoorFurniture.GRILL;
import static com.reiko45.furniture.outdoor.OutdoorFurniture.PATIO_CHAIR;
import static com.reiko45.furniture.outdoor.OutdoorFurniture.PATIO_TABLE;
import static com.reiko45.furniture.outdoor.OutdoorFurniture.PLANTER_BOX;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Models and recipes for the outdoor section. P = planks, L = log, S = wooden slab, D = dirt, I = iron ingot, C = coal. */
public class OutdoorData implements RoomData {
    private static final Room ROOM = Room.OUTDOOR;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodTable(PATIO_TABLE, "patio_table");
        models.woodFacing(PATIO_CHAIR, "patio_chair");
        models.woodFacing(GARDEN_BENCH, "garden_bench");
        models.woodSimple(PLANTER_BOX, "planter_box");
        models.facingLitBlock(GRILL.get(), models.existingTemplate(ROOM, "grill"), models.existingTemplate(ROOM, "grill_on"));
        models.blockItem(GRILL.get(), models.template(ROOM, "grill"));
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, PATIO_TABLE, 1, wood -> Map.of(), "SSS", "L L", "L L");
        recipes.woodShaped(output, PATIO_CHAIR, 2, wood -> Map.of(), "S  ", "SSS", "L L");
        recipes.woodShaped(output, GARDEN_BENCH, 1, wood -> Map.of(), "S S", "SSS", "L L");
        recipes.woodShaped(output, PLANTER_BOX, 2, wood -> Map.of('D', Ingredient.of(Items.DIRT)), "P P", "PDP", "PPP");
        recipes.shaped(GRILL.get(), 1, "III", "ICI", "I I")
                .define('I', Items.IRON_INGOT).define('C', ItemTags.COALS)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
    }
}
