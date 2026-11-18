package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.workbench.WorkbenchMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Screens added by the mod. Storage furniture uses the vanilla chest screen instead. */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ReikosFurniture.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<WorkbenchMenu>> WORKBENCH = MENUS.register(
            "furniture_workbench", () -> new MenuType<>(WorkbenchMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {
    }
}
