package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class BedroomGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void tableLampTurnsOnAndOff(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BedroomFurniture.TABLE_LAMP.get(FurnitureWood.BIRCH).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(pos).getLightEmission(helper.getLevel(), helper.absolutePos(pos)),
                15, "light level when on");

        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void breakingWardrobeTopRemovesBottom(GameTestHelper helper) {
        BlockPos lowerPos = new BlockPos(2, 1, 2);
        Block wardrobe = BedroomFurniture.WARDROBE.get(FurnitureWood.SPRUCE).get();
        BlockState lower = wardrobe.defaultBlockState();
        helper.setBlock(lowerPos, lower);
        helper.setBlock(lowerPos.above(), lower.setValue(TallStorageFurnitureBlock.HALF, DoubleBlockHalf.UPPER));

        helper.destroyBlock(lowerPos.above());
        helper.assertBlockNotPresent(wardrobe, lowerPos);
        helper.assertBlockNotPresent(wardrobe, lowerPos.above());
        helper.assertItemEntityCountIs(wardrobe.asItem(), lowerPos, 2.0, 1);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void wardrobeTopUsesBottomStorage(GameTestHelper helper) {
        BlockPos lowerPos = new BlockPos(2, 1, 2);
        TallStorageFurnitureBlock wardrobe = BedroomFurniture.WARDROBE.get(FurnitureWood.OAK).get();
        BlockState upper = wardrobe.defaultBlockState().setValue(TallStorageFurnitureBlock.HALF, DoubleBlockHalf.UPPER);
        helper.assertValueEqual(wardrobe.storagePos(upper, lowerPos.above()), lowerPos, "storage position of top half");
        helper.assertTrue(wardrobe.newBlockEntity(lowerPos.above(), upper) == null, "top half has no inventory");
        helper.succeed();
    }
}
