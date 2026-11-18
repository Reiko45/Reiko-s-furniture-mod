package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.tool.DollyItem;
import com.reiko45.furniture.core.tool.HammerItem;
import com.reiko45.furniture.core.tool.Tools;
import com.reiko45.furniture.office.OfficeFurniture;
import com.reiko45.furniture.office.SafeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the furniture hammer and the moving dolly. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class ToolGameTests {
    private static void worked(GameTestHelper helper, DollyItem.Problem problem, String what) {
        helper.assertTrue(problem == null, what + " failed: " + problem);
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void hammerTurnsFurniture(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        CoreGameTests.placeFacing(helper, pos, BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get(), Direction.NORTH);
        HammerItem.Result result = HammerItem.rotate(helper.getLevel(), helper.absolutePos(pos), true);
        helper.assertValueEqual(result, HammerItem.Result.TURNED, "result");
        helper.assertBlockProperty(pos, FurnitureBlock.FACING, Direction.EAST);
        HammerItem.rotate(helper.getLevel(), helper.absolutePos(pos), false);
        helper.assertBlockProperty(pos, FurnitureBlock.FACING, Direction.NORTH);

        // Both halves of a wardrobe turn together, from either half.
        BlockPos lower = new BlockPos(3, 1, 1);
        BlockState wardrobe = BedroomFurniture.WARDROBE.get(FurnitureWood.OAK).get().defaultBlockState();
        helper.setBlock(lower, wardrobe);
        helper.setBlock(lower.above(), wardrobe.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        HammerItem.rotate(helper.getLevel(), helper.absolutePos(lower.above()), true);
        helper.assertBlockProperty(lower, FurnitureBlock.FACING, Direction.EAST);
        helper.assertBlockProperty(lower.above(), FurnitureBlock.FACING, Direction.EAST);

        // Beds can't be turned, and vanilla blocks are left alone.
        helper.setBlock(new BlockPos(5, 1, 1), BedroomFurniture.BED.get(FurnitureWood.OAK).get());
        helper.assertValueEqual(HammerItem.rotate(helper.getLevel(), helper.absolutePos(new BlockPos(5, 1, 1)), true),
                HammerItem.Result.CANNOT_TURN, "bed");
        helper.setBlock(new BlockPos(6, 1, 1), Blocks.FURNACE);
        helper.assertValueEqual(HammerItem.rotate(helper.getLevel(), helper.absolutePos(new BlockPos(6, 1, 1)), true),
                HammerItem.Result.NOT_FURNITURE, "furnace");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void dollyMovesStorageWithItsContents(GameTestHelper helper) {
        BlockPos from = new BlockPos(1, 1, 1);
        BlockPos to = new BlockPos(5, 1, 5);
        helper.setBlock(from, BedroomFurniture.DRESSER.get(FurnitureWood.ACACIA).get());
        FurnitureStorageBlockEntity dresser = helper.getBlockEntity(from);
        dresser.setItem(4, new ItemStack(Items.DIAMOND, 7));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack dolly = new ItemStack(Tools.MOVING_DOLLY.get());

        worked(helper, DollyItem.pickUp(helper.getLevel(), helper.absolutePos(from), player, dolly), "pick up");
        helper.assertBlockNotPresent(BedroomFurniture.DRESSER.get(FurnitureWood.ACACIA).get(), from);
        helper.assertTrue(dolly.has(ModDataComponents.CARRIED_FURNITURE.get()), "the dolly carries it");
        helper.assertItemEntityNotPresent(Items.DIAMOND);

        worked(helper, DollyItem.putDown(helper.getLevel(), helper.absolutePos(to), Direction.WEST, dolly), "put down");
        helper.assertBlockProperty(to, FurnitureBlock.FACING, Direction.WEST);
        FurnitureStorageBlockEntity moved = helper.getBlockEntity(to);
        helper.assertTrue(moved.getItem(4).is(Items.DIAMOND) && moved.getItem(4).getCount() == 7, "the diamonds came along");
        helper.assertFalse(dolly.has(ModDataComponents.CARRIED_FURNITURE.get()), "the dolly is empty again");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void dollyMovesTallStorageAndRespectsSafes(GameTestHelper helper) {
        BlockPos lower = new BlockPos(1, 1, 1);
        BlockState wardrobe = BedroomFurniture.WARDROBE.get(FurnitureWood.SPRUCE).get().defaultBlockState();
        helper.setBlock(lower, wardrobe);
        helper.setBlock(lower.above(), wardrobe.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack dolly = new ItemStack(Tools.MOVING_DOLLY.get());
        worked(helper, DollyItem.pickUp(helper.getLevel(), helper.absolutePos(lower.above()), owner, dolly), "pick up");
        helper.assertBlockNotPresent(wardrobe.getBlock(), lower);
        helper.assertBlockNotPresent(wardrobe.getBlock(), lower.above());
        DollyItem.putDown(helper.getLevel(), helper.absolutePos(new BlockPos(4, 1, 1)), Direction.NORTH, dolly);
        helper.assertBlockProperty(new BlockPos(4, 2, 1), TallBlockHelper.HALF, DoubleBlockHalf.UPPER);

        BlockPos safePos = new BlockPos(1, 1, 5);
        helper.setBlock(safePos, OfficeFurniture.SAFE.get());
        SafeBlockEntity safe = helper.getBlockEntity(safePos);
        safe.setOwner(owner);
        Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertValueEqual(DollyItem.pickUp(helper.getLevel(), helper.absolutePos(safePos), stranger, new ItemStack(Tools.MOVING_DOLLY.get())),
                DollyItem.Problem.NOT_YOURS, "stranger");
        worked(helper, DollyItem.pickUp(helper.getLevel(), helper.absolutePos(safePos), owner, new ItemStack(Tools.MOVING_DOLLY.get())), "owner");
        helper.succeed();
    }
}
