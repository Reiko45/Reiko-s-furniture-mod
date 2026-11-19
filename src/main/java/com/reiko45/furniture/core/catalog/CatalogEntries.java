package com.reiko45.furniture.core.catalog;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.material.MaterialSet;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModItems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * What the furniture catalog lists: a section per room, per style, and one for everything else
 * (tools, food, the workbench). Each entry is one piece, with all its woods or colors as variants.
 */
public final class CatalogEntries {
    /** One piece; {@code variants} are its woods or colors (just one for single blocks and items). */
    public record Entry(List<ItemStack> variants) {
        public ItemStack first() {
            return variants.get(0);
        }
    }

    public record Section(Component title, List<Entry> entries) {
    }

    private static List<Section> sections;

    private CatalogEntries() {
    }

    /** Built once, after registration (it needs the items to exist). */
    public static List<Section> sections() {
        if (sections == null) {
            sections = build();
        }
        return sections;
    }

    private static List<Section> build() {
        // Which wood or color set each block belongs to, so a set becomes a single entry.
        Map<Block, Object> setOf = new HashMap<>();
        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            set.all().forEach(block -> setOf.put(block.get(), set));
        }
        for (ColorSet<?> set : FurnitureRegistry.colorSets()) {
            set.all().forEach(block -> setOf.put(block.get(), set));
        }
        for (MaterialSet<?, ?> set : MaterialSet.sets()) {
            set.all().forEach(block -> setOf.put(block.get(), set));
        }
        Map<Room, Map<Object, Entry>> rooms = new LinkedHashMap<>();
        Map<Style, Map<Object, Entry>> styles = new LinkedHashMap<>();
        for (Room room : Room.values()) {
            rooms.put(room, new LinkedHashMap<>());
        }
        for (Style style : Style.values()) {
            styles.put(style, new LinkedHashMap<>());
        }
        for (Map.Entry<DeferredBlock<?>, Room> registered : FurnitureRegistry.rooms().entrySet()) {
            Room room = registered.getValue();
            if (room == null) {
                continue; // the workbench goes with the extras
            }
            Block block = registered.getKey().get();
            Object key = setOf.getOrDefault(block, block);
            Style style = key instanceof WoodSet<?> wood ? wood.style() : key instanceof ColorSet<?> color ? color.style() : null;
            Map<Object, Entry> target = style != null ? styles.get(style) : rooms.get(room);
            target.computeIfAbsent(key, k -> new Entry(variants(k)));
        }
        List<Section> out = new ArrayList<>();
        rooms.forEach((room, entries) -> out.add(new Section(Component.translatable(room.translationKey()), List.copyOf(entries.values()))));
        styles.forEach((style, entries) -> out.add(new Section(Component.translatable(style.translationKey()), List.copyOf(entries.values()))));

        List<Entry> extras = new ArrayList<>();
        for (Holder<Item> holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.value();
            if (!(item instanceof FurnitureBlockItem furniture) || furniture.room() == null) {
                extras.add(new Entry(List.of(new ItemStack(item))));
            }
        }
        out.add(new Section(Component.translatable("catalog.reikos_furniture.extras"), List.copyOf(extras)));
        return List.copyOf(out);
    }

    private static List<ItemStack> variants(Object key) {
        Collection<? extends DeferredBlock<?>> blocks = key instanceof WoodSet<?> wood ? wood.all()
                : key instanceof ColorSet<?> color ? color.all() : key instanceof MaterialSet<?, ?> material ? material.all() : null;
        if (blocks == null) {
            return List.of(new ItemStack((Block) key));
        }
        return blocks.stream().map(block -> new ItemStack(block.get())).filter(stack -> !stack.isEmpty()).toList();
    }
}
