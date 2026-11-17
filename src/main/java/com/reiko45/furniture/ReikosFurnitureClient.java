package com.reiko45.furniture;

import com.reiko45.furniture.core.registry.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only setup. */
@Mod(value = ReikosFurniture.MODID, dist = Dist.CLIENT)
public class ReikosFurnitureClient {
    public ReikosFurnitureClient(IEventBus modBus) {
        modBus.addListener(ReikosFurnitureClient::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // The seat entity is invisible; players just appear to sit on the block.
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
    }
}
