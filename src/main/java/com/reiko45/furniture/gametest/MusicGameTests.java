package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.music.DrumKitBlock;
import com.reiko45.furniture.music.MusicFurniture;
import com.reiko45.furniture.music.PianoBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;

/** Tests for the music room. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class MusicGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void pianoKeysGoUpFromLeftToRight(GameTestHelper helper) {
        // A piano facing south is played by someone looking north: their left is west (low x).
        helper.assertValueEqual(PianoBlock.noteFor(Direction.SOUTH, new Vec3(0.0, 0.5, 0.2)), 0, "far left");
        helper.assertValueEqual(PianoBlock.noteFor(Direction.SOUTH, new Vec3(0.5, 0.5, 0.2)), 12, "middle");
        helper.assertValueEqual(PianoBlock.noteFor(Direction.SOUTH, new Vec3(1.0, 0.5, 0.2)), 24, "far right");
        // Facing north, the player looks south and their left is east.
        helper.assertValueEqual(PianoBlock.noteFor(Direction.NORTH, new Vec3(1.0, 0.5, 0.2)), 0, "far left");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void drumKitPartsSoundDifferent(GameTestHelper helper) {
        helper.assertValueEqual(DrumKitBlock.drumFor(0.65), NoteBlockInstrument.HAT, "cymbal");
        helper.assertValueEqual(DrumKitBlock.drumFor(0.5), NoteBlockInstrument.SNARE, "snare");
        helper.assertValueEqual(DrumKitBlock.drumFor(0.2), NoteBlockInstrument.BASEDRUM, "bass drum");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void recordPlayerPlaysDiscs(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, MusicFurniture.RECORD_PLAYER.get(FurnitureWood.SPRUCE).get());
        helper.assertTrue(helper.getBlockEntity(pos) instanceof JukeboxBlockEntity, "uses the jukebox block entity");
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MUSIC_DISC_CAT));
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, JukeboxBlock.HAS_RECORD, true);
        helper.assertTrue(player.getMainHandItem().isEmpty(), "the disc went in");
        JukeboxBlockEntity jukebox = helper.getBlockEntity(pos);
        helper.assertTrue(jukebox.getSongPlayer().isPlaying(), "the song should be playing");

        // An empty hand takes it back out.
        helper.useBlock(pos, player);
        helper.assertBlockProperty(pos, JukeboxBlock.HAS_RECORD, false);
        helper.succeed();
    }
}
