package com.reiko45.furniture.livingroom;

import static com.reiko45.furniture.livingroom.LivingRoomFurniture.ARMCHAIR;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.BLINDS;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.CEILING_LIGHT;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.WALL_CLOCK;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.WALL_SHELF;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.BOOKSHELF;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.CURTAINS;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.FIREPLACE;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.RUG;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.COFFEE_TABLE;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.FLOOR_LAMP;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.SOFA;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.TV;
import static com.reiko45.furniture.livingroom.LivingRoomFurniture.TV_STAND;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
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
        models.woodFacingOpenable(TV_STAND, "tv_stand");
        models.woodTallLit(FLOOR_LAMP, "floor_lamp");

        models.colorFacing(ARMCHAIR, "armchair");
        models.woodFacing(WALL_SHELF, "wall_shelf");
        models.singleFacingLit(CEILING_LIGHT.get(), ROOM, "ceiling_light");
        models.colorFacingOpenable(BLINDS, "blinds");
        models.woodFacing(WALL_CLOCK, "wall_clock");
        models.colorFacingOpenable(CURTAINS, "curtains");
        for (DyeColor color : DyeColor.values()) {
            rug(models, RUG.get(color).get(), color);
            models.woolItem(RUG, color, "rug_item");
        }
        models.facingLitBlock(FIREPLACE.get(), models.existingTemplate(ROOM, "fireplace"), models.existingTemplate(ROOM, "fireplace_on"));
        models.blockItem(FIREPLACE.get(), models.template(ROOM, "fireplace"));

        ModelFile tvOff = models.existingTemplate(ROOM, "tv");
        ModelFile[] tvChannels = new ModelFile[TvBlock.CHANNELS + 1];
        for (int channel = 1; channel <= TvBlock.CHANNELS; channel++) {
            tvChannels[channel] = models.existingTemplate(ROOM, "tv_on_" + channel);
        }
        models.facingBlock(TV.get(), state -> state.getValue(TvBlock.LIT) ? tvChannels[state.getValue(TvBlock.CHANNEL)] : tvOff);
        models.blockItem(TV.get(), models.template(ROOM, "tv"));
    }

    /** The base is always shown; each armrest only on the open ends; an extra backrest on corners. */
    private static void sofa(FurnitureBlockStateProvider models, Block block, DyeColor color) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile base = models.woolModel(name + "_base", models.template(ROOM, "sofa_base"), color);
        ModelFile left = models.woolModel(name + "_arm_left", models.template(ROOM, "sofa_arm_left"), color);
        ModelFile right = models.woolModel(name + "_arm_right", models.template(ROOM, "sofa_arm_right"), color);
        ModelFile cornerLeft = models.woolModel(name + "_corner_left", models.template(ROOM, "sofa_corner_left"), color);
        ModelFile cornerRight = models.woolModel(name + "_corner_right", models.template(ROOM, "sofa_corner_right"), color);

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
            builder.part().modelFile(cornerLeft).rotationY(y).addModel()
                    .condition(FurnitureBlock.FACING, facing)
                    .condition(SofaBlock.PART, SofaBlock.Part.CORNER_LEFT).end();
            builder.part().modelFile(cornerRight).rotationY(y).addModel()
                    .condition(FurnitureBlock.FACING, facing)
                    .condition(SofaBlock.PART, SofaBlock.Part.CORNER_RIGHT).end();
        }
    }

    /** The rug is always drawn; a border strip is added on each side that doesn't join another rug. */
    private static void rug(FurnitureBlockStateProvider models, Block block, DyeColor color) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile center = models.woolModel(name, models.template(ROOM, "rug_center"), color);
        ModelFile border = models.woolModel(name + "_border", models.template(ROOM, "rug_border"), color);
        var builder = models.getMultipartBuilder(block);
        builder.part().modelFile(center).addModel().end();
        for (Direction side : Direction.Plane.HORIZONTAL) {
            builder.part().modelFile(border).rotationY(FurnitureBlockStateProvider.yRot(side)).addModel()
                    .condition(ConnectingTableBlock.property(side), false).end();
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
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            Item carpet = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_carpet"));
            recipes.shaped(ARMCHAIR.get(color).get(), 1, "PWP", "PWP")
                    .define('W', wool).define('P', ItemTags.PLANKS)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
            recipes.shaped(RUG.get(color).get(), 2, "CC", "CC")
                    .define('C', carpet)
                    .unlockedBy("has_carpet", FurnitureRecipeProvider.hasItem(carpet)).save(output);
            recipes.shaped(CURTAINS.get(color).get(), 2, "SSS", "W W", "W W")
                    .define('S', Items.STICK).define('W', wool)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
        for (DyeColor color : DyeColor.values()) {
            Item carpet = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_carpet"));
            recipes.shaped(BLINDS.get(color).get(), 1, "SSS", "CCC")
                    .define('S', Items.STICK).define('C', carpet)
                    .unlockedBy("has_carpet", FurnitureRecipeProvider.hasItem(carpet)).save(output);
        }
        recipes.woodShaped(output, WALL_SHELF, 2, wood -> Map.of('T', Ingredient.of(Items.STICK)), "PPP", "T T");
        recipes.woodShaped(output, WALL_CLOCK, 1, wood -> Map.of('K', Ingredient.of(Items.CLOCK)), "PPP", "PKP", "PPP");
        recipes.shaped(CEILING_LIGHT.get(), 1, "III", "GLG")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('L', Items.GLOWSTONE_DUST)
                .unlockedBy("has_glowstone_dust", FurnitureRecipeProvider.hasItem(Items.GLOWSTONE_DUST)).save(output);
        recipes.shaped(FIREPLACE.get(), 1, "BBB", "B B", "BBB")
                .define('B', Items.BRICKS)
                .unlockedBy("has_bricks", FurnitureRecipeProvider.hasItem(Items.BRICKS)).save(output);
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
    public void translations(java.util.function.BiConsumer<String, String> add) {
        add.accept("message.reikos_furniture.clock_time", "It's %s");
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        // Bookshelves power enchanting tables, like the vanilla bookshelf.
        BOOKSHELF.all().forEach(block -> tags.tagFor(BlockTags.ENCHANTMENT_POWER_PROVIDER).add(block.get()));
    }
}
