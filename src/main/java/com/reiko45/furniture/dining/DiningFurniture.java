package com.reiko45.furniture.dining;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Dining room section: dining table, dining chair, bench and sideboard, each in every wood type.
 * Models are in {@code models/block/template/dining/}.
 */
public final class DiningFurniture {
    private static final Room ROOM = Room.DINING_ROOM;

    // Shapes for a north-facing block, in pixels.
    private static final VoxelShape TABLE_TOP = boxes(box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape TABLE_LEG = boxes(box(1, 0, 1, 3, 14, 3));
    private static final VoxelShape CHAIR_SHAPE = boxes(box(2, 0, 2, 14, 9, 14), box(2.5, 9, 12.5, 13.5, 16, 14));
    private static final VoxelShape BENCH_SHAPE = boxes(box(0, 0, 2.5, 16, 8, 13.5));
    private static final VoxelShape SIDEBOARD_SHAPE = boxes(box(0.5, 0, 1, 15.5, 14, 15.5), box(0, 14, 0.5, 16, 16, 16));

    /** Joins with neighbouring tables of the same wood into one long table. */
    public static final WoodSet<ConnectingTableBlock> DINING_TABLE = FurnitureRegistry.woodSet("dining_table", ROOM,
            wood -> new ConnectingTableBlock(wood.properties(), TABLE_TOP, TABLE_LEG));

    public static final WoodSet<SeatBlock> DINING_CHAIR = FurnitureRegistry.woodSet("dining_chair", ROOM,
            wood -> new SeatBlock(wood.properties(), CHAIR_SHAPE, 9));

    public static final WoodSet<SeatBlock> BENCH = FurnitureRegistry.woodSet("bench", ROOM,
            wood -> new SeatBlock(wood.properties(), BENCH_SHAPE, 8));

    /** Low cupboard with three rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> SIDEBOARD = FurnitureRegistry.woodSet("sideboard", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 3, SIDEBOARD_SHAPE));

    private DiningFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
