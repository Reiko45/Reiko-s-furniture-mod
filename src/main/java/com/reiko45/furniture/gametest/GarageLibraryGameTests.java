package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.garage.GarageFurniture;
import com.reiko45.furniture.garage.WorkshopBenchBlock;
import com.reiko45.furniture.library.GlobeBlock;
import com.reiko45.furniture.library.LibraryFurniture;
import com.reiko45.furniture.library.LibraryLadderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the garage and the library. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class GarageLibraryGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void pegboardHoldsSixTools(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, GarageFurniture.PEGBOARD.get(FurnitureWood.OAK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE, 8));
        helper.useBlock(pos, player);
        DisplayBlockEntity board = helper.getBlockEntity(pos);
        helper.assertTrue(board.isEmpty(), "stone isn't a tool");
        for (var tool : new net.minecraft.world.item.Item[] {Items.IRON_PICKAXE, Items.SHEARS, Items.IRON_SWORD, Items.FISHING_ROD,
                Items.STONE_AXE, Items.WOODEN_HOE, Items.BOW}) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tool));
            helper.useBlock(pos, player);
        }
        helper.assertFalse(board.hasSpace(), "six tools fill it");
        helper.assertTrue(player.getMainHandItem().is(Items.BOW), "the seventh doesn't fit");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void workshopBenchIsACraftingTable(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        WorkshopBenchBlock bench = GarageFurniture.WORKSHOP_BENCH.get(FurnitureWood.SPRUCE).get();
        helper.setBlock(pos, bench);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(helper.absoluteVec(new Vec3(1.5, 1, 2.5)));
        var menu = new WorkshopBenchBlock.BenchMenu(1, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(pos)), bench);
        helper.assertTrue(menu.stillValid(player), "the crafting grid stays open at the bench");
        helper.setBlock(pos, Blocks.AIR);
        helper.assertFalse(menu.stillValid(player), "and closes when the bench is gone");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tallBookcasesPowerEnchanting(GameTestHelper helper) {
        BlockPos table = new BlockPos(3, 1, 3);
        helper.setBlock(table, Blocks.ENCHANTING_TABLE);
        BlockState bookcase = LibraryFurniture.TALL_BOOKCASE.get(FurnitureWood.DARK_OAK).get().defaultBlockState();
        helper.setBlock(new BlockPos(1, 1, 3), bookcase);
        helper.setBlock(new BlockPos(1, 2, 3), bookcase.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        BlockPos absolute = helper.absolutePos(table);
        helper.assertTrue(EnchantingTableBlock.isValidBookShelf(helper.getLevel(), absolute, new BlockPos(-2, 0, 0)), "lower half");
        helper.assertTrue(EnchantingTableBlock.isValidBookShelf(helper.getLevel(), absolute, new BlockPos(-2, 1, 0)), "upper half");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void libraryLadderClimbsAndSlides(GameTestHelper helper) {
        LibraryLadderBlock ladder = LibraryFurniture.LIBRARY_LADDER.get(FurnitureWood.OAK).get();
        helper.assertTrue(ladder.defaultBlockState().is(BlockTags.CLIMBABLE), "climbable");
        // Two bookcase blocks (stone here) to lean on, at z = 2; the ladder faces north in front of the first.
        helper.setBlock(new BlockPos(1, 1, 2), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.STONE);
        helper.setBlock(new BlockPos(1, 2, 2), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 2, 2), Blocks.STONE);
        BlockState state = ladder.defaultBlockState().setValue(LibraryLadderBlock.FACING, Direction.NORTH);
        helper.setBlock(new BlockPos(1, 1, 1), state);
        helper.setBlock(new BlockPos(1, 2, 1), state);
        // Seen from the front (north), left is east: the whole column slides to x = 2.
        helper.assertTrue(ladder.slide(helper.getLevel(), helper.absolutePos(new BlockPos(1, 1, 1)), state, true), "slides");
        helper.assertBlockPresent(ladder, new BlockPos(2, 1, 1));
        helper.assertBlockPresent(ladder, new BlockPos(2, 2, 1));
        helper.assertBlockNotPresent(ladder, new BlockPos(1, 2, 1));
        // Nothing to lean on further east.
        helper.assertFalse(ladder.slide(helper.getLevel(), helper.absolutePos(new BlockPos(2, 1, 1)), state, true), "stops");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 400)
    public static void globeSpinsAndStops(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, LibraryFurniture.GLOBE.get(FurnitureWood.BIRCH).get());
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, GlobeBlock.SPEED, 3);
        helper.runAfterDelay(3, () -> helper.assertFalse(helper.getBlockState(pos).getValue(GlobeBlock.ANGLE) == 0, "it turned"));
        helper.succeedWhen(() -> helper.assertBlockProperty(pos, GlobeBlock.SPEED, 0));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void readingLampAndBookStand(GameTestHelper helper) {
        BlockPos lamp = new BlockPos(1, 1, 1);
        helper.setBlock(lamp, LibraryFurniture.READING_LAMP.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(lamp, player);
        helper.assertBlockProperty(lamp, SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(lamp).getLightEmission(helper.getLevel(), helper.absolutePos(lamp)), 12, "light");

        BlockPos stand = new BlockPos(3, 1, 1);
        helper.setBlock(stand, LibraryFurniture.BOOK_STAND.get(FurnitureWood.OAK).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE));
        helper.useBlock(stand, player);
        DisplayBlockEntity display = helper.getBlockEntity(stand);
        helper.assertTrue(display.isEmpty(), "an apple isn't a book");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.ENCHANTED_BOOK));
        helper.useBlock(stand, player);
        helper.assertTrue(display.getItems().get(0).is(Items.ENCHANTED_BOOK), "the book is on the stand");
        helper.succeed();
    }
}
