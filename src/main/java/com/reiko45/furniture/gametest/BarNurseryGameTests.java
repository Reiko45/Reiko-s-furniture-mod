package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bar.BarFurniture;
import com.reiko45.furniture.bar.ChalkboardBlock;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.nursery.AquariumBlockEntity;
import com.reiko45.furniture.nursery.BirdCageBlockEntity;
import com.reiko45.furniture.nursery.NurseryFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the home bar and the nursery and hobby room. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class BarNurseryGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void wineRackHoldsBottles(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BarFurniture.WINE_RACK.get(FurnitureWood.DARK_OAK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BREAD));
        helper.useBlock(pos, player);
        DisplayBlockEntity rack = helper.getBlockEntity(pos);
        helper.assertTrue(rack.isEmpty(), "bread isn't a bottle");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.HONEY_BOTTLE, 12));
        for (int i = 0; i < 12; i++) {
            helper.useBlock(pos, player);
        }
        helper.assertFalse(rack.hasSpace(), "nine bottles fill it");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 3, "three left over");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 300)
    public static void espressoMachineMakesEspresso(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, BarFurniture.ESPRESSO_MACHINE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COCOA_BEANS, 2));
        helper.useBlock(pos, player);
        helper.useBlock(pos, player);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, ApplianceBlock.RUNNING, true);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(BarFurniture.ESPRESSO.get(), pos, 2.0));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void chalkboardIsASignWithWhiteText(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos.south(), Blocks.STONE);
        helper.setBlock(pos, BarFurniture.CHALKBOARD.get().defaultBlockState().setValue(WallSignBlock.FACING, Direction.NORTH));
        helper.assertTrue(helper.getBlockEntity(pos) instanceof SignBlockEntity, "it's a sign");
        SignBlockEntity sign = helper.getBlockEntity(pos);
        ChalkboardBlock.whiten(sign);
        helper.assertValueEqual(sign.getFrontText().getColor(), DyeColor.WHITE, "chalk is white");
        helper.assertTrue(sign.getFrontText().hasGlowingText(), "and bright");
        helper.assertValueEqual(SignBlock.getWoodType(BarFurniture.CHALKBOARD.get()), ChalkboardBlock.TYPE, "chalkboard look");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void aquariumTakesFishAndGivesThemBack(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, NurseryFurniture.AQUARIUM.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TROPICAL_FISH_BUCKET));
        helper.useBlock(pos, player);
        AquariumBlockEntity tank = helper.getBlockEntity(pos);
        helper.assertValueEqual(tank.count(), 1, "one fish");
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "an empty bucket comes back");
        helper.useBlock(pos, player);
        helper.assertValueEqual(tank.count(), 0, "scooped out");
        helper.assertTrue(player.getMainHandItem().is(Items.TROPICAL_FISH_BUCKET), "the fish is back in the bucket");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void birdCageKeepsAParrot(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, NurseryFurniture.BIRD_CAGE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Parrot parrot = EntityType.PARROT.create(helper.getLevel());
        CompoundTag bird = new CompoundTag();
        bird.putString("id", "minecraft:parrot");
        parrot.saveWithoutId(bird);
        player.setShoulderEntityLeft(bird);

        helper.useBlock(pos, player);
        BirdCageBlockEntity cage = helper.getBlockEntity(pos);
        helper.assertTrue(cage.hasBird(), "the parrot is in the cage");
        helper.assertTrue(player.getShoulderEntityLeft().isEmpty(), "and off the shoulder");
        helper.useBlock(pos, player);
        helper.assertFalse(cage.hasBird(), "let out");
        helper.assertEntityPresent(EntityType.PARROT);
        helper.succeed();
    }
}
