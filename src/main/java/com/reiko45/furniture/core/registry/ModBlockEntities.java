package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Block entity register. Room-specific block entities (like the stove) are registered by their room. */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ReikosFurniture.MODID);

    /** Shared by every storage piece (nightstands, cabinets, wardrobes, the fridge, ...). */
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FurnitureStorageBlockEntity>> STORAGE =
            BLOCK_ENTITIES.register("storage", () -> BlockEntityType.Builder.of(
                    FurnitureStorageBlockEntity::new, storageBlocks()).build(null));

    /** Shared by every block that shows items (shelves, plates, cutting boards, feeding bowls). */
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayBlockEntity>> DISPLAY =
            BLOCK_ENTITIES.register("display", () -> BlockEntityType.Builder.of(
                    DisplayBlockEntity::new, blocksOfType(DisplayFurnitureBlock.class)).build(null));

    private ModBlockEntities() {
    }

    /** Every registered block of a class, in registration order. */
    public static Block[] blocksOfType(Class<?> type) {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(Holder::value)
                .filter(type::isInstance)
                .toArray(Block[]::new);
    }

    private static Block[] storageBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(Holder::value)
                .filter(block -> block instanceof FurnitureStorageBlock storage && storage.usesSharedStorage())
                .toArray(Block[]::new);
    }
}
