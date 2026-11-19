package com.reiko45.furniture.material;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * One piece of stone or metal furniture in each of its materials (stones, or ages of copper), like a
 * {@link com.reiko45.furniture.core.WoodSet} for woods. The catalog shows them as one entry.
 */
public final class MaterialSet<K extends Enum<K>, B extends Block> {
    private static final List<MaterialSet<?, ?>> SETS = new ArrayList<>();

    private final String piece;
    private final Map<K, DeferredBlock<B>> blocks;

    private MaterialSet(String piece, Map<K, DeferredBlock<B>> blocks) {
        this.piece = piece;
        this.blocks = blocks;
    }

    /** Registers {@code name(key)} for every key, each made by the factory. */
    public static <K extends Enum<K>, B extends Block> MaterialSet<K, B> register(String piece, Room room, Class<K> keys,
                                                                               Function<K, String> name, Function<K, B> factory) {
        Map<K, DeferredBlock<B>> blocks = new EnumMap<>(keys);
        for (K key : keys.getEnumConstants()) {
            blocks.put(key, FurnitureRegistry.register(name.apply(key), room, () -> factory.apply(key)));
        }
        MaterialSet<K, B> set = new MaterialSet<>(piece, Collections.unmodifiableMap(blocks));
        SETS.add(set);
        return set;
    }

    public String piece() {
        return piece;
    }

    public DeferredBlock<B> get(K key) {
        return blocks.get(key);
    }

    public Collection<DeferredBlock<B>> all() {
        return blocks.values();
    }

    public Map<K, DeferredBlock<B>> byKey() {
        return blocks;
    }

    /** Every material set, in registration order. */
    public static List<MaterialSet<?, ?>> sets() {
        return Collections.unmodifiableList(SETS);
    }
}
