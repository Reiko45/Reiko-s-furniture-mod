package com.reiko45.furniture.bathroom;

import static com.reiko45.furniture.bathroom.BathroomFurniture.BATHROOM_CABINET;
import static com.reiko45.furniture.bathroom.BathroomFurniture.BATHROOM_SINK;
import static com.reiko45.furniture.bathroom.BathroomFurniture.BATHTUB;
import static com.reiko45.furniture.bathroom.BathroomFurniture.SHOWER;
import static com.reiko45.furniture.bathroom.BathroomFurniture.TOILET;
import static com.reiko45.furniture.bathroom.BathroomFurniture.TOWEL_RACK;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models and recipes for the bathroom. Q = white terracotta (the "ceramic"), B = bucket, I = iron ingot,
 * G = glass pane, C = chest, N = iron nugget, W = wool.
 */
public class BathroomData implements RoomData {
    private static final Room ROOM = Room.BATHROOM;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        ModelFile toilet = models.existingTemplate(ROOM, "toilet");
        models.facingBlock(TOILET.get(), state -> toilet);
        models.blockItem(TOILET.get(), models.template(ROOM, "toilet"));

        bathtub(models);

        models.tallBlock(SHOWER.get(), models.existingTemplate(ROOM, "shower_lower"), models.existingTemplate(ROOM, "shower_upper"));
        models.blockItem(SHOWER.get(), models.template(ROOM, "shower_item"));

        ModelFile sink = models.existingTemplate(ROOM, "bathroom_sink");
        models.facingBlock(BATHROOM_SINK.get(), state -> sink);
        models.blockItem(BATHROOM_SINK.get(), models.template(ROOM, "bathroom_sink"));

        models.facingOpenableBlock(BATHROOM_CABINET.get(), models.existingTemplate(ROOM, "bathroom_cabinet"),
                models.existingTemplate(ROOM, "bathroom_cabinet_open"));
        models.blockItem(BATHROOM_CABINET.get(), models.template(ROOM, "bathroom_cabinet"));

        models.colorFacing(TOWEL_RACK, "towel_rack");
    }

    /** The tub is drawn from parts: the base, an end wall on each open end, and water when filled. */
    private static void bathtub(FurnitureBlockStateProvider models) {
        ModelFile base = models.existingTemplate(ROOM, "bathtub_base");
        ModelFile left = models.existingTemplate(ROOM, "bathtub_end_left");
        ModelFile right = models.existingTemplate(ROOM, "bathtub_end_right");
        ModelFile water = models.existingTemplate(ROOM, "bathtub_water");
        var builder = models.getMultipartBuilder(BATHTUB.get());
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int y = FurnitureBlockStateProvider.yRot(facing);
            builder.part().modelFile(base).rotationY(y).addModel().condition(FurnitureBlock.FACING, facing).end();
            builder.part().modelFile(left).rotationY(y).addModel().condition(FurnitureBlock.FACING, facing)
                    .condition(BathtubBlock.PART, BathtubBlock.Part.SINGLE, BathtubBlock.Part.LEFT).end();
            builder.part().modelFile(right).rotationY(y).addModel().condition(FurnitureBlock.FACING, facing)
                    .condition(BathtubBlock.PART, BathtubBlock.Part.SINGLE, BathtubBlock.Part.RIGHT).end();
            builder.part().modelFile(water).rotationY(y).addModel().condition(FurnitureBlock.FACING, facing)
                    .condition(BathtubBlock.FILLED, true).end();
        }
        models.blockItem(BATHTUB.get(), models.template(ROOM, "bathtub_item"));
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.shaped(TOILET.get(), 1, "Q  ", "QBQ", " Q ")
                .define('Q', Items.WHITE_TERRACOTTA).define('B', Items.BUCKET)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.WHITE_TERRACOTTA)).save(output);
        recipes.shaped(BATHTUB.get(), 1, "Q Q", "QQQ")
                .define('Q', Items.WHITE_TERRACOTTA)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.WHITE_TERRACOTTA)).save(output);
        recipes.shaped(SHOWER.get(), 1, " I ", "GBG", "QQQ")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('B', Items.BUCKET).define('Q', Items.WHITE_TERRACOTTA)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.WHITE_TERRACOTTA)).save(output);
        recipes.shaped(BATHROOM_SINK.get(), 1, "QBQ", " Q ", " Q ")
                .define('Q', Items.WHITE_TERRACOTTA).define('B', Items.BUCKET)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.WHITE_TERRACOTTA)).save(output);
        recipes.shaped(BATHROOM_CABINET.get(), 1, "QQQ", "QGQ", "QCQ")
                .define('Q', Items.WHITE_TERRACOTTA).define('G', Items.GLASS_PANE).define('C', Items.CHEST)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.WHITE_TERRACOTTA)).save(output);
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(TOWEL_RACK.get(color).get(), 1, "NNN", "WWW")
                    .define('N', Items.IRON_NUGGET).define('W', wool)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
    }
}
