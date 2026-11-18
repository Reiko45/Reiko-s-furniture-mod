package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.livingroom.CurtainBlock;
import com.reiko45.furniture.livingroom.FloorLampBlock;
import com.reiko45.furniture.livingroom.RugBlock;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.livingroom.SofaBlock;
import com.reiko45.furniture.livingroom.TvBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class LivingRoomGameTests {
    private static void placeSofa(GameTestHelper helper, BlockPos pos, DyeColor color) {
        BlockState state = LivingRoomFurniture.SOFA.get(color).get().defaultBlockState()
                .setValue(FurnitureBlock.FACING, Direction.NORTH);
        BlockPos absolute = helper.absolutePos(pos);
        helper.getLevel().setBlock(absolute, Block.updateFromNeighbourShapes(state, helper.getLevel(), absolute),
                Block.UPDATE_ALL);
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void sofasJoinIntoLongSofa(GameTestHelper helper) {
        // Facing north, the sitter's right is east (+x).
        BlockPos left = new BlockPos(1, 1, 1);
        placeSofa(helper, left, DyeColor.RED);
        helper.assertBlockProperty(left, SofaBlock.PART, SofaBlock.Part.SINGLE);

        placeSofa(helper, left.east(), DyeColor.BLUE);
        helper.assertBlockProperty(left, SofaBlock.PART, SofaBlock.Part.LEFT);
        helper.assertBlockProperty(left.east(), SofaBlock.PART, SofaBlock.Part.RIGHT);

        placeSofa(helper, left.east(2), DyeColor.RED);
        helper.assertBlockProperty(left, SofaBlock.PART, SofaBlock.Part.LEFT);
        helper.assertBlockProperty(left.east(), SofaBlock.PART, SofaBlock.Part.MIDDLE);
        helper.assertBlockProperty(left.east(2), SofaBlock.PART, SofaBlock.Part.RIGHT);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void sofasFormLShape(GameTestHelper helper) {
        // Corner C faces north; row A continues east of it; row B continues north of it, facing east.
        BlockPos corner = new BlockPos(1, 1, 2);
        Block red = LivingRoomFurniture.SOFA.get(DyeColor.RED).get();
        CoreGameTests.placeFacing(helper, corner, red, Direction.NORTH);
        CoreGameTests.placeFacing(helper, corner.east(), red, Direction.NORTH);
        CoreGameTests.placeFacing(helper, corner.north(), red, Direction.EAST);

        helper.assertBlockProperty(corner, SofaBlock.PART, SofaBlock.Part.CORNER_LEFT);
        helper.assertBlockProperty(corner.east(), SofaBlock.PART, SofaBlock.Part.RIGHT);
        helper.assertBlockProperty(corner.north(), SofaBlock.PART, SofaBlock.Part.LEFT);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void curtainsOpenAndClose(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.CURTAINS.get(DyeColor.PURPLE).get());
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, CurtainBlock.OPEN, true);
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, CurtainBlock.OPEN, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void fireplaceLightsWithFlintAndSteel(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.FIREPLACE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.FLINT_AND_STEEL));
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(pos).getLightEmission(helper.getLevel(), helper.absolutePos(pos)), 15, "light level");
        helper.assertValueEqual(player.getMainHandItem().getDamageValue(), 1, "flint and steel damage");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void rugsOfSameColorJoin(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 1);
        for (int x = 0; x < 5; x++) {
            helper.setBlock(new BlockPos(x, 0, 1), Blocks.STONE); // rugs need a floor
        }
        DiningGameTests.place(helper, pos, LivingRoomFurniture.RUG.get(DyeColor.RED).get());
        DiningGameTests.place(helper, pos.east(), LivingRoomFurniture.RUG.get(DyeColor.RED).get());
        DiningGameTests.place(helper, pos.west(), LivingRoomFurniture.RUG.get(DyeColor.BLUE).get());
        helper.assertBlockProperty(pos, RugBlock.EAST, true);
        helper.assertBlockProperty(pos.east(), RugBlock.WEST, true);
        helper.assertBlockProperty(pos, RugBlock.WEST, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void bookshelfPowersEnchanting(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.BOOKSHELF.get(FurnitureWood.DARK_OAK).get());
        float bonus = helper.getBlockState(pos).getEnchantPowerBonus(helper.getLevel(), helper.absolutePos(pos));
        helper.assertValueEqual(bonus, 1.0F, "enchanting power");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tvTurnsOn(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.TV.get());
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(pos).getLightEmission(helper.getLevel(), helper.absolutePos(pos)),
                10, "TV light level");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tvCyclesThroughChannels(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.TV.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int channel = 1; channel <= TvBlock.CHANNELS; channel++) {
            helper.useBlock(pos, player);
            helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, true);
            helper.assertBlockProperty(pos, TvBlock.CHANNEL, channel);
        }
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void floorLampFollowsRedstoneOnTopHalf(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        FloorLampBlock lamp = LivingRoomFurniture.FLOOR_LAMP.get(FurnitureWood.BIRCH).get();
        helper.setBlock(lower, lamp.defaultBlockState());
        helper.setBlock(lower.above(), lamp.defaultBlockState().setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));

        helper.setBlock(lower.above().east(), Blocks.REDSTONE_BLOCK);
        helper.assertBlockProperty(lower, SwitchableFurnitureBlock.LIT, true);
        helper.assertBlockProperty(lower.above(), SwitchableFurnitureBlock.LIT, true);

        helper.setBlock(lower.above().east(), Blocks.AIR);
        helper.assertBlockProperty(lower, SwitchableFurnitureBlock.LIT, false);
        helper.assertBlockProperty(lower.above(), SwitchableFurnitureBlock.LIT, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void floorLampSwitchesBothHalves(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        FloorLampBlock lamp = LivingRoomFurniture.FLOOR_LAMP.get(FurnitureWood.OAK).get();
        helper.setBlock(lower, lamp.defaultBlockState());
        helper.setBlock(lower.above(), lamp.defaultBlockState().setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        // Switch it on from the bottom: both halves change, only the shade gives light.
        helper.useBlock(lower, player);
        helper.assertBlockProperty(lower, SwitchableFurnitureBlock.LIT, true);
        helper.assertBlockProperty(lower.above(), SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(lower.above())
                .getLightEmission(helper.getLevel(), helper.absolutePos(lower.above())), 15, "shade light level");
        helper.assertValueEqual(helper.getBlockState(lower)
                .getLightEmission(helper.getLevel(), helper.absolutePos(lower)), 0, "pole light level");

        // Switch it off from the top.
        helper.useBlock(lower.above(), player);
        helper.assertBlockProperty(lower, SwitchableFurnitureBlock.LIT, false);
        helper.assertBlockProperty(lower.above(), SwitchableFurnitureBlock.LIT, false);

        // Breaking the bottom removes the top too.
        helper.destroyBlock(lower);
        helper.assertBlockNotPresent(lamp, lower.above());
        helper.succeed();
    }
}
