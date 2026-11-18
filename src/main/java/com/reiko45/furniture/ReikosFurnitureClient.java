package com.reiko45.furniture;

import com.reiko45.furniture.core.registry.ModEntities;
import com.reiko45.furniture.core.registry.ModMenus;
import com.reiko45.furniture.core.workbench.WorkbenchScreen;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only setup. */
@Mod(value = ReikosFurniture.MODID, dist = Dist.CLIENT)
public class ReikosFurnitureClient {
    public ReikosFurnitureClient(IEventBus modBus) {
        modBus.addListener(ReikosFurnitureClient::registerRenderers);
        modBus.addListener(ReikosFurnitureClient::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WORKBENCH.get(), WorkbenchScreen::new);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // The seat entity is invisible; players just appear to sit on the block.
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
    }
}
