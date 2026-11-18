package com.reiko45.furniture.laundry;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Laundry room section: washing machine, dryer and laundry baskets. */
public final class LaundryFurniture {
    private static final Room ROOM = Room.LAUNDRY;
    private static final VoxelShape MACHINE_SHAPE = boxes(box(0.5, 0, 1, 15.5, 16, 15.5));

    private static BlockBehaviour.Properties machine() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(2.0F).sound(SoundType.METAL).noOcclusion();
    }

    /** Takes the dye out of clothes, wool, banners and shulker boxes. */
    public static final DeferredBlock<LaundryMachineBlock> WASHING_MACHINE = FurnitureRegistry.register("washing_machine", ROOM,
            () -> new LaundryMachineBlock(machine(), MACHINE_SHAPE, LaundryMachineBlock.Kind.WASHER));

    /** Dries wet sponges. */
    public static final DeferredBlock<LaundryMachineBlock> DRYER = FurnitureRegistry.register("dryer", ROOM,
            () -> new LaundryMachineBlock(machine(), MACHINE_SHAPE, LaundryMachineBlock.Kind.DRYER));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaundryMachineBlockEntity>> LAUNDRY_MACHINE_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("laundry_machine", () -> BlockEntityType.Builder.of(
                    LaundryMachineBlockEntity::new, WASHING_MACHINE.get(), DRYER.get()).build(null));

    /** Woven basket, one row of storage. */
    public static final WoodSet<FurnitureStorageBlock> LAUNDRY_BASKET = FurnitureRegistry.woodSet("laundry_basket", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 1, boxes(box(1.5, 0, 3, 14.5, 9, 13))));

    private LaundryFurniture() {
    }

    public static void init() {
    }
}
