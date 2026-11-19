package com.reiko45.furniture.studio;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import com.reiko45.furniture.greenhouse.PlantHolderBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Art studio: easels in every wood and canvases to paint on them; a pottery wheel that throws vases
 * (terracotta and 16 glazes) for flowers; a sewing machine that sews floor cushions in every color; and
 * pots of paint in every color.
 */
public final class StudioFurniture {
    private static final Room ROOM = Room.STUDIO;

    private static final VoxelShape VASE_SHAPE = boxes(box(5, 0, 5, 11, 10, 11));
    private static final List<DisplaySlot> VASE_SLOT = List.of(DisplaySlot.plant(8, 9, 8, 0.75F));

    /** Holds a canvas to paint. */
    public static final WoodSet<EaselBlock> EASEL = FurnitureRegistry.woodSet("easel", ROOM,
            wood -> new EaselBlock(wood.properties(), boxes(box(2, 0, 4, 14, 16, 12))));

    /** Painted on an easel; hangs on a wall or goes in a picture frame. */
    public static final DeferredBlock<CanvasBlock> CANVAS = FurnitureRegistry.register("canvas", ROOM,
            () -> new CanvasBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.WOOL)
                    .noCollission()),
            (block, room) -> new CanvasItem(block, new Item.Properties().stacksTo(16), room));

    /** Throws vases from clay. */
    public static final DeferredBlock<ApplianceBlock> POTTERY_WHEEL = FurnitureRegistry.register("pottery_wheel", ROOM,
            () -> new ApplianceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD)
                    .noOcclusion(), boxes(box(1, 0, 1, 15, 11, 15)), ApplianceType.POTTERY_WHEEL));

    /** Holds a flower. */
    public static final DeferredBlock<PlantHolderBlock> TERRACOTTA_VASE = FurnitureRegistry.register("terracotta_vase", ROOM,
            () -> new PlantHolderBlock(vase(MapColor.TERRACOTTA_ORANGE), VASE_SHAPE, VASE_SLOT));

    public static final ColorSet<PlantHolderBlock> VASE = FurnitureRegistry.colorSet("vase", ROOM,
            color -> new PlantHolderBlock(vase(color.getMapColor()), VASE_SHAPE, VASE_SLOT), false);

    /** Sews floor cushions from wool and string. */
    public static final DeferredBlock<ApplianceBlock> SEWING_MACHINE = FurnitureRegistry.register("sewing_machine", ROOM,
            () -> new ApplianceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(1.5F).sound(SoundType.METAL)
                    .noOcclusion(), boxes(box(2, 0, 4, 14, 11, 12)), ApplianceType.SEWING_MACHINE));

    /** A low cushion to sit on. */
    public static final ColorSet<SeatBlock> FLOOR_CUSHION = FurnitureRegistry.colorSet("floor_cushion", ROOM,
            color -> new SeatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.5F).noOcclusion(), boxes(box(1, 0, 1, 15, 4, 15)), 3));

    /** A few pots of paint with brushes. */
    public static final ColorSet<FurnitureBlock> PAINT_POTS = FurnitureRegistry.colorSet("paint_pots", ROOM,
            color -> new FurnitureBlock(BlockBehaviour.Properties.of().mapColor(color.getMapColor()).strength(0.5F)
                    .sound(SoundType.LANTERN).noOcclusion(), boxes(box(2, 0, 2, 14, 7, 14))), false);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CanvasBlockEntity>> CANVAS_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("canvas", () -> BlockEntityType.Builder.of(CanvasBlockEntity::new,
                    CANVAS.get()).build(null));

    private StudioFurniture() {
    }

    public static void init() {
    }

    private static BlockBehaviour.Properties vase(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(0.5F).sound(SoundType.DECORATED_POT).noOcclusion();
    }
}
