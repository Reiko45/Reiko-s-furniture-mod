package com.reiko45.furniture.core.stain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

/**
 * The wood stains in one chunk: block position (as a long) to dye color. Immutable; changes make a new
 * map, which the chunk attachment then saves and sends to players.
 */
public record StainMap(Map<Long, DyeColor> stains) {
    public static final StainMap EMPTY = new StainMap(Collections.unmodifiableMap(new TreeMap<>()));

    private record Entry(long pos, DyeColor color) {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("pos").forGetter(Entry::pos),
                DyeColor.CODEC.fieldOf("color").forGetter(Entry::color)).apply(instance, Entry::new));
    }

    public static final Codec<StainMap> CODEC = Entry.CODEC.listOf().xmap(StainMap::fromEntries, StainMap::entries);

    public static final StreamCodec<ByteBuf, StainMap> STREAM_CODEC = ByteBufCodecs
            .<ByteBuf, Long, DyeColor, Map<Long, DyeColor>>map(size -> new TreeMap<>(), ByteBufCodecs.VAR_LONG, DyeColor.STREAM_CODEC)
            .map(StainMap::of, StainMap::stains);

    private static StainMap of(Map<Long, DyeColor> stains) {
        return stains.isEmpty() ? EMPTY : new StainMap(Collections.unmodifiableMap(new TreeMap<>(stains)));
    }

    private static StainMap fromEntries(List<Entry> entries) {
        TreeMap<Long, DyeColor> stains = new TreeMap<>();
        entries.forEach(entry -> stains.put(entry.pos(), entry.color()));
        return of(stains);
    }

    /** In position order, so saved chunks don't change from save to save. */
    private List<Entry> entries() {
        return new TreeMap<>(stains).entrySet().stream().map(entry -> new Entry(entry.getKey(), entry.getValue())).toList();
    }

    @Nullable
    public DyeColor get(BlockPos pos) {
        return stains.get(pos.asLong());
    }

    /** This map with {@code pos} stained {@code color} (or unstained if null). */
    public StainMap with(BlockPos pos, @Nullable DyeColor color) {
        TreeMap<Long, DyeColor> copy = new TreeMap<>(stains);
        if (color == null) {
            copy.remove(pos.asLong());
        } else {
            copy.put(pos.asLong(), color);
        }
        return of(copy);
    }

    public boolean isEmpty() {
        return stains.isEmpty();
    }
}
