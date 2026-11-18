package com.reiko45.furniture.bathroom;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.kitchen.SinkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Bathroom section: toilet, bathtub, shower, sink and mirror cabinet in white ceramic, and towel racks
 * in every color. Models are in {@code models/block/template/bathroom/}.
 */
public final class BathroomFurniture {
    private static final Room ROOM = Room.BATHROOM;

    private static final VoxelShape TOILET_SHAPE = boxes(box(3, 0, 2, 13, 9, 12), box(2, 0, 11.5, 14, 16, 16));
    private static final VoxelShape SINK_SHAPE = boxes(box(6, 0, 7, 10, 10, 12), box(2, 10, 4, 14, 14, 15));
    private static final VoxelShape CABINET_SHAPE = boxes(box(2, 2, 10.5, 14, 14, 16));
    private static final VoxelShape TOWEL_RACK_SHAPE = boxes(box(1, 3, 12.5, 15, 13, 16));

    private static BlockBehaviour.Properties ceramic() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(1.5F).sound(SoundType.STONE).noOcclusion();
    }

    /** Sit on it; sneak and right-click to flush. */
    public static final DeferredBlock<ToiletBlock> TOILET = FurnitureRegistry.register("toilet", ROOM,
            () -> new ToiletBlock(ceramic(), TOILET_SHAPE));

    /** Joins into longer tubs; fill it with water and sit in it. */
    public static final DeferredBlock<BathtubBlock> BATHTUB = FurnitureRegistry.register("bathtub", ROOM,
            () -> new BathtubBlock(ceramic()));

    /** Two blocks tall; turn the water on and off. Puts out burning players. */
    public static final DeferredBlock<ShowerBlock> SHOWER = FurnitureRegistry.register("shower", ROOM,
            () -> new ShowerBlock(ceramic()));

    /** Pedestal sink: fills buckets and bottles with water, like the kitchen sink. */
    public static final DeferredBlock<SinkBlock> BATHROOM_SINK = FurnitureRegistry.register("bathroom_sink", ROOM,
            () -> new SinkBlock(ceramic(), SINK_SHAPE));

    /** Wall cabinet with a mirror door, one row of storage. */
    public static final DeferredBlock<FurnitureStorageBlock> BATHROOM_CABINET = FurnitureRegistry.register("bathroom_cabinet", ROOM,
            () -> new FurnitureStorageBlock(ceramic(), 1, CABINET_SHAPE));

    /** Towel hanging on a metal rail. */
    public static final ColorSet<FurnitureBlock> TOWEL_RACK = FurnitureRegistry.colorSet("towel_rack", ROOM,
            color -> new FurnitureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .mapColor(color.getMapColor()).strength(0.5F).noOcclusion().noCollission(), TOWEL_RACK_SHAPE));

    private BathroomFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
