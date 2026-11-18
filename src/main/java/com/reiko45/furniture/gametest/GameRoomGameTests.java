package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.gameroom.ArcadeCabinetBlock;
import com.reiko45.furniture.gameroom.DartboardBlock;
import com.reiko45.furniture.gameroom.GameRoomFurniture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/** Tests for the game room. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class GameRoomGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void dartScoresAreReal(GameTestHelper helper) {
        RandomSource random = RandomSource.create(42);
        for (int i = 0; i < 500; i++) {
            int score = DartboardBlock.randomScore(random);
            boolean triple = score % 3 == 0 && score <= 60;
            boolean dbl = score % 2 == 0 && score <= 40;
            helper.assertTrue(score >= 1 && (score <= 20 || dbl || triple || score == 25 || score == 50), "impossible score " + score);
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void dartboardGivesRedstonePulse(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, GameRoomFurniture.DARTBOARD.get());
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, DartboardBlock.POWERED, true);
        helper.succeedWhen(() -> helper.assertBlockProperty(pos, DartboardBlock.POWERED, false));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void arcadeScreenLightsUp(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        ArcadeCabinetBlock arcade = GameRoomFurniture.ARCADE_CABINET.get();
        helper.setBlock(lower, arcade.defaultBlockState());
        helper.setBlock(lower.above(), arcade.defaultBlockState().setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        helper.useBlock(lower, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(lower, SwitchableFurnitureBlock.LIT, true);
        helper.assertBlockProperty(lower.above(), SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(lower.above())
                .getLightEmission(helper.getLevel(), helper.absolutePos(lower.above())), 9, "screen light level");
        helper.succeed();
    }
}
