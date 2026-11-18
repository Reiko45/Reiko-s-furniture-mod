package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.WallTrimBlock;
import com.reiko45.furniture.decor.DecorFurniture;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the decor building blocks: wallpaper, tiles, parquet and wall trims. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class BuildingBlockGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void baseboardsTurnCorners(GameTestHelper helper) {
        Block baseboard = DecorFurniture.BASEBOARD.get(FurnitureWood.OAK).get();
        // Inner corner: the next baseboard in front runs along the other wall (facing east).
        CoreGameTests.placeFacing(helper, new BlockPos(1, 1, 0), baseboard, Direction.EAST);
        CoreGameTests.placeFacing(helper, new BlockPos(1, 1, 1), baseboard, Direction.NORTH);
        helper.assertBlockProperty(new BlockPos(1, 1, 1), WallTrimBlock.SHAPE, WallTrimBlock.Shape.INNER_RIGHT);
        // Outer corner: the baseboard behind runs along the other wall (facing west).
        CoreGameTests.placeFacing(helper, new BlockPos(4, 1, 2), baseboard, Direction.WEST);
        CoreGameTests.placeFacing(helper, new BlockPos(4, 1, 1), baseboard, Direction.NORTH);
        helper.assertBlockProperty(new BlockPos(4, 1, 1), WallTrimBlock.SHAPE, WallTrimBlock.Shape.OUTER_LEFT);
        // In a row: straight.
        CoreGameTests.placeFacing(helper, new BlockPos(6, 1, 1), baseboard, Direction.NORTH);
        CoreGameTests.placeFacing(helper, new BlockPos(7, 1, 1), baseboard, Direction.NORTH);
        helper.assertBlockProperty(new BlockPos(7, 1, 1), WallTrimBlock.SHAPE, WallTrimBlock.Shape.STRAIGHT);
        // Crown molding doesn't join baseboards.
        CoreGameTests.placeFacing(helper, new BlockPos(1, 2, 0), DecorFurniture.CROWN_MOLDING.get(FurnitureWood.OAK).get(), Direction.EAST);
        CoreGameTests.placeFacing(helper, new BlockPos(1, 2, 1), baseboard, Direction.NORTH);
        helper.assertBlockProperty(new BlockPos(1, 2, 1), WallTrimBlock.SHAPE, WallTrimBlock.Shape.STRAIGHT);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void doubleParquetSlabDropsTwo(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        SlabBlock slab = DecorFurniture.PARQUET_SLAB.get(FurnitureWood.CHERRY).get();
        BlockState full = slab.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        List<ItemStack> drops = Block.getDrops(full, helper.getLevel(), pos, null);
        helper.assertValueEqual(drops.stream().mapToInt(ItemStack::getCount).sum(), 2, "double slab drops");
        helper.assertTrue(slab.defaultBlockState().is(BlockTags.WOODEN_SLABS), "is a wooden slab");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tilesNeedAPickaxeAndWallpaperAnAxe(GameTestHelper helper) {
        for (var tiles : DecorFurniture.TILES.values()) {
            BlockState state = tiles.get().defaultBlockState();
            helper.assertTrue(state.requiresCorrectToolForDrops() && state.is(BlockTags.MINEABLE_WITH_PICKAXE), tiles.getId() + " needs a pickaxe");
        }
        helper.assertTrue(DecorFurniture.WALLPAPER.get(DyeColor.RED).get().defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), "wallpaper");
        helper.assertTrue(DecorFurniture.PARQUET.get(FurnitureWood.OAK).get().defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), "parquet");
        helper.succeed();
    }
}
