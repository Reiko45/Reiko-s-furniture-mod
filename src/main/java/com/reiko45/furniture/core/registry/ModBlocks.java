package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Block register. Room classes add their blocks through {@link com.reiko45.furniture.core.FurnitureRegistry}. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ReikosFurniture.MODID);

    private ModBlocks() {
    }
}
