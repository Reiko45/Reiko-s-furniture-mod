package com.reiko45.furniture.garage;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Garage section: pegboards and workshop benches in every wood, toolboxes and oil drums in every color, metal shelving. */
public final class GarageFurniture {
    private static final Room ROOM = Room.GARAGE;

    private static BlockBehaviour.Properties metal(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(2.0F).sound(SoundType.METAL).noOcclusion();
    }

    /** Hang up to six tools on the wall. */
    public static final WoodSet<PegboardBlock> PEGBOARD = FurnitureRegistry.woodSet("pegboard", ROOM,
            wood -> new PegboardBlock(wood.properties()));

    /** Craft on it like on a crafting table. */
    public static final WoodSet<WorkshopBenchBlock> WORKSHOP_BENCH = FurnitureRegistry.woodSet("workshop_bench", ROOM,
            wood -> new WorkshopBenchBlock(wood.properties(), boxes(box(0, 13, 1, 16, 16, 15), box(1, 0, 2, 15, 13, 14), box(1.5, 12, -1, 5.5, 16, 1))));

    /** One row of storage. */
    public static final ColorSet<ToolboxBlock> TOOLBOX = FurnitureRegistry.colorSet("toolbox", ROOM,
            color -> new ToolboxBlock(metal(color.getMapColor()), 1, boxes(box(2.75, 0, 4.75, 13.25, 8.25, 11.25))), false);

    /** One row of storage. */
    public static final ColorSet<ToolboxBlock> OIL_DRUM = FurnitureRegistry.colorSet("oil_drum", ROOM,
            color -> new ToolboxBlock(metal(color.getMapColor()), 1, boxes(box(1, 0, 1, 15, 16, 15))), false);

    /** Three rows of storage. */
    public static final DeferredBlock<FurnitureStorageBlock> METAL_SHELVING = FurnitureRegistry.register("metal_shelving", ROOM,
            () -> new FurnitureStorageBlock(metal(MapColor.METAL), 3, boxes(box(1, 0, 2, 15, 16, 14))));

    private GarageFurniture() {
    }

    public static void init() {
    }
}
