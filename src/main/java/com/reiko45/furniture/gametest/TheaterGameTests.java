package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.theater.MoviePosterBlock;
import com.reiko45.furniture.theater.ProjectorBlock;
import com.reiko45.furniture.theater.ProjectorScreenBlock;
import com.reiko45.furniture.theater.ReclinerBlock;
import com.reiko45.furniture.theater.SpeakerBlock;
import com.reiko45.furniture.theater.TheaterFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class TheaterGameTests {
    /** Puts up a screen facing north with its bottom middle at {@code main}, as placing the item does. */
    private static void placeScreen(GameTestHelper helper, BlockPos main) {
        BlockState state = TheaterFurniture.PROJECTOR_SCREEN.get().defaultBlockState()
                .setValue(ProjectorScreenBlock.FACING, Direction.NORTH);
        helper.getLevel().setBlock(helper.absolutePos(main), state, net.minecraft.world.level.block.Block.UPDATE_ALL);
        state.getBlock().setPlacedBy(helper.getLevel(), helper.absolutePos(main), state, null,
                new ItemStack(TheaterFurniture.PROJECTOR_SCREEN.get()));
    }

    private static void assertScreen(GameTestHelper helper, BlockPos main, int film) {
        BlockPos absolute = helper.absolutePos(main);
        for (ProjectorScreenBlock.Part part : ProjectorScreenBlock.Part.values()) {
            BlockState state = helper.getLevel().getBlockState(ProjectorScreenBlock.partPos(absolute, Direction.NORTH, part));
            helper.assertTrue(state.is(TheaterFurniture.PROJECTOR_SCREEN.get()) && state.getValue(ProjectorScreenBlock.PART) == part,
                    "screen part " + part.getSerializedName() + " in place");
            helper.assertValueEqual(state.getValue(ProjectorScreenBlock.LIT), film > 0, "part " + part.getSerializedName() + " lit");
            if (film > 0) {
                helper.assertValueEqual(state.getValue(ProjectorScreenBlock.FILM), film, "film");
            }
        }
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void projectorShowsFilmsOnTheScreen(GameTestHelper helper) {
        BlockPos main = new BlockPos(1, 1, 2);
        placeScreen(helper, main);
        assertScreen(helper, main, 0);

        // In front of the screen (north of it), lens pointing back south at it.
        BlockPos projector = main.north(2);
        helper.setBlock(projector, TheaterFurniture.PROJECTOR.get().defaultBlockState().setValue(ProjectorBlock.FACING, Direction.NORTH));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(projector, player);
        assertScreen(helper, main, 1);
        helper.useBlock(projector, player);
        assertScreen(helper, main, 2);

        // Breaking the projector blanks the screen.
        helper.setBlock(projector, Blocks.AIR);
        assertScreen(helper, main, 0);

        // Redstone works too.
        helper.setBlock(projector, TheaterFurniture.PROJECTOR.get().defaultBlockState().setValue(ProjectorBlock.FACING, Direction.NORTH));
        helper.setBlock(projector.west(), Blocks.REDSTONE_BLOCK);
        assertScreen(helper, main, 1);
        helper.setBlock(projector.west(), Blocks.AIR);
        assertScreen(helper, main, 0);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void projectorFacingAwayFindsNoScreen(GameTestHelper helper) {
        BlockPos main = new BlockPos(1, 1, 2);
        placeScreen(helper, main);
        BlockPos projector = main.north(2);
        // Facing south: the lens points north, away from the screen.
        helper.setBlock(projector, TheaterFurniture.PROJECTOR.get().defaultBlockState().setValue(ProjectorBlock.FACING, Direction.SOUTH));
        helper.useBlock(projector, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(projector, ProjectorBlock.LIT, true);
        assertScreen(helper, main, 0);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void screenBreaksAsAWhole(GameTestHelper helper) {
        BlockPos main = new BlockPos(1, 1, 2);
        placeScreen(helper, main);
        BlockPos absolute = helper.absolutePos(main);
        helper.getLevel().destroyBlock(ProjectorScreenBlock.partPos(absolute, Direction.NORTH, ProjectorScreenBlock.Part.TOP_RIGHT), true);
        for (ProjectorScreenBlock.Part part : ProjectorScreenBlock.Part.values()) {
            helper.assertFalse(helper.getLevel().getBlockState(ProjectorScreenBlock.partPos(absolute, Direction.NORTH, part))
                    .is(TheaterFurniture.PROJECTOR_SCREEN.get()), "part " + part.getSerializedName() + " gone");
        }
        int dropped = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(absolute).inflate(4))
                .stream().filter(item -> item.getItem().is(TheaterFurniture.PROJECTOR_SCREEN.get().asItem()))
                .mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertValueEqual(dropped, 1, "one screen dropped");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void reclinerTiltsBack(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, TheaterFurniture.RECLINER.get(DyeColor.RED).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, ReclinerBlock.RECLINED, true);
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, ReclinerBlock.RECLINED, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 300)
    public static void popcornMachineMakesPopcorn(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, TheaterFurniture.POPCORN_MACHINE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WHEAT_SEEDS, 2));
        helper.useBlock(pos, player);
        helper.useBlock(pos, player);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.PAPER));
        helper.useBlock(pos, player);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(TheaterFurniture.POPCORN.get(), pos, 2.0));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void postersAndSpeakers(GameTestHelper helper) {
        BlockPos poster = new BlockPos(0, 1, 0);
        helper.setBlock(poster, TheaterFurniture.MOVIE_POSTER.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int i = 1; i <= MoviePosterBlock.DESIGNS; i++) {
            helper.useBlock(poster, player);
            helper.assertBlockProperty(poster, MoviePosterBlock.DESIGN, i % MoviePosterBlock.DESIGNS);
        }

        BlockPos speaker = new BlockPos(2, 1, 2);
        helper.setBlock(speaker, TheaterFurniture.SPEAKER.get(com.reiko45.furniture.core.FurnitureWood.OAK).get());
        helper.assertFalse(SpeakerBlock.musicNear(helper.getLevel(), helper.absolutePos(speaker)), "quiet");
        helper.setBlock(new BlockPos(0, 1, 2), Blocks.JUKEBOX.defaultBlockState().setValue(JukeboxBlock.HAS_RECORD, true));
        helper.assertTrue(SpeakerBlock.musicNear(helper.getLevel(), helper.absolutePos(speaker)), "a record is playing");
        helper.succeed();
    }
}
