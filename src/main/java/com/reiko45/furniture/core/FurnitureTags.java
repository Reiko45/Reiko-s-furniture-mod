package com.reiko45.furniture.core;

import com.reiko45.furniture.ReikosFurniture;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** The mod's tags: item tags used by the advancements, and the lamps block tag. Written by data generation. */
public final class FurnitureTags {
    /** Every furniture item that belongs to a room, classic and styled. */
    public static final TagKey<Item> FURNITURE = item("furniture");
    /** Every style piece. */
    public static final TagKey<Item> STYLES = item("styles");
    /** Drinks made with the kitchen and bar appliances. */
    public static final TagKey<Item> DRINKS = item("drinks");

    /** Lamps a light switch can control. */
    public static final TagKey<Block> LAMPS = TagKey.create(Registries.BLOCK, ReikosFurniture.id("lamps"));

    private FurnitureTags() {
    }

    /** The furniture of one room, e.g. {@code reikos_furniture:rooms/bedroom}. */
    public static TagKey<Item> room(Room room) {
        return item("rooms/" + room.id());
    }

    /** The pieces of one style, e.g. {@code reikos_furniture:styles/rustic}. */
    public static TagKey<Item> style(Style style) {
        return item("styles/" + style.id());
    }

    private static TagKey<Item> item(String path) {
        return TagKey.create(Registries.ITEM, ReikosFurniture.id(path));
    }
}
