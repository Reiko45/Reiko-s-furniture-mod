package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.livingroom.FloorLampBlock;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.livingroom.SofaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
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
