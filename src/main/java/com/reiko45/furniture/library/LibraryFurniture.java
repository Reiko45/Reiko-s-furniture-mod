package com.reiko45.furniture.library;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Library section: tall bookcases, ladders, globes, book stands and writing desks in every wood; a reading lamp. */
public final class LibraryFurniture {
    private static final Room ROOM = Room.LIBRARY;

    /** Two blocks tall, six rows of storage; both halves power enchanting tables. */
    public static final WoodSet<TallStorageFurnitureBlock> TALL_BOOKCASE = FurnitureRegistry.woodSet("tall_bookcase", ROOM,
            wood -> new TallStorageFurnitureBlock(wood.properties(), 6, boxes(box(0, 0, 0, 16, 16, 16)), boxes(box(0, 0, 0, 16, 16, 16))));

    /** Climbable; slides sideways along the bookcases. */
    public static final WoodSet<LibraryLadderBlock> LIBRARY_LADDER = FurnitureRegistry.woodSet("library_ladder", ROOM,
            wood -> new LibraryLadderBlock(wood.properties()));

    /** Right-click to spin it. */
    public static final WoodSet<GlobeBlock> GLOBE = FurnitureRegistry.woodSet("globe", ROOM,
            wood -> new GlobeBlock(wood.properties(), boxes(box(4, 0, 4, 12, 14.5, 12))));

    /** Shows one book. */
    public static final WoodSet<BookStandBlock> BOOK_STAND = FurnitureRegistry.woodSet("book_stand", ROOM,
            wood -> new BookStandBlock(wood.properties()));

    /** One row of storage in its drawer. */
    public static final WoodSet<FurnitureStorageBlock> WRITING_DESK = FurnitureRegistry.woodSet("writing_desk", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 1, boxes(box(0, 12, 1, 16, 13.5, 15), box(1, 0, 2, 15, 12, 14))));

    /** Brass lamp with a green glass shade. */
    public static final DeferredBlock<SwitchableFurnitureBlock> READING_LAMP = FurnitureRegistry.register("reading_lamp", ROOM,
            () -> new SwitchableFurnitureBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(1.0F)
                    .sound(SoundType.METAL).noOcclusion().lightLevel(SwitchableFurnitureBlock.lightWhenOn(12)),
                    boxes(box(3, 0, 6, 13, 8.5, 10))));

    private LibraryFurniture() {
    }

    public static void init() {
    }
}
