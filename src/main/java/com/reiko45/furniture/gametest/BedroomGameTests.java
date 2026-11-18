package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    public static void tableLampFollowsRedstone(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BedroomFurniture.TABLE_LAMP.get(FurnitureWood.OAK).get());

        helper.setBlock(pos.west(), Blocks.REDSTONE_BLOCK);
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, true);

        // Right-clicking still works while powered, and power loss turns it off.
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, false);
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.setBlock(pos.west(), Blocks.AIR);
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void bedCanBeDyed(GameTestHelper helper) {
        BlockPos foot = new BlockPos(1, 1, 2);
        FurnitureBedBlock bed = BedroomFurniture.BED.get(FurnitureWood.OAK).get();
        BlockState state = bed.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
        helper.setBlock(foot, state);
        helper.setBlock(foot.north(), state.setValue(BedBlock.PART, BedPart.HEAD));
        helper.assertTrue(helper.getBlockState(foot).isBed(helper.getLevel(), helper.absolutePos(foot), null),
                "players should be able to sleep in it");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BLUE_DYE, 2));
        helper.useBlock(foot, player);
        helper.assertBlockProperty(foot, FurnitureBedBlock.COLOR, DyeColor.BLUE);
        helper.assertBlockProperty(foot.north(), FurnitureBedBlock.COLOR, DyeColor.BLUE);
        helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "dye used");
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
