package com.reiko45.furniture.core;

import java.util.Collection;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * One furniture piece made in every {@link FurnitureWood}, e.g. all eleven nightstands.
 *
 * @param piece  the piece name, e.g. {@code nightstand}; block ids are {@code <wood>_<piece>}
 * @param room   the room section the piece belongs to
 * @param blocks the registered block for each wood type
 */
public record WoodSet<B extends Block>(String piece, Room room, Map<FurnitureWood, DeferredBlock<B>> blocks) {
    public DeferredBlock<B> get(FurnitureWood wood) {
        return blocks.get(wood);
    }

    public Collection<DeferredBlock<B>> all() {
        return blocks.values();
    }
}
