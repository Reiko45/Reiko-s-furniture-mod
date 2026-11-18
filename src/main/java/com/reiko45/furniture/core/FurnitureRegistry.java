package com.reiko45.furniture.core;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModBlocks;
import com.reiko45.furniture.core.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

/**
 * Registers furniture blocks together with their items.
 * <p>
 * Room classes call these helpers from their static fields. Registration order is kept, so the
 * creative tab lists pieces room by room in the order they are declared.
 */
public final class FurnitureRegistry {
    private static final Map<DeferredBlock<?>, Room> ROOMS = new LinkedHashMap<>();
    private static final Map<DeferredBlock<?>, int[]> FLAMMABLE = new LinkedHashMap<>();
    private static final List<WoodSet<?>> WOOD_SETS = new ArrayList<>();
    private static final List<ColorSet<?>> COLOR_SETS = new ArrayList<>();
    private static final List<Item> MODDED_ITEMS = new ArrayList<>();

    private FurnitureRegistry() {
    }

    /** Registers {@code <wood>_<piece>} with its item for every vanilla wood (and later, every modded wood). */
    public static <B extends Block> WoodSet<B> woodSet(String piece, Room room, Function<FurnitureWood, B> factory) {
        return woodSet(piece, room, factory, true);
    }

    /** Like {@link #woodSet} but without items: helper blocks that another block places, like the top bunk. */
    public static <B extends Block> WoodSet<B> woodBlocks(String piece, Function<FurnitureWood, B> factory) {
        return woodSet(piece, null, factory, false);
    }

    private static <B extends Block> WoodSet<B> woodSet(String piece, @Nullable Room room, Function<FurnitureWood, B> factory,
                                                        boolean hasItem) {
        WoodSet<B> set = new WoodSet<>(piece, room, factory, hasItem);
        for (FurnitureWood wood : FurnitureWood.values()) {
            String name = WoodSet.name(wood, piece);
            DeferredBlock<B> block = hasItem ? register(name, room, () -> set.create(wood))
                    : ModBlocks.BLOCKS.register(name, () -> set.create(wood));
            set.put(wood, block);
            if (wood.flammable()) {
                FLAMMABLE.put(block, new int[] {5, 20});
            }
        }
        WOOD_SETS.add(set);
        return set;
    }

    /**
     * Registers every wood set's block (and item) for one modded wood. Called while items are being
     * registered, when every mod's blocks exist; the block registry is still open at that point.
     */
    public static <B extends Block> void registerModded(WoodSet<B> set, FurnitureWood wood, BiConsumer<String, Item> items) {
        String name = WoodSet.name(wood, set.piece());
        ResourceLocation id = ReikosFurniture.id(name);
        B block = set.create(wood);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        DeferredBlock<B> holder = DeferredBlock.createBlock(id);
        set.put(wood, holder);
        if (set.hasItem()) {
            Item item = new FurnitureBlockItem(block, new Item.Properties(), set.room());
            items.accept(name, item);
            MODDED_ITEMS.add(item);
        }
        if (wood.flammable()) {
            FLAMMABLE.put(holder, new int[] {5, 20});
        }
    }

    /** Items of modded-wood furniture, in registration order (shown at the end of the creative tab). */
    public static List<Item> moddedItems() {
        return Collections.unmodifiableList(MODDED_ITEMS);
    }

    /** Registers {@code <color>_<piece>} for every dye color. These burn like wool. */
    public static <B extends Block> ColorSet<B> colorSet(String piece, Room room, Function<DyeColor, B> factory) {
        Map<DyeColor, DeferredBlock<B>> blocks = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            DeferredBlock<B> block = register(color.getName() + "_" + piece, room, () -> factory.apply(color));
            blocks.put(color, block);
            FLAMMABLE.put(block, new int[] {30, 60});
        }
        ColorSet<B> set = new ColorSet<>(piece, room, Collections.unmodifiableMap(blocks));
        COLOR_SETS.add(set);
        return set;
    }

    /** Registers a single block (no wood or color variants) and its item. {@code room} is null for tools like the workbench. */
    public static <B extends Block> DeferredBlock<B> register(String name, @Nullable Room room, Supplier<B> factory) {
        DeferredBlock<B> block = ModBlocks.BLOCKS.register(name, factory);
        ModItems.ITEMS.register(name, () -> new FurnitureBlockItem(block.get(), new Item.Properties(), room));
        ROOMS.put(block, room);
        return block;
    }

    /** Every block with the room it belongs to (null for the workbench), in registration order. */
    public static Map<DeferredBlock<?>, Room> rooms() {
        return Collections.unmodifiableMap(ROOMS);
    }

    public static List<WoodSet<?>> woodSets() {
        return Collections.unmodifiableList(WOOD_SETS);
    }

    public static List<ColorSet<?>> colorSets() {
        return Collections.unmodifiableList(COLOR_SETS);
    }

    /** Lets wooden and wool furniture catch fire. Runs during common setup. */
    public static void registerFlammability() {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        FLAMMABLE.forEach((block, values) -> fire.setFlammable(block.get(), values[0], values[1]));
    }

    /** "light_gray" becomes "Light Gray". */
    public static String titleCase(String id) {
        StringBuilder sb = new StringBuilder();
        for (String part : id.split("_")) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }
}
