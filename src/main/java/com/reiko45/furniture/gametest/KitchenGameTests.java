package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import com.reiko45.furniture.kitchen.StoveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class KitchenGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void countersFormCorners(GameTestHelper helper) {
        Block counter = KitchenFurniture.COUNTER.get(FurnitureWood.OAK).get();
        // Inner corner: the counter in front of it is turned sideways.
        BlockPos inner = new BlockPos(1, 1, 2);
        CoreGameTests.placeFacing(helper, inner, counter, Direction.NORTH);
        CoreGameTests.placeFacing(helper, inner.north(), counter, Direction.EAST);
        helper.assertBlockProperty(inner, CounterBlock.SHAPE, CounterBlock.Shape.INNER);
        helper.assertBlockProperty(inner.north(), CounterBlock.SHAPE, CounterBlock.Shape.STRAIGHT);

        // Outer corner: the counter behind it faces west, so this one has doors on its left side too.
        BlockPos outer = new BlockPos(4, 1, 2);
        CoreGameTests.placeFacing(helper, outer, counter, Direction.NORTH);
        CoreGameTests.placeFacing(helper, outer.south(), counter, Direction.WEST);
        helper.assertBlockProperty(outer, CounterBlock.SHAPE, CounterBlock.Shape.OUTER_LEFT);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void sinkFillsBucket(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, KitchenFurniture.SINK.get(FurnitureWood.OAK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));

        helper.useBlock(pos, player);
        helper.assertTrue(player.getMainHandItem().is(Items.WATER_BUCKET), "bucket should be filled with water");

        helper.useBlock(pos, player);
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "water bucket should be emptied into the sink");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void sinkFillsGlassBottle(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, KitchenFurniture.SINK.get(FurnitureWood.CHERRY).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));

        helper.useBlock(pos, player);
        ItemStack held = player.getMainHandItem();
        PotionContents contents = held.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        helper.assertTrue(held.is(Items.POTION) && contents.is(Potions.WATER), "bottle should hold water, got " + held);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 400)
    public static void stoveCooksFood(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, KitchenFurniture.STOVE.get());
        StoveBlockEntity stove = helper.getBlockEntity(pos);
        stove.setItem(0, new ItemStack(Items.BEEF));
        stove.setItem(1, new ItemStack(Items.COAL));

        helper.succeedWhen(() -> {
            helper.assertTrue(stove.getItem(2).is(Items.COOKED_BEEF), "beef should be cooked");
            helper.assertBlockProperty(pos, AbstractFurnaceBlock.LIT, true);
        });
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 200)
    public static void stoveDoesNotSmeltOre(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, KitchenFurniture.STOVE.get());
        StoveBlockEntity stove = helper.getBlockEntity(pos);
        stove.setItem(0, new ItemStack(Items.RAW_IRON));
        stove.setItem(1, new ItemStack(Items.COAL));

        // Like a smoker, the stove only cooks food.
        helper.runAfterDelay(150, () -> {
            helper.assertTrue(stove.getItem(2).isEmpty(), "stove should not smelt raw iron");
            helper.succeed();
        });
    }
}
