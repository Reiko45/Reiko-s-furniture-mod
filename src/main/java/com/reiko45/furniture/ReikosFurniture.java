package com.reiko45.furniture;

import com.mojang.logging.LogUtils;
import com.reiko45.furniture.bar.BarFurniture;
import com.reiko45.furniture.bathroom.BathroomFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.config.FurnitureConfig;
import com.reiko45.furniture.core.modded.ModdedWoodPack;
import com.reiko45.furniture.core.modded.ModdedWoods;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import com.reiko45.furniture.core.registry.ModBlocks;
import com.reiko45.furniture.core.registry.ModCreativeTab;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.registry.ModEntities;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.core.registry.ModMenus;
import com.reiko45.furniture.core.registry.ModRecipes;
import com.reiko45.furniture.core.registry.ModSounds;
import com.reiko45.furniture.core.tool.Tools;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.datagen.DataGenerators;
import com.reiko45.furniture.decor.DecorFurniture;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.gameroom.GameRoomFurniture;
import com.reiko45.furniture.garage.GarageFurniture;
import com.reiko45.furniture.library.LibraryFurniture;
import com.reiko45.furniture.kids.KidsFurniture;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.laundry.LaundryFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.music.MusicFurniture;
import com.reiko45.furniture.nursery.NurseryFurniture;
import com.reiko45.furniture.office.OfficeFurniture;
import com.reiko45.furniture.outdoor.OutdoorFurniture;
import com.reiko45.furniture.pets.PetFurniture;
import com.reiko45.furniture.villager.FurnitureMaker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
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
        // Settings first: the startup file decides whether modded woods are registered.
        FurnitureConfig.register(container);

        // Room sections, in the order they appear in the creative tab.
        BedroomFurniture.init();
        KitchenFurniture.init();
        DiningFurniture.init();
        LivingRoomFurniture.init();
        BathroomFurniture.init();
        OfficeFurniture.init();
        OutdoorFurniture.init();
        KidsFurniture.init();
        LaundryFurniture.init();
        MusicFurniture.init();
        GameRoomFurniture.init();
        PetFurniture.init();
        DecorFurniture.init();
        GarageFurniture.init();
        LibraryFurniture.init();
        BarFurniture.init();
        NurseryFurniture.init();
        Workbench.init();
        Tools.init();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModCreativeTab.TABS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModRecipes.TYPES.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModDataComponents.COMPONENTS.register(modBus);
        FurnitureMaker.POI_TYPES.register(modBus);
        FurnitureMaker.PROFESSIONS.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(ReikosFurniture::registerCapabilities);
        modBus.addListener(DataGenerators::gatherData);
        modBus.addListener(ReikosFurniture::addBlockEntityBlocks);
        // Furniture in other mods' woods.
        modBus.addListener(ModdedWoods::onRegister);
        modBus.addListener(ModdedWoods::onAddBlockEntityBlocks);
        modBus.addListener(ModdedWoodPack::onAddPackFinders);
        NeoForge.EVENT_BUS.addListener(PetFurniture::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(FurnitureMaker::addTrades);
    }

    /** Record players use the vanilla jukebox block entity, so music discs and comparators work as usual. */
    private static void addBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.JUKEBOX, MusicFurniture.RECORD_PLAYER.all().stream().map(block -> (Block) block.get()).toArray(Block[]::new));
        // The chalkboard is a sign with its own look.
        event.modify(BlockEntityType.SIGN, BarFurniture.CHALKBOARD.get());
    }

    /** Lets hoppers and pipes from other mods move items in and out of storage furniture (not the safe). */
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.STORAGE.get(), (storage, side) -> new InvWrapper(storage));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KitchenFurniture.TRASH_CAN_BLOCK_ENTITY.get(), (trash, side) -> new InvWrapper(trash));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LaundryFurniture.LAUNDRY_MACHINE_BLOCK_ENTITY.get(), (machine, side) -> new InvWrapper(machine));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(FurnitureRegistry::registerFlammability);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
