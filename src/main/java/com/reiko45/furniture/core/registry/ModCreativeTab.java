package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.styles.StyleFurniture;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

/**
 * Two creative tabs. The first lists the classic furniture room by room, in registration order; the
 * second lists the style pieces, style by style. Furniture in other mods' woods goes at the end.
 */
public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReikosFurniture.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FURNITURE = TABS.register("furniture",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.reikos_furniture"))
                    .icon(ModCreativeTab::icon)
                    .displayItems((params, output) -> allItems().filter(item -> styleOf(item) == null).forEach(output::accept))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STYLES = TABS.register("styles",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.reikos_furniture.styles"))
                    .withTabsBefore(FURNITURE.getKey())
                    .icon(() -> new ItemStack(StyleFurniture.get(Style.ROYAL).bed().get(FurnitureWood.DARK_OAK).get()))
                    .displayItems((params, output) -> {
                        for (Style style : Style.values()) {
                            allItems().filter(item -> styleOf(item) == style).forEach(output::accept);
                        }
                    })
                    .build());

    private ModCreativeTab() {
    }

    /** Every furniture item: the mod's own registrations, then the pieces in other mods' woods. */
    private static Stream<Item> allItems() {
        return Stream.concat(ModItems.ITEMS.getEntries().stream().map(Holder::value), FurnitureRegistry.moddedItems().stream());
    }

    @Nullable
    private static Style styleOf(Item item) {
        return item instanceof FurnitureBlockItem furniture ? furniture.style() : null;
    }

    /** The tab icon is the first furniture item that was registered. */
    private static ItemStack icon() {
        return ModItems.ITEMS.getEntries().stream().findFirst()
                .map(item -> new ItemStack(item.value()))
                .orElseGet(() -> new ItemStack(Items.OAK_PLANKS));
    }
}
