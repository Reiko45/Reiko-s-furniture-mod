package com.reiko45.furniture.core.display;

/**
 * Where an item is drawn on a display block, for a block facing north, in pixels (0-16).
 *
 * @param flat  true if the item lies flat (on a plate), false if it stands up (on a shelf)
 * @param scale size of the drawn item, 1 = a full block
 * @param plant true if a flower or sapling here is drawn as the plant itself, standing in soil, rather than as an item
 */
public record DisplaySlot(float x, float y, float z, boolean flat, float scale, boolean plant) {
    public DisplaySlot(float x, float y, float z, boolean flat, float scale) {
        this(x, y, z, flat, scale, false);
    }

    /** A spot where a plant grows: (x, y, z) is the middle of the soil surface. */
    public static DisplaySlot plant(float x, float y, float z, float scale) {
        return new DisplaySlot(x, y, z, false, scale, true);
    }
}
