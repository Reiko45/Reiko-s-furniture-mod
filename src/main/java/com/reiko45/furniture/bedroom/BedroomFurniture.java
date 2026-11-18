package com.reiko45.furniture.bedroom;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Bedroom section: nightstand, dresser, wardrobe, desk, table lamp, bed, mirror and vanity, each in every wood type.
 * Models are in {@code models/block/template/bedroom/}.
 */
public final class BedroomFurniture {
    private static final Room ROOM = Room.BEDROOM;

    // Shapes for a north-facing block, in pixels.
    private static final VoxelShape NIGHTSTAND_SHAPE = boxes(box(1, 0, 1, 15, 14, 15), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape DRESSER_SHAPE = boxes(box(0.5, 0, 1, 15.5, 14, 15.5), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape WARDROBE_LOWER_SHAPE = boxes(box(0, 0, 0.5, 16, 16, 16));
    private static final VoxelShape WARDROBE_UPPER_SHAPE = boxes(box(0.5, 0, 0.5, 15.5, 14, 16), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape DESK_SHAPE = boxes(box(0, 14, 0, 16, 16, 16), box(1, 0, 1, 3, 14, 15),
            box(9, 0, 0.5, 15, 14, 15), box(3, 6, 13.5, 9, 14, 14.5));
    private static final VoxelShape TABLE_LAMP_SHAPE = boxes(box(4.5, 0, 4.5, 11.5, 11, 11.5));
    private static final VoxelShape MIRROR_SHAPE = boxes(box(1, 1, 14, 15, 15, 16));
    private static final VoxelShape VANITY_SHAPE = boxes(box(0, 0, 0, 16, 11, 16), box(2, 11, 13, 14, 16, 15.5));

    /** Small bedside table with one row of storage. */
    public static final WoodSet<FurnitureStorageBlock> NIGHTSTAND = FurnitureRegistry.woodSet("nightstand", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 1, NIGHTSTAND_SHAPE));

    /** Chest of drawers with three rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> DRESSER = FurnitureRegistry.woodSet("dresser", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 3, DRESSER_SHAPE));

    /** Two-block-tall wardrobe with six rows of storage (as much as a double chest). */
    public static final WoodSet<TallStorageFurnitureBlock> WARDROBE = FurnitureRegistry.woodSet("wardrobe", ROOM,
            wood -> new TallStorageFurnitureBlock(wood.properties(), 6, WARDROBE_LOWER_SHAPE, WARDROBE_UPPER_SHAPE));

    public static final WoodSet<FurnitureBlock> DESK = FurnitureRegistry.woodSet("desk", ROOM,
            wood -> new FurnitureBlock(wood.properties(), DESK_SHAPE));

    /** Right-click to switch on and off. */
    public static final WoodSet<SwitchableFurnitureBlock> TABLE_LAMP = FurnitureRegistry.woodSet("table_lamp", ROOM,
            wood -> new SwitchableFurnitureBlock(
                    wood.properties().lightLevel(SwitchableFurnitureBlock.lightWhenOn(15)), TABLE_LAMP_SHAPE));

    /** A bed you can sleep in. Right-click with dye to change the blanket color. */
    public static final WoodSet<FurnitureBedBlock> BED = FurnitureRegistry.woodSet("bed", ROOM,
            wood -> new FurnitureBedBlock(wood.properties().strength(0.2F).pushReaction(PushReaction.DESTROY)));

    /** Wall mirror. */
    public static final WoodSet<FurnitureBlock> MIRROR = FurnitureRegistry.woodSet("mirror", ROOM,
            wood -> new FurnitureBlock(wood.properties(), MIRROR_SHAPE));

    /** Small table with a mirror and a drawer (one row of storage). */
    public static final WoodSet<FurnitureStorageBlock> VANITY = FurnitureRegistry.woodSet("vanity", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 1, VANITY_SHAPE));

    /** Wall-mounted lamp; switch it with a right-click or redstone. */
    public static final WoodSet<SwitchableFurnitureBlock> WALL_LAMP = FurnitureRegistry.woodSet("wall_lamp", ROOM,
            wood -> new SwitchableFurnitureBlock(wood.properties().lightLevel(SwitchableFurnitureBlock.lightWhenOn(13)),
                    boxes(box(5, 4, 9, 11, 12, 16))));

    private BedroomFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
