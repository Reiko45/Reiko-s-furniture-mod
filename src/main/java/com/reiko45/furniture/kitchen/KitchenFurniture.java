package com.reiko45.furniture.kitchen;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
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
 * Kitchen section: counter, upper cabinet and sink in every wood type, plus a stove and a fridge.
 * Models are in {@code models/block/template/kitchen/}.
 */
public final class KitchenFurniture {
    private static final Room ROOM = Room.KITCHEN;

    // Shapes for a north-facing block, in pixels.
    private static final VoxelShape FULL = boxes(box(0, 0, 0, 16, 16, 16));
    private static final VoxelShape UPPER_CABINET_SHAPE = boxes(box(0, 4, 7.5, 16, 16, 16));

    /** Kitchen counter with a stone top and three rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> COUNTER = FurnitureRegistry.woodSet("counter", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 3, FULL));

    /** Wall cabinet that hangs at the top of the block, two rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> UPPER_CABINET = FurnitureRegistry.woodSet("upper_cabinet", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 2, UPPER_CABINET_SHAPE));

    /** Fills buckets and glass bottles with water. */
    public static final WoodSet<SinkBlock> SINK = FurnitureRegistry.woodSet("sink", ROOM,
            wood -> new SinkBlock(wood.properties(), FULL));

    /** Cooks food like a smoker. */
    public static final DeferredBlock<StoveBlock> STOVE = FurnitureRegistry.register("stove", ROOM,
            () -> new StoveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(13))));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StoveBlockEntity>> STOVE_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("stove",
                    () -> BlockEntityType.Builder.of(StoveBlockEntity::new, STOVE.get()).build(null));

    /** Two-block-tall fridge with six rows of storage. */
    public static final DeferredBlock<FridgeBlock> FRIDGE = FurnitureRegistry.register("fridge", ROOM,
            () -> new FridgeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion(), 6, FULL, FULL));

    private KitchenFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
