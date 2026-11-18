package com.reiko45.furniture.core.display;

/**
 * Where an item is drawn on a display block, for a block facing north, in pixels (0-16).
 *
 * @param flat  true if the item lies flat (on a plate), false if it stands up (on a shelf)
 * @param scale size of the drawn item, 1 = a full block
 */
public record DisplaySlot(float x, float y, float z, boolean flat, float scale) {
}
