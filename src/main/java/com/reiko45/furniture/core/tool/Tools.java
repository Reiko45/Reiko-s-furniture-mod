package com.reiko45.furniture.core.tool;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/** The furniture tools: hammer and moving dolly. */
public final class Tools {
    public static final DeferredItem<HammerItem> HAMMER = ModItems.ITEMS.register("hammer",
            () -> new HammerItem(new Item.Properties().durability(250)));
    public static final DeferredItem<DollyItem> MOVING_DOLLY = ModItems.ITEMS.register("moving_dolly",
            () -> new DollyItem(new Item.Properties()));

    private Tools() {
    }

    public static void init() {
    }

    /** Fires the furniture advancement trigger for a tool action ({@code hammer} or {@code dolly}). */
    static void trigger(Player player, String action) {
        FurnitureActionTrigger.award(player, action);
    }
}
