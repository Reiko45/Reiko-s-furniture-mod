package com.reiko45.furniture.livingroom;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Living room section: sofas, armchairs, rugs and curtains in every color; coffee table, bookshelf,
 * TV stand and floor lamp in every wood type; a TV and a fireplace. Models are in {@code models/block/template/livingroom/}.
 */
public final class LivingRoomFurniture {
    private static final Room ROOM = Room.LIVING_ROOM;

    // Shapes for a north-facing block, in pixels.
    private static final VoxelShape COFFEE_TABLE_TOP = boxes(box(0, 7, 0, 16, 9, 16));
    private static final VoxelShape COFFEE_TABLE_LEG = boxes(box(1, 0, 1, 3, 7, 3));
    private static final VoxelShape FULL = boxes(box(0, 0, 0, 16, 16, 16));
    private static final VoxelShape TV_STAND_SHAPE = boxes(box(0, 0, 0.5, 16, 14, 16), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape TV_SHAPE = boxes(box(4, 0, 6, 12, 3, 10), box(0, 3, 6.75, 16, 13, 8.5));
    private static final VoxelShape FLOOR_LAMP_LOWER = boxes(box(5, 0, 5, 11, 1, 11), box(7.5, 1, 7.5, 8.5, 16, 8.5));
    private static final VoxelShape FLOOR_LAMP_UPPER = boxes(box(7.5, 0, 7.5, 8.5, 6, 8.5), box(3.5, 6, 3.5, 12.5, 14, 12.5));
    private static final VoxelShape ARMCHAIR_SHAPE = boxes(box(1, 0, 1, 15, 10, 15), box(2.5, 10, 12, 13.5, 15, 15));

    /** Sit on it. Sofas facing the same way join into one long sofa. */
    public static final ColorSet<SofaBlock> SOFA = FurnitureRegistry.colorSet("sofa", ROOM,
            color -> new SofaBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .mapColor(color.getMapColor()).strength(0.8F).noOcclusion()));

    /** Low table that joins with neighbouring coffee tables of the same wood. */
    public static final WoodSet<ConnectingTableBlock> COFFEE_TABLE = FurnitureRegistry.woodSet("coffee_table", ROOM,
            wood -> new ConnectingTableBlock(wood.properties(), COFFEE_TABLE_TOP, COFFEE_TABLE_LEG));

    /** Two rows of storage, and it powers enchanting tables like a vanilla bookshelf. */
    public static final WoodSet<FurnitureStorageBlock> BOOKSHELF = FurnitureRegistry.woodSet("bookshelf", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 2, FULL));

    /** Open shelf with a cupboard below, one row of storage. A TV fits on top. */
    public static final WoodSet<FurnitureStorageBlock> TV_STAND = FurnitureRegistry.woodSet("tv_stand", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 1, TV_STAND_SHAPE));

    /** Two-block-tall lamp, right-click to switch on and off. */
    public static final WoodSet<FloorLampBlock> FLOOR_LAMP = FurnitureRegistry.woodSet("floor_lamp", ROOM,
            wood -> new FloorLampBlock(wood.properties().lightLevel(FloorLampBlock::lightLevel),
                    FLOOR_LAMP_LOWER, FLOOR_LAMP_UPPER));

    /** Flat-screen TV with four channels. Right-click to switch channel; redstone switches it on and off. */
    public static final DeferredBlock<TvBlock> TV = FurnitureRegistry.register("tv", ROOM,
            () -> new TvBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(10)), TV_SHAPE));

    /** Cushioned chair with wooden arms. Sit on it. */
    public static final ColorSet<SeatBlock> ARMCHAIR = FurnitureRegistry.colorSet("armchair", ROOM,
            color -> new SeatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .mapColor(color.getMapColor()).strength(0.8F).noOcclusion(), ARMCHAIR_SHAPE, 7));

    /** Flat rug; rugs of the same color join into one big rug. */
    public static final ColorSet<RugBlock> RUG = FurnitureRegistry.colorSet("rug", ROOM,
            color -> new RugBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CARPET).mapColor(color.getMapColor())));

    /** Right-click to open or close. */
    public static final ColorSet<CurtainBlock> CURTAINS = FurnitureRegistry.colorSet("curtains", ROOM,
            color -> new CurtainBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .mapColor(color.getMapColor()).strength(0.5F).noOcclusion().noCollission()));

    /** Brick fireplace; light it with flint and steel or a right-click. */
    public static final DeferredBlock<FireplaceBlock> FIREPLACE = FurnitureRegistry.register("fireplace", ROOM,
            () -> new FireplaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
                    .noOcclusion()
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(15)), FULL));

    private LivingRoomFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
