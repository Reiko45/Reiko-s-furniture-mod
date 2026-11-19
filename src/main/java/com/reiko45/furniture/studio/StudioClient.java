package com.reiko45.furniture.studio;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/** Client only: opens the paint screen. Kept apart so the server never loads screen classes. */
final class StudioClient {
    private StudioClient() {
    }

    static void openPaintScreen(BlockPos easel, Painting painting) {
        Minecraft.getInstance().setScreen(new PaintScreen(easel, painting));
    }
}
