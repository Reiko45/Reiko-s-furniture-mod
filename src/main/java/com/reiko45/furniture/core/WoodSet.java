package com.reiko45.furniture.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

/**
 * One furniture piece made in every {@link FurnitureWood}, e.g. all the nightstands. The vanilla
 * woods are registered when the room class loads; woods from other mods are added at startup.
 */
public final class WoodSet<B extends Block> {
    private final String piece;
    @Nullable
    private final Room room;
    private final Function<FurnitureWood, B> factory;
    private final boolean hasItem;
    private final Map<FurnitureWood, DeferredBlock<B>> blocks = new LinkedHashMap<>();

    WoodSet(String piece, @Nullable Room room, Function<FurnitureWood, B> factory, boolean hasItem) {
        this.piece = piece;
        this.room = room;
        this.factory = factory;
        this.hasItem = hasItem;
    }

    /** The piece name, e.g. {@code nightstand}; block ids are {@code <wood>_<piece>}. */
    public String piece() {
        return piece;
    }

    /** The room section the piece belongs to (null for hidden helper blocks like the top bunk). */
    @Nullable
    public Room room() {
        return room;
    }

    /** The furniture style, from the piece name ({@code rustic_bed}), or null for classic pieces. */
    @Nullable
    public Style style() {
        return Style.of(piece);
    }

    /** Whether the blocks have items (helper blocks placed by another block don't). */
    public boolean hasItem() {
        return hasItem;
    }

    /** Makes the block for one wood. */
    public B create(FurnitureWood wood) {
        return factory.apply(wood);
    }

    void put(FurnitureWood wood, DeferredBlock<B> block) {
        blocks.put(wood, block);
    }

    public DeferredBlock<B> get(FurnitureWood wood) {
        return blocks.get(wood);
    }

    /** Every block in the set, vanilla woods first, then modded woods. */
    public Collection<DeferredBlock<B>> all() {
        return Collections.unmodifiableCollection(blocks.values());
    }

    public static String name(FurnitureWood wood, String piece) {
        return wood.id() + "_" + piece;
    }
}
