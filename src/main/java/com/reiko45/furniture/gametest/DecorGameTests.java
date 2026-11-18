package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.WindowCoveringBlock;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.livingroom.WallClockBlock;
import com.reiko45.furniture.office.OfficeFurniture;
import com.reiko45.furniture.office.SafeBlockEntity;
import com.reiko45.furniture.outdoor.OutdoorFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for display pieces, window coverings and the practical pieces added in 1.2. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class DecorGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void plateHoldsAndGivesBackFood(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, DiningFurniture.PLATE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE, 3));

        helper.useBlock(pos, player);
        DisplayBlockEntity plate = helper.getBlockEntity(pos);
        helper.assertTrue(plate.getItems().get(0).is(Items.APPLE), "the apple should be on the plate");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 2, "one apple used");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(pos, player);
        helper.assertTrue(plate.isEmpty(), "the plate should be empty again");
        helper.assertTrue(player.getInventory().contains(new ItemStack(Items.APPLE)), "the apple should be back");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void wallShelfHoldsThreeItems(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.WALL_SHELF.get(FurnitureWood.OAK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOOK, 5));
        for (int i = 0; i < 5; i++) {
            helper.useBlock(pos, player);
        }
        DisplayBlockEntity shelf = helper.getBlockEntity(pos);
        helper.assertFalse(shelf.hasSpace(), "the shelf should be full");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 2, "only three books fit");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void closedBlindsBlockLight(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LivingRoomFurniture.BLINDS.get(DyeColor.WHITE).get());
        helper.assertValueEqual(helper.getBlockState(pos).getLightBlock(helper.getLevel(), helper.absolutePos(pos)), 15, "closed");
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, WindowCoveringBlock.OPEN, true);
        helper.assertValueEqual(helper.getBlockState(pos).getLightBlock(helper.getLevel(), helper.absolutePos(pos)), 0, "open");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void trashCanDestroysItems(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, KitchenFurniture.TRASH_CAN.get());
        FurnitureStorageBlockEntity trash = helper.getBlockEntity(pos);
        // Hoppers: items disappear right away.
        trash.setItem(0, new ItemStack(Items.ROTTEN_FLESH));
        helper.assertTrue(trash.getItem(0).isEmpty(), "items from hoppers are destroyed");
        // Players: items stay while the lid is open and are destroyed when it closes.
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        trash.startOpen(player);
        trash.setItem(0, new ItemStack(Items.ROTTEN_FLESH));
        helper.assertFalse(trash.getItem(0).isEmpty(), "items stay while open");
        trash.stopOpen(player);
        helper.assertTrue(trash.getItem(0).isEmpty(), "items are destroyed when it closes");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void safeOnlyOpensForItsOwner(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, OfficeFurniture.SAFE.get());
        SafeBlockEntity safe = helper.getBlockEntity(pos);
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
        safe.setOwner(owner);
        helper.assertTrue(safe.canAccess(owner), "the owner can open it");
        helper.assertFalse(safe.canAccess(stranger), "others can't");
        helper.assertValueEqual(helper.getBlockState(pos).getDestroyProgress(stranger, helper.getLevel(), helper.absolutePos(pos)),
                0.0F, "others can't break it");
        helper.assertTrue(helper.getBlockState(pos).getDestroyProgress(owner, helper.getLevel(), helper.absolutePos(pos)) > 0,
                "the owner can break it");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void doorbellGivesRedstonePulse(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos.below(), net.minecraft.world.level.block.Blocks.STONE);
        BlockState bell = OutdoorFurniture.DOORBELL.get().defaultBlockState().setValue(ButtonBlock.FACE, AttachFace.FLOOR);
        helper.setBlock(pos, bell);
        helper.pressButton(pos);
        helper.assertBlockProperty(pos, ButtonBlock.POWERED, true);
        helper.succeedWhen(() -> helper.assertBlockProperty(pos, ButtonBlock.POWERED, false));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void wallClockReadsTime(GameTestHelper helper) {
        helper.assertValueEqual(WallClockBlock.formatTime(0), "06:00", "sunrise");
        helper.assertValueEqual(WallClockBlock.formatTime(6000), "12:00", "noon");
        helper.assertValueEqual(WallClockBlock.formatTime(18000), "00:00", "midnight");
        helper.assertValueEqual(WallClockBlock.formatTime(30500), "12:30", "next day");
        helper.succeed();
    }
}
