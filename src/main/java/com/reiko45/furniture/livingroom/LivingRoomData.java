package com.reiko45.furniture.livingroom;

import static com.reiko45.furniture.livingroom.LivingRoomFurniture.BOOKSHELF;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.COFFEE_TABLE;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.FLOOR_LAMP;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.SOFA;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.TV;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.TV_STAND;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models and recipes for the living room. P = planks, L = log, S = wooden slab, C = chest, B = book,
 * W = wool, T = torch, I = iron ingot, G = glass pane, R = redstone.
 */
public class LivingRoomData implements RoomData {
    private static final Room ROOM = Room.LIVING_ROOM;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (DyeColor color : DyeColor.values()) {
            sofa(models, SOFA.get(color).get(), color);
            models.woolItem(SOFA, color, "sofa_item");
        }
        models.woodTable(COFFEE_TABLE, "coffee_table");
        models.woodFacing(BOOKSHELF, "bookshelf");
        models.woodFacing(TV_STAND, "tv_stand");
        models.woodTallLit(FLOOR_LAMP, "floor_lamp");

        models.facingLitBlock(TV.get(), models.existingTemplate(ROOM, "tv"), models.existingTemplate(ROOM, "tv_on"));
        models.blockItem(TV.get(), models.template(ROOM, "tv"));
    }

    /** The base is always shown; each armrest only on the open ends of a long sofa. */
    private static void sofa(FurnitureBlockStateProvider models, Block block, DyeColor color) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile base = models.woolModel(name + "_base", models.template(ROOM, "sofa_base"), color);
        ModelFile left = models.woolModel(name + "_arm_left", models.template(ROOM, "sofa_arm_left"), color);
        ModelFile right = models.woolModel(name + "_arm_right", models.template(ROOM, "sofa_arm_right"), color);

        var builder = models.getMultipartBuilder(block);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int y = FurnitureBlockStateProvider.yRot(facing);
            builder.part().modelFile(base).rotationY(y).addModel()
                    .condition(FurnitureBlock.FACING, facing).end();
            builder.part().modelFile(left).rotationY(y).addModel()
                    .condition(FurnitureBlock.FACING, facing)
                    .condition(SofaBlock.PART, SofaBlock.Part.SINGLE, SofaBlock.Part.LEFT).end();
            builder.part().modelFile(right).rotationY(y).addModel()
                    .condition(FurnitureBlock.FACING, facing)
                    .condition(SofaBlock.PART, SofaBlock.Part.SINGLE, SofaBlock.Part.RIGHT).end();
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(SOFA.get(color).get(), 2, "W W", "WWW", "P P")
                    .define('W', wool)
                    .define('P', ItemTags.PLANKS)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool))
                    .save(output);
        }
        recipes.woodShaped(output, COFFEE_TABLE, 1, wood -> Map.of(), "SSS", "L L");
        recipes.woodShaped(output, BOOKSHELF, 1,
                wood -> Map.of('B', Ingredient.of(Items.BOOK), 'C', Ingredient.of(Items.CHEST)), "PPP", "BCB", "PPP");
        recipes.woodShaped(output, TV_STAND, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "SSS", "PCP", "PPP");
        recipes.woodShaped(output, FLOOR_LAMP, 1,
                wood -> Map.of('W', Ingredient.of(Items.WHITE_WOOL), 'T', Ingredient.of(Items.TORCH)), "W", "T", "L");
        recipes.shaped(TV.get(), 1, "III", "IGI", "IRI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('R', Items.REDSTONE)
                .unlockedBy("has_redstone", FurnitureRecipeProvider.hasItem(Items.REDSTONE))
                .save(output);
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        // Bookshelves power enchanting tables, like the vanilla bookshelf.
        BOOKSHELF.all().forEach(block -> tags.tagFor(BlockTags.ENCHANTMENT_POWER_PROVIDER).add(block.get()));
    }
}
