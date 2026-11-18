package com.reiko45.furniture;

import com.reiko45.furniture.bathroom.BathroomFurniture;
import com.reiko45.furniture.core.registry.ModEntities;
import com.reiko45.furniture.core.registry.ModMenus;
import com.reiko45.furniture.core.workbench.WorkbenchScreen;
import com.reiko45.furniture.outdoor.GrillRenderer;
import com.reiko45.furniture.outdoor.OutdoorFurniture;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only setup. */
@Mod(value = ReikosFurniture.MODID, dist = Dist.CLIENT)
public class ReikosFurnitureClient {
    public ReikosFurnitureClient(IEventBus modBus) {
        modBus.addListener(ReikosFurnitureClient::registerRenderers);
        modBus.addListener(ReikosFurnitureClient::registerScreens);
        modBus.addListener(ReikosFurnitureClient::registerBlockColors);
    }

    private static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Bathtub water takes the biome's water color, like a cauldron.
        event.register((state, level, pos, tintIndex) -> level != null && pos != null
                ? BiomeColors.getAverageWaterColor(level, pos) : 0x3F76E4, BathroomFurniture.BATHTUB.get());
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WORKBENCH.get(), WorkbenchScreen::new);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // The seat entity is invisible; players just appear to sit on the block.
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
        // Food lying on the grill.
        event.registerBlockEntityRenderer(OutdoorFurniture.GRILL_BLOCK_ENTITY.get(), GrillRenderer::new);
    }
}
