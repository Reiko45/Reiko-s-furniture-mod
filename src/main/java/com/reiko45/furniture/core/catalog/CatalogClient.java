package com.reiko45.furniture.core.catalog;

import net.minecraft.client.Minecraft;

/** Client-only: opens the catalog screen. Kept apart so the server never loads screen classes. */
final class CatalogClient {
    private CatalogClient() {
    }

    static void open() {
        Minecraft.getInstance().setScreen(new CatalogScreen());
    }
}
