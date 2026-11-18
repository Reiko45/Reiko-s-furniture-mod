package com.reiko45.furniture.core;

import java.util.Collection;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * One furniture piece made in every dye color, e.g. all sixteen sofas.
 *
 * @param piece  the piece name, e.g. {@code sofa}; block ids are {@code <color>_<piece>}
 * @param room   the room section the piece belongs to
 * @param blocks the registered block for each color
 */
public record ColorSet<B extends Block>(String piece, Room room, Map<DyeColor, DeferredBlock<B>> blocks) {
    public DeferredBlock<B> get(DyeColor color) {
        return blocks.get(color);
    }

    /** The furniture style, from the piece name ({@code modern_sofa}), or null for classic pieces. */
    @org.jetbrains.annotations.Nullable
    public Style style() {
        return Style.of(piece);
    }

    public Collection<DeferredBlock<B>> all() {
        return blocks.values();
    }
}
