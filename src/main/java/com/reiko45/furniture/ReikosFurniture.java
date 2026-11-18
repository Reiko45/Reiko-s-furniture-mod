package com.reiko45.furniture;

import com.mojang.logging.LogUtils;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import com.reiko45.furniture.core.registry.ModBlocks;
import com.reiko45.furniture.core.registry.ModCreativeTab;
import com.reiko45.furniture.core.registry.ModEntities;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.datagen.DataGenerators;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * Entry point of Reiko's Furniture.
 * <p>
 * The furniture is split into room sections. Each room lives in its own package
 * ({@code bedroom}, {@code kitchen}, {@code dining}, {@code livingroom}) and registers
 * its pieces through {@link FurnitureRegistry}. Shared building blocks (seats, storage,
 * lamps, tables, ...) live in {@code core}.
 */
@Mod(ReikosFurniture.MODID)
public class ReikosFurniture {
    public static final String MODID = "reikos_furniture";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReikosFurniture(IEventBus modBus, ModContainer container) {
        // Room sections, in the order they appear in the creative tab.
        BedroomFurniture.init();
        KitchenFurniture.init();
        DiningFurniture.init();
        LivingRoomFurniture.init();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModCreativeTab.TABS.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(DataGenerators::gatherData);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(FurnitureRegistry::registerFlammability);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
