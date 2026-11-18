package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.entity.SeatEntity;
import com.reiko45.furniture.office.OfficeFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class OfficeGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void officeChairCanBeSatOn(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, OfficeFurniture.OFFICE_CHAIR.get(DyeColor.BLACK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(pos, player);
        helper.assertTrue(player.getVehicle() instanceof SeatEntity, "player should be sitting");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void computerAndDeskLampSwitchOn(GameTestHelper helper) {
        BlockPos computer = new BlockPos(1, 1, 1);
        BlockPos lamp = new BlockPos(3, 1, 1);
        helper.setBlock(computer, OfficeFurniture.COMPUTER.get());
        helper.setBlock(lamp, OfficeFurniture.DESK_LAMP.get());
        helper.useBlock(computer, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(computer, SwitchableFurnitureBlock.LIT, true);
        helper.setBlock(lamp.east(), Blocks.REDSTONE_BLOCK);
        helper.assertBlockProperty(lamp, SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(lamp).getLightEmission(helper.getLevel(), helper.absolutePos(lamp)), 13, "desk lamp light");
        helper.succeed();
    }
}
