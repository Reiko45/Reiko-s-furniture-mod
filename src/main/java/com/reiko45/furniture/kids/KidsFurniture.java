package com.reiko45.furniture.kids;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.registry.ModBlocks;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Kids' room section: toy chest, kids' table, kids' chair, rocking horse and bunk bed, each in every
 * wood type. Models are in {@code models/block/template/kids/}.
 */
public final class KidsFurniture {
    private static final Room ROOM = Room.KIDS_ROOM;

    private static final VoxelShape TOY_CHEST_SHAPE = boxes(box(1, 0, 1.5, 15, 11, 14));
    private static final VoxelShape TABLE_TOP = boxes(box(0, 9, 0, 16, 10.5, 16));
    private static final VoxelShape TABLE_LEG = boxes(box(1.5, 0, 1.5, 3.5, 9, 3.5));
    private static final VoxelShape CHAIR_SHAPE = boxes(box(3, 0, 3, 13, 6.5, 13), box(3.5, 6.5, 11.25, 12.5, 13, 12.5));
    private static final VoxelShape HORSE_SHAPE = boxes(box(3, 0, 0, 13, 10.5, 16), box(5.5, 10, -1.5, 10.5, 15.5, 4.5));

    /** Chest with a lid that opens; three rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> TOY_CHEST = FurnitureRegistry.woodSet("toy_chest", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 3, TOY_CHEST_SHAPE));

    /** Low table; joins with kids' tables of the same wood. */
    public static final WoodSet<ConnectingTableBlock> KIDS_TABLE = FurnitureRegistry.woodSet("kids_table", ROOM,
            wood -> new ConnectingTableBlock(wood.properties(), TABLE_TOP, TABLE_LEG));

    public static final WoodSet<SeatBlock> KIDS_CHAIR = FurnitureRegistry.woodSet("kids_chair", ROOM,
            wood -> new SeatBlock(wood.properties(), CHAIR_SHAPE, 6.5));

    public static final WoodSet<SeatBlock> ROCKING_HORSE = FurnitureRegistry.woodSet("rocking_horse", ROOM,
            wood -> new SeatBlock(wood.properties(), HORSE_SHAPE, 10.5));

    /** The decorative top bunks; they have no item and are placed by the bunk bed. */
    public static final Map<FurnitureWood, DeferredBlock<BunkTopBlock>> BUNK_BED_TOP;

    /** Bunk bed: sleep in the bottom bunk; right-click with dye to change both blankets. */
    public static final WoodSet<BunkBedBlock> BUNK_BED;

    static {
        Map<FurnitureWood, DeferredBlock<BunkTopBlock>> tops = new EnumMap<>(FurnitureWood.class);
        BUNK_BED = FurnitureRegistry.woodSet("bunk_bed", ROOM, wood -> new BunkBedBlock(
                wood.properties().strength(0.2F).pushReaction(PushReaction.DESTROY), () -> tops.get(wood).get()));
        for (FurnitureWood wood : FurnitureWood.values()) {
            tops.put(wood, ModBlocks.BLOCKS.register(wood.id() + "_bunk_bed_top", () -> new BunkTopBlock(
                    wood.properties().strength(0.2F).pushReaction(PushReaction.DESTROY), () -> BUNK_BED.get(wood).get())));
        }
        BUNK_BED_TOP = Collections.unmodifiableMap(tops);
    }

    private KidsFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
