package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.WallTrimBlock;
import com.reiko45.furniture.decor.DecorFurniture;
import com.reiko45.furniture.decor.FramedWindowBlock;
import com.reiko45.furniture.decor.SlidingDoorBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the decor section: wallpaper, tiles, parquet, wall trims, doors and windows. */
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

    @GameTest(template = CoreGameTests.EMPTY)
    public static void frenchDoorOpensWithRedstone(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        DoorBlock door = DecorFurniture.FRENCH_DOOR.get(FurnitureWood.BIRCH).get();
        helper.setBlock(lower.below(), Blocks.STONE);
        helper.setBlock(lower, door.defaultBlockState());
        helper.setBlock(lower.above(), door.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        helper.setBlock(lower.east(), Blocks.REDSTONE_BLOCK);
        helper.assertBlockProperty(lower, DoorBlock.OPEN, true);
        helper.assertBlockProperty(lower.above(), DoorBlock.OPEN, true);
        helper.assertTrue(door.defaultBlockState().is(BlockTags.WOODEN_DOORS), "villagers can open it");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void slidingDoorOpensTheDoorway(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        SlidingDoorBlock door = DecorFurniture.SLIDING_DOOR.get(FurnitureWood.OAK).get();
        helper.setBlock(lower, door.defaultBlockState());
        helper.setBlock(lower.above(), door.defaultBlockState().setValue(SlidingDoorBlock.HALF, DoubleBlockHalf.UPPER));
        VoxelShape middle = Shapes.box(0.25, 0.1, 0.8, 0.75, 1, 1); // above the floor track
        BlockPos absolute = helper.absolutePos(lower);
        helper.assertTrue(Shapes.joinIsNotEmpty(helper.getBlockState(lower).getCollisionShape(helper.getLevel(), absolute), middle,
                BooleanOp.AND), "closed: the glass blocks the way");
        helper.useBlock(lower, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(lower.above(), SlidingDoorBlock.OPEN, true);
        helper.assertFalse(Shapes.joinIsNotEmpty(helper.getBlockState(lower).getCollisionShape(helper.getLevel(), absolute), middle,
                BooleanOp.AND), "open: you can walk through");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void windowsJoinIntoOne(GameTestHelper helper) {
        FramedWindowBlock window = DecorFurniture.WINDOW.get(FurnitureWood.SPRUCE).get();
        // A 2x2 window facing north: (1,1) and (2,1) on the bottom row, (1,2) and (2,2) above.
        for (BlockPos pos : List.of(new BlockPos(1, 1, 1), new BlockPos(2, 1, 1), new BlockPos(1, 2, 1), new BlockPos(2, 2, 1))) {
            CoreGameTests.placeFacing(helper, pos, window, Direction.NORTH);
        }
        BlockPos bottomWest = new BlockPos(1, 1, 1);
        helper.assertBlockProperty(bottomWest, FramedWindowBlock.UP, true);
        helper.assertBlockProperty(bottomWest, FramedWindowBlock.DOWN, false);
        helper.assertBlockProperty(bottomWest, FramedWindowBlock.LEFT, false);
        helper.assertBlockProperty(bottomWest, FramedWindowBlock.RIGHT, true);
        // A window facing another way doesn't join.
        CoreGameTests.placeFacing(helper, new BlockPos(3, 1, 1), window, Direction.EAST);
        helper.assertBlockProperty(new BlockPos(2, 1, 1), FramedWindowBlock.RIGHT, false);
        helper.useBlock(bottomWest, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(bottomWest, FramedWindowBlock.OPEN, true);
        helper.succeed();
    }
}
