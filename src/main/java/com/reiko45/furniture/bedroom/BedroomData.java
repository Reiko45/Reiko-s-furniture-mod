package com.reiko45.furniture.bedroom;

import static com.reiko45.furniture.bedroom.BedroomFurniture.BED;
import static com.reiko45.furniture.bedroom.BedroomFurniture.DESK;
import static com.reiko45.furniture.bedroom.BedroomFurniture.MIRROR;
import static com.reiko45.furniture.bedroom.BedroomFurniture.VANITY;
import static com.reiko45.furniture.bedroom.BedroomFurniture.DRESSER;
import static com.reiko45.furniture.bedroom.BedroomFurniture.NIGHTSTAND;
import static com.reiko45.furniture.bedroom.BedroomFurniture.TABLE_LAMP;
import static com.reiko45.furniture.bedroom.BedroomFurniture.WARDROBE;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Models and recipes for the bedroom. P = planks, L = log, C = chest, W = wool, T = torch, G = glass pane. */
public class BedroomData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacingOpenable(NIGHTSTAND, "nightstand");
        models.woodFacingOpenable(DRESSER, "dresser");
        models.woodTallOpenable(WARDROBE, "wardrobe");
        models.woodFacing(DESK, "desk");
        models.woodFacingLit(TABLE_LAMP, "table_lamp");
        beds(models);
        models.woodFacing(MIRROR, "mirror");
        models.woodFacingOpenable(VANITY, "vanity");
    }

    /** Beds have a head and a foot model for every wood and blanket color. */
    private static void beds(FurnitureBlockStateProvider models) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            FurnitureBedBlock block = BED.get(wood).get();
            String name = wood.id() + "_bed";
            Map<DyeColor, ModelFile> heads = new EnumMap<>(DyeColor.class);
            Map<DyeColor, ModelFile> feet = new EnumMap<>(DyeColor.class);
            for (DyeColor color : DyeColor.values()) {
                heads.put(color, models.woodModel(name + "_" + color.getName() + "_head", models.template(Room.BEDROOM, "bed_head"), wood)
                        .texture("wool", FurnitureBlockStateProvider.woolTexture(color)));
                feet.put(color, models.woodModel(name + "_" + color.getName() + "_foot", models.template(Room.BEDROOM, "bed_foot"), wood)
                        .texture("wool", FurnitureBlockStateProvider.woolTexture(color)));
            }
            models.facingBlock(block, state -> (state.getValue(BedBlock.PART) == BedPart.HEAD ? heads : feet)
                    .get(state.getValue(FurnitureBedBlock.COLOR)), BedBlock.OCCUPIED);
            models.woodItem(block, Room.BEDROOM, "bed_item", wood);
        }
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
        recipes.woodShaped(output, BED, 1, wood -> Map.of('W', Ingredient.of(ItemTags.WOOL)), "WWW", "PPP", "L L");
        var glass = Map.of('G', Ingredient.of(Items.GLASS_PANE), 'C', Ingredient.of(Items.CHEST));
        recipes.woodShaped(output, MIRROR, 1, wood -> glass, "PPP", "PGP", "PPP");
        recipes.woodShaped(output, VANITY, 1, wood -> glass, "PGP", "PCP", "P P");
    }
}
