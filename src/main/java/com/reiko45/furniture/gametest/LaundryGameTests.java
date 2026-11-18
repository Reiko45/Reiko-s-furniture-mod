package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.laundry.LaundryFurniture;
import com.reiko45.furniture.laundry.LaundryMachineBlock;
import com.reiko45.furniture.laundry.LaundryMachineBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.GameType;

/** Tests for the laundry room. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class LaundryGameTests {
    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = LaundryMachineBlockEntity.CYCLE_TICKS + 60)
    public static void washingMachineRemovesDye(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LaundryFurniture.WASHING_MACHINE.get());
        LaundryMachineBlockEntity washer = helper.getBlockEntity(pos);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
        helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(0xFF0000, true));

        washer.startOpen(player);
        washer.setItem(0, new ItemStack(Items.RED_WOOL, 4));
        washer.setItem(1, helmet);
        washer.setItem(2, new ItemStack(Items.BLUE_SHULKER_BOX));
        washer.setItem(3, new ItemStack(Items.STONE));
        washer.stopOpen(player);
        helper.assertBlockProperty(pos, LaundryMachineBlock.RUNNING, true);

        helper.succeedWhen(() -> {
            helper.assertBlockProperty(pos, LaundryMachineBlock.RUNNING, false);
            helper.assertTrue(washer.getItem(0).is(Items.WHITE_WOOL) && washer.getItem(0).getCount() == 4, "wool washed white");
            helper.assertFalse(washer.getItem(1).has(DataComponents.DYED_COLOR), "helmet color washed out");
            helper.assertTrue(washer.getItem(2).is(Items.SHULKER_BOX), "shulker box washed plain");
            helper.assertTrue(washer.getItem(3).is(Items.STONE), "stone untouched");
        });
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void washingMachineIgnoresCleanItems(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LaundryFurniture.WASHING_MACHINE.get());
        FurnitureStorageBlockEntity washer = helper.getBlockEntity(pos);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        washer.startOpen(player);
        washer.setItem(0, new ItemStack(Items.WHITE_WOOL));
        washer.stopOpen(player);
        helper.assertBlockProperty(pos, LaundryMachineBlock.RUNNING, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = LaundryMachineBlockEntity.CYCLE_TICKS + 80)
    public static void dryerDriesSpongesFromHoppers(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LaundryFurniture.DRYER.get());
        LaundryMachineBlockEntity dryer = helper.getBlockEntity(pos);
        // Put in the way a hopper would, without opening the door.
        dryer.setItem(0, new ItemStack(Items.WET_SPONGE, 2));
        helper.succeedWhen(() -> {
            helper.assertBlockProperty(pos, LaundryMachineBlock.RUNNING, false);
            helper.assertTrue(dryer.getItem(0).is(Items.SPONGE) && dryer.getItem(0).getCount() == 2, "sponges dried");
        });
    }
}
