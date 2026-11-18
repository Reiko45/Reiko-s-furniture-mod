package com.reiko45.furniture.office;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Office section: office chairs in every color, filing cabinets in every wood type, a computer and
 * a desk lamp. Models are in {@code models/block/template/office/}.
 */
public final class OfficeFurniture {
    private static final Room ROOM = Room.OFFICE;

    private static final VoxelShape CHAIR_SHAPE = boxes(box(2, 0, 2, 14, 9, 14), box(2.5, 9, 12.5, 13.5, 16, 14));
    private static final VoxelShape FILING_CABINET_SHAPE = boxes(box(1, 0, 1, 15, 16, 15.5));
    private static final VoxelShape COMPUTER_SHAPE = boxes(box(2, 0, 9, 14, 12, 13), box(2.5, 0, 2, 14.5, 1, 6));
    private static final VoxelShape DESK_LAMP_SHAPE = boxes(box(5, 0, 7, 11, 1, 13), box(5.5, 1, 2.5, 10.5, 9, 10.5));

    private static BlockBehaviour.Properties metal() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL).noOcclusion();
    }

    /** Swivel chair on wheels. Sit on it. */
    public static final ColorSet<SeatBlock> OFFICE_CHAIR = FurnitureRegistry.colorSet("office_chair", ROOM,
            color -> new SeatBlock(metal().mapColor(color.getMapColor()).sound(SoundType.WOOL), CHAIR_SHAPE, 9));

    /** Three drawers, three rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> FILING_CABINET = FurnitureRegistry.woodSet("filing_cabinet", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 3, FILING_CABINET_SHAPE));

    /** Right-click or use redstone to switch it on. */
    public static final DeferredBlock<SwitchableFurnitureBlock> COMPUTER = FurnitureRegistry.register("computer", ROOM,
            () -> new SwitchableFurnitureBlock(metal().lightLevel(SwitchableFurnitureBlock.lightWhenOn(6)), COMPUTER_SHAPE));

    /** Small adjustable lamp. Right-click or use redstone to switch it on. */
    public static final DeferredBlock<SwitchableFurnitureBlock> DESK_LAMP = FurnitureRegistry.register("desk_lamp", ROOM,
            () -> new SwitchableFurnitureBlock(metal().lightLevel(SwitchableFurnitureBlock.lightWhenOn(13)), DESK_LAMP_SHAPE));

    private OfficeFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
