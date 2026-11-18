package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bathroom.BathroomFurniture;
import com.reiko45.furniture.bathroom.BathtubBlock;
import com.reiko45.furniture.bathroom.ShowerBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class BathroomGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void bathtubFillsAndEmpties(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BathroomFurniture.BATHTUB.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));

        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, BathtubBlock.FILLED, true);
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "bucket should be empty");

        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, BathtubBlock.FILLED, false);
        helper.assertTrue(player.getMainHandItem().is(Items.WATER_BUCKET), "bucket should be full again");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(pos, player);
        helper.assertTrue(player.getVehicle() instanceof SeatEntity, "player should sit in the tub");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void bathtubsJoin(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        CoreGameTests.placeFacing(helper, pos, BathroomFurniture.BATHTUB.get(), Direction.NORTH);
        CoreGameTests.placeFacing(helper, pos.east(), BathroomFurniture.BATHTUB.get(), Direction.NORTH);
        helper.assertBlockProperty(pos, BathtubBlock.PART, BathtubBlock.Part.LEFT);
        helper.assertBlockProperty(pos.east(), BathtubBlock.PART, BathtubBlock.Part.RIGHT);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void showerPutsOutFire(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        BlockState state = BathroomFurniture.SHOWER.get().defaultBlockState();
        helper.setBlock(lower, state);
        helper.setBlock(lower.above(), state.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        helper.useBlock(lower.above(), helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(lower, ShowerBlock.RUNNING, true);
        helper.assertBlockProperty(lower.above(), ShowerBlock.RUNNING, true);

        var pig = helper.spawn(net.minecraft.world.entity.EntityType.PIG, lower);
        pig.igniteForSeconds(10);
        helper.succeedWhen(() -> helper.assertFalse(pig.isOnFire(), "the shower should put the fire out"));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void bathroomSinkFillsBucket(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BathroomFurniture.BATHROOM_SINK.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        helper.useBlock(pos, player);
        helper.assertTrue(player.getMainHandItem().is(Items.WATER_BUCKET), "bucket should be filled");
        helper.succeed();
    }
}
