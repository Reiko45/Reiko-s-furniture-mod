package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One shared creative tab. Items appear room by room, in registration order. */
public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReikosFurniture.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FURNITURE = TABS.register("furniture",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.reikos_furniture"))
                    .icon(ModCreativeTab::icon)
                    .displayItems((params, output) -> ModItems.ITEMS.getEntries().stream()
                            .map(Holder::value)
                            .forEach(output::accept))
                    .build());

    private ModCreativeTab() {
    }

    /** The tab icon is the first furniture item that was registered. */
    private static ItemStack icon() {
        return ModItems.ITEMS.getEntries().stream().findFirst()
                .map(item -> new ItemStack(item.value()))
                .orElseGet(() -> new ItemStack(Items.OAK_PLANKS));
    }
}
