package com.reiko45.furniture.outdoor;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Outdoor section: patio table, patio chair, garden bench and planter box in every wood type, and a
 * grill. Models are in {@code models/block/template/outdoor/}.
 */
public final class OutdoorFurniture {
    private static final Room ROOM = Room.OUTDOOR;

    private static final VoxelShape TABLE_TOP = boxes(box(0, 14, 0, 16, 15.5, 16));
    private static final VoxelShape TABLE_LEG = boxes(box(1, 0, 1, 3, 14, 3));
    private static final VoxelShape CHAIR_SHAPE = boxes(box(2, 0, 2, 14, 8, 14), box(2, 8, 12.5, 14, 15, 14),
            box(2, 8, 2, 3.5, 11, 14), box(12.5, 8, 2, 14, 11, 14));
    private static final VoxelShape BENCH_SHAPE = boxes(box(0, 0, 2, 16, 7, 14), box(0.5, 7, 12, 15.5, 15, 14));
    private static final VoxelShape GRILL_SHAPE = boxes(box(1.5, 0, 1.5, 14.5, 11, 14.5));

    /** Slatted garden table; joins with neighbouring patio tables of the same wood. */
    public static final WoodSet<ConnectingTableBlock> PATIO_TABLE = FurnitureRegistry.woodSet("patio_table", ROOM,
            wood -> new ConnectingTableBlock(wood.properties(), TABLE_TOP, TABLE_LEG));

    public static final WoodSet<SeatBlock> PATIO_CHAIR = FurnitureRegistry.woodSet("patio_chair", ROOM,
            wood -> new SeatBlock(wood.properties(), CHAIR_SHAPE, 8));

    public static final WoodSet<SeatBlock> GARDEN_BENCH = FurnitureRegistry.woodSet("garden_bench", ROOM,
            wood -> new SeatBlock(wood.properties(), BENCH_SHAPE, 7));

    /** Plant flowers, saplings and crops on top; crops grow like on watered farmland. */
    public static final WoodSet<PlanterBoxBlock> PLANTER_BOX = FurnitureRegistry.woodSet("planter_box", ROOM,
            wood -> new PlanterBoxBlock(wood.properties()));

    /** Barbecue: put food on it and light it. Cooks like a campfire, twice as fast. */
    public static final DeferredBlock<GrillBlock> GRILL = FurnitureRegistry.register("grill", ROOM,
            () -> new GrillBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(10)), GRILL_SHAPE));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrillBlockEntity>> GRILL_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("grill",
                    () -> BlockEntityType.Builder.of(GrillBlockEntity::new, GRILL.get()).build(null));

    private OutdoorFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
