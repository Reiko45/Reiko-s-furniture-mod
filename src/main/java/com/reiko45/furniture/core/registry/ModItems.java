package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Item register. Every furniture block gets its item through {@link com.reiko45.furniture.core.FurnitureRegistry}. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ReikosFurniture.MODID);

    private ModItems() {
    }
}
