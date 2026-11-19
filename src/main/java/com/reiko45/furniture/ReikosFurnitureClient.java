package com.reiko45.furniture;

import com.reiko45.furniture.bathroom.BathroomFurniture;
import com.reiko45.furniture.core.display.DisplayRenderer;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import com.reiko45.furniture.core.registry.ModEntities;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.livingroom.WallClockRenderer;
import com.reiko45.furniture.core.registry.ModMenus;
import com.reiko45.furniture.core.workbench.WorkbenchScreen;
import com.reiko45.furniture.outdoor.GrillRenderer;
import com.reiko45.furniture.outdoor.OutdoorFurniture;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import com.reiko45.furniture.bar.ChalkboardBlock;
import com.reiko45.furniture.nursery.AquariumRenderer;
import com.reiko45.furniture.nursery.BirdCageRenderer;
import com.reiko45.furniture.nursery.NurseryFurniture;
import net.minecraft.client.renderer.Sheets;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.renderer.item.ItemProperties;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.tool.Tools;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import com.reiko45.furniture.core.stain.StainClient;
import com.reiko45.furniture.core.stain.Stains;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only setup. */
@Mod(value = ReikosFurniture.MODID, dist = Dist.CLIENT)
public class ReikosFurnitureClient {
    public ReikosFurnitureClient(IEventBus modBus, ModContainer container) {
        // Settings screen in the Mods list.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(ReikosFurnitureClient::registerRenderers);
        modBus.addListener(ReikosFurnitureClient::clientSetup);
        modBus.addListener(ReikosFurnitureClient::registerScreens);
        modBus.addListener(ReikosFurnitureClient::registerBlockColors);
        // Wood stain: tints on stained pieces and items.
        Stains.clientListener = StainClient::onChanged;
        modBus.addListener(StainClient::registerBlockColors);
        modBus.addListener(StainClient::registerItemColors);
        NeoForge.EVENT_BUS.addListener(StainClient::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(StainClient::onLevelUnload);
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
        event.registerBlockEntityRenderer(NurseryFurniture.AQUARIUM_BLOCK_ENTITY.get(), AquariumRenderer::new);
        event.registerBlockEntityRenderer(NurseryFurniture.BIRD_CAGE_BLOCK_ENTITY.get(), BirdCageRenderer::new);
        // The seat entity is invisible; players just appear to sit on the block.
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
        // Food lying on the grill.
        event.registerBlockEntityRenderer(OutdoorFurniture.GRILL_BLOCK_ENTITY.get(), GrillRenderer::new);
        // Items on shelves, plates and cutting boards, and the wall clock dial.
        event.registerBlockEntityRenderer(ModBlockEntities.DISPLAY.get(), DisplayRenderer::new);
        event.registerBlockEntityRenderer(LivingRoomFurniture.WALL_CLOCK_BLOCK_ENTITY.get(), WallClockRenderer::new);
    }

    /** The moving dolly shows a crate on it while it carries furniture. */
    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Tools.MOVING_DOLLY.get(), ReikosFurniture.id("loaded"),
                    (stack, level, entity, seed) -> stack.has(ModDataComponents.CARRIED_FURNITURE.get()) ? 1.0F : 0.0F);
            // The chalkboard's sign texture.
            Sheets.addWoodType(ChalkboardBlock.TYPE);
        });
    }
}
