package com.reiko45.furniture.core.workbench;

import com.reiko45.furniture.core.FurnitureRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

/** The furniture workbench block. It belongs to no room, so it is registered after all rooms. */
public final class Workbench {
    public static final DeferredBlock<FurnitureWorkbenchBlock> BLOCK = FurnitureRegistry.register("furniture_workbench", null,
            () -> new FurnitureWorkbenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).noOcclusion()));

    private Workbench() {
    }

    /** Loads this class so the block gets registered. */
    public static void init() {
    }
}
