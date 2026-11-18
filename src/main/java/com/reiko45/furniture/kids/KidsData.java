package com.reiko45.furniture.kids;

import static com.reiko45.furniture.kids.KidsFurniture.BUNK_BED;
import static com.reiko45.furniture.kids.KidsFurniture.BUNK_BED_TOP;
import static com.reiko45.furniture.kids.KidsFurniture.KIDS_CHAIR;
import static com.reiko45.furniture.kids.KidsFurniture.KIDS_TABLE;
import static com.reiko45.furniture.kids.KidsFurniture.ROCKING_HORSE;
import static com.reiko45.furniture.kids.KidsFurniture.TOY_CHEST;

import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models and recipes for the kids' room. P = planks, S = wooden slab, C = chest, B = bed of the same wood. */
public class KidsData implements RoomData {
    private static final Room ROOM = Room.KIDS_ROOM;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacingOpenable(TOY_CHEST, "toy_chest");
        models.woodTable(KIDS_TABLE, "kids_table");
        models.woodFacing(KIDS_CHAIR, "kids_chair");
        models.woodFacing(ROCKING_HORSE, "rocking_horse");
        bunkBeds(models);
    }

    /** Lower bed and top bunk each have a head and a foot model for every wood and blanket color. */
    private static void bunkBeds(FurnitureBlockStateProvider models) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            String name = wood.id() + "_bunk_bed";
            Map<DyeColor, ModelFile[]> lower = new EnumMap<>(DyeColor.class);
            Map<DyeColor, ModelFile[]> top = new EnumMap<>(DyeColor.class);
            for (DyeColor color : DyeColor.values()) {
                lower.put(color, new ModelFile[] {
                        bunkModel(models, name, "lower_head", color, wood), bunkModel(models, name, "lower_foot", color, wood)});
                top.put(color, new ModelFile[] {
                        bunkModel(models, name, "top_head", color, wood), bunkModel(models, name, "top_foot", color, wood)});
            }
            models.facingBlock(BUNK_BED.get(wood).get(), state -> lower.get(state.getValue(FurnitureBedBlock.COLOR))
                    [state.getValue(BedBlock.PART) == BedPart.HEAD ? 0 : 1], BedBlock.OCCUPIED);
            models.facingBlock(BUNK_BED_TOP.get(wood).get(), state -> top.get(state.getValue(FurnitureBedBlock.COLOR))
                    [state.getValue(BedBlock.PART) == BedPart.HEAD ? 0 : 1]);
            models.woodItem(BUNK_BED.get(wood).get(), ROOM, "bunk_bed_item", wood);
        }
    }

    private static ModelFile bunkModel(FurnitureBlockStateProvider models, String name, String part, DyeColor color,
                                       FurnitureWood wood) {
        return models.woodModel(name + "_" + color.getName() + "_" + part, models.template(ROOM, "bunk_bed_" + part), wood)
                .texture("wool", FurnitureBlockStateProvider.woolTexture(color));
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, TOY_CHEST, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "PSP", "PCP", "PPP");
        recipes.woodShaped(output, KIDS_TABLE, 1, wood -> Map.of(), "SSS", "S S");
        recipes.woodShaped(output, KIDS_CHAIR, 2, wood -> Map.of(), "S  ", "SSS", "S S");
        recipes.woodShaped(output, ROCKING_HORSE, 1, wood -> Map.of(), "P  ", "PPP", "SSS");
        recipes.woodShaped(output, BUNK_BED, 1,
                wood -> Map.of('B', Ingredient.of(BedroomFurniture.BED.get(wood).get())), "B", "B");
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        BUNK_BED_TOP.all().forEach(top -> tags.tagFor(BlockTags.MINEABLE_WITH_AXE).add((Block) top.get()));
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            add.accept(BUNK_BED_TOP.get(wood).get().getDescriptionId(), wood.displayName() + " Bunk Bed");
        }
    }
}
