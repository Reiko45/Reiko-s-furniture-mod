package com.reiko45.furniture.music;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.SeatBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Music room section: piano, piano bench and record player in every wood, and a drum kit. */
public final class MusicFurniture {
    private static final Room ROOM = Room.MUSIC;

    /** Right-click the keys to play notes. */
    public static final WoodSet<PianoBlock> PIANO = FurnitureRegistry.woodSet("piano", ROOM,
            wood -> new PianoBlock(wood.properties(), boxes(box(0, 0, 8, 16, 16, 16), box(0, 0, 3, 16, 8.5, 8))));

    public static final WoodSet<SeatBlock> PIANO_BENCH = FurnitureRegistry.woodSet("piano_bench", ROOM,
            wood -> new SeatBlock(wood.properties(), boxes(box(1.5, 0, 3.5, 14.5, 9, 12.5)), 9));

    /** Plays music discs like a jukebox. */
    public static final WoodSet<RecordPlayerBlock> RECORD_PLAYER = FurnitureRegistry.woodSet("record_player", ROOM,
            wood -> new RecordPlayerBlock(wood.properties()));

    /** Hit the cymbal, snare or bass drum. */
    public static final DeferredBlock<DrumKitBlock> DRUM_KIT = FurnitureRegistry.register("drum_kit", ROOM,
            () -> new DrumKitBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.0F)
                    .sound(SoundType.WOOD).noOcclusion(), boxes(box(1, 0, 1.5, 15.5, 11.5, 11))));

    private MusicFurniture() {
    }

    public static void init() {
    }
}
