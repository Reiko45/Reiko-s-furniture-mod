package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.entity.SeatEntity;
import com.reiko45.furniture.dining.DiningFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class DiningGameTests {
    /** Places a block and lets it look at its neighbours, the way a block placed by a player does. */
    static void place(GameTestHelper helper, BlockPos pos, Block block) {
        BlockPos absolute = helper.absolutePos(pos);
        helper.getLevel().setBlock(absolute,
                Block.updateFromNeighbourShapes(block.defaultBlockState(), helper.getLevel(), absolute), Block.UPDATE_ALL);
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void diningTablesConnect(GameTestHelper helper) {
        Block table = DiningFurniture.DINING_TABLE.get(FurnitureWood.OAK).get();
        BlockPos first = new BlockPos(1, 1, 1);
        BlockPos second = first.east();
        place(helper, first, table);
        place(helper, second, table);
        helper.assertBlockProperty(first, ConnectingTableBlock.EAST, true);
        helper.assertBlockProperty(second, ConnectingTableBlock.WEST, true);
        helper.assertBlockProperty(first, ConnectingTableBlock.WEST, false);

        helper.setBlock(second, net.minecraft.world.level.block.Blocks.AIR);
        helper.assertBlockProperty(first, ConnectingTableBlock.EAST, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tablesOfDifferentWoodDoNotConnect(GameTestHelper helper) {
        BlockPos first = new BlockPos(1, 1, 1);
        place(helper, first, DiningFurniture.DINING_TABLE.get(FurnitureWood.OAK).get());
        place(helper, first.east(), DiningFurniture.DINING_TABLE.get(FurnitureWood.BIRCH).get());
        helper.assertBlockProperty(first, ConnectingTableBlock.EAST, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void playerSitsOnChair(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, DiningFurniture.DINING_CHAIR.get(FurnitureWood.OAK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        helper.useBlock(pos, player);
        helper.assertTrue(player.getVehicle() instanceof SeatEntity, "player should be sitting");
        helper.assertValueEqual(player.getVehicle().getY(), helper.absolutePos(pos).getY() + 9 / 16.0, "seat height");

        // Breaking the chair makes the player stand up.
        helper.destroyBlock(pos);
        helper.succeedWhen(() -> helper.assertFalse(player.isPassenger(), "player should stand up"));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void onlyOnePlayerPerSeat(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, DiningFurniture.BENCH.get(FurnitureWood.SPRUCE).get());
        Player first = helper.makeMockPlayer(GameType.SURVIVAL);
        Player second = helper.makeMockPlayer(GameType.SURVIVAL);

        helper.useBlock(pos, first);
        helper.useBlock(pos, second);
        helper.assertTrue(first.isPassenger(), "first player should be sitting");
        helper.assertFalse(second.isPassenger(), "second player should not share the seat");
        helper.succeed();
    }
}
