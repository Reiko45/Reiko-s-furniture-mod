package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.greenhouse.GreenhouseFurniture;
import com.reiko45.furniture.greenhouse.GrowingPotBlock;
import com.reiko45.furniture.greenhouse.PlantHolderBlock;
import com.reiko45.furniture.greenhouse.WateringCanItem;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class GreenhouseGameTests {
    /** Uses the item in the player's main hand on a block, as a right-click on its top would. */
    private static InteractionResult useOn(GameTestHelper helper, ServerPlayer player, BlockPos relative) {
        BlockPos pos = helper.absolutePos(relative);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        return player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // a mock server player holds the watering can
    public static void potGrowsWithTheWateringCan(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, GreenhouseFurniture.TERRACOTTA_GROWING_POT.get());
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL); // creative players never run out of water
        ItemStack can = new ItemStack(GreenhouseFurniture.WATERING_CAN.get());
        WateringCanItem.setWater(can, WateringCanItem.CAPACITY);
        player.setItemInHand(InteractionHand.MAIN_HAND, can);

        // An empty pot has nothing to grow.
        useOn(helper, player, pos);
        helper.assertBlockProperty(pos, GrowingPotBlock.GROWTH, 0);
        helper.assertValueEqual(WateringCanItem.water(can), WateringCanItem.CAPACITY - 1, "one watering used");

        DisplayBlockEntity pot = helper.getBlockEntity(pos);
        helper.assertTrue(pot.insert(new ItemStack(Items.OAK_SAPLING), null), "sapling planted");
        for (int i = 0; i < GrowingPotBlock.FULL + 1; i++) {
            useOn(helper, player, pos);
        }
        helper.assertBlockProperty(pos, GrowingPotBlock.GROWTH, GrowingPotBlock.FULL);
        helper.assertValueEqual(WateringCanItem.water(can), WateringCanItem.CAPACITY - 5, "five waterings used");
        helper.assertFalse(((GrowingPotBlock) helper.getBlockState(pos).getBlock())
                .isValidBonemealTarget(helper.getLevel(), helper.absolutePos(pos), helper.getBlockState(pos)), "fully grown");

        // Taking the plant out empties the pot; the next plant starts small.
        helper.getBlockState(pos).useWithoutItem(helper.getLevel(), player,
                new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(pos)), Direction.UP, helper.absolutePos(pos), false));
        helper.assertTrue(pot.isEmpty(), "sapling taken out");
        helper.assertBlockProperty(pos, GrowingPotBlock.GROWTH, 0);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // a mock server player holds the watering can
    public static void wateringCanFillsAndEmpties(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL); // creative players never run out of water
        ItemStack can = new ItemStack(GreenhouseFurniture.WATERING_CAN.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, can);
        BlockPos farmland = new BlockPos(0, 1, 0);
        helper.setBlock(farmland, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 0));

        helper.assertValueEqual(WateringCanItem.water(can), 0, "a new can is empty");
        helper.assertValueEqual(useOn(helper, player, farmland), InteractionResult.FAIL, "nothing to pour");

        BlockPos sink = new BlockPos(2, 1, 2);
        helper.setBlock(sink, KitchenFurniture.SINK.get(FurnitureWood.OAK).get());
        useOn(helper, player, sink);
        helper.assertValueEqual(WateringCanItem.water(can), WateringCanItem.CAPACITY, "filled at the sink");

        useOn(helper, player, farmland);
        helper.assertBlockProperty(farmland, FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE);
        helper.assertValueEqual(WateringCanItem.water(can), WateringCanItem.CAPACITY - 1, "one watering used");

        BlockPos cauldron = new BlockPos(2, 1, 0);
        helper.setBlock(cauldron, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
        useOn(helper, player, cauldron);
        helper.assertValueEqual(WateringCanItem.water(can), WateringCanItem.CAPACITY, "filled from a cauldron");
        helper.assertBlockProperty(cauldron, LayeredCauldronBlock.LEVEL, 2);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void flowerBoxHoldsThreeFlowers(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, GreenhouseFurniture.WINDOW_FLOWER_BOX.get(FurnitureWood.BIRCH).get());
        PlantHolderBlock block = (PlantHolderBlock) helper.getBlockState(pos).getBlock();
        helper.assertTrue(block.accepts(new ItemStack(Items.POPPY)), "flowers");
        helper.assertTrue(block.accepts(new ItemStack(Items.BIRCH_SAPLING)), "saplings");
        helper.assertTrue(block.accepts(new ItemStack(Items.ROSE_BUSH)), "tall flowers");
        helper.assertFalse(block.accepts(new ItemStack(Items.STICK)), "not sticks");

        DisplayBlockEntity box = helper.getBlockEntity(pos);
        for (var flower : new net.minecraft.world.item.Item[] {Items.POPPY, Items.DANDELION, Items.CORNFLOWER}) {
            helper.assertTrue(box.insert(new ItemStack(flower), null), "planted");
        }
        helper.assertFalse(box.canInsert(new ItemStack(Items.ALLIUM)), "three flowers fill it");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void potsGrowOnTheirOwnAndWithBoneMeal(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, GreenhouseFurniture.GROWING_POT.get(net.minecraft.world.item.DyeColor.BLUE).get());
        GrowingPotBlock block = (GrowingPotBlock) helper.getBlockState(pos).getBlock();
        BlockPos abs = helper.absolutePos(pos);
        helper.assertTrue(helper.getBlockState(pos).isRandomlyTicking(), "an unfinished pot ticks");
        helper.assertFalse(block.isValidBonemealTarget(helper.getLevel(), abs, helper.getBlockState(pos)), "nothing planted");

        DisplayBlockEntity pot = helper.getBlockEntity(pos);
        pot.insert(new ItemStack(Items.POPPY), null);
        helper.assertTrue(block.isValidBonemealTarget(helper.getLevel(), abs, helper.getBlockState(pos)), "planted");
        block.performBonemeal(helper.getLevel(), helper.getLevel().random, abs, helper.getBlockState(pos));
        helper.assertBlockProperty(pos, GrowingPotBlock.GROWTH, 1);
        // Random ticks grow it a third of the time.
        for (int i = 0; i < 200 && helper.getBlockState(pos).getValue(GrowingPotBlock.GROWTH) < GrowingPotBlock.FULL; i++) {
            helper.getBlockState(pos).randomTick(helper.getLevel(), abs, helper.getLevel().random);
        }
        helper.assertBlockProperty(pos, GrowingPotBlock.GROWTH, GrowingPotBlock.FULL);
        helper.assertFalse(helper.getBlockState(pos).isRandomlyTicking(), "a full pot stops ticking");
        helper.succeed();
    }
}
