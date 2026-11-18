package com.reiko45.furniture.core.modded;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.WoodSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Furniture in other mods' woods. When items are registered, every mod's blocks already exist, so we
 * look for {@code <mod>:<wood>_planks} with a matching log (or stem) and slab, and register every
 * wooden piece in that wood as {@code reikos_furniture:<mod>_<wood>_<piece>}. Their models, loot
 * tables, recipes, tags and names come from a built-in pack ({@link ModdedWoodPack}) made by copying
 * the oak versions.
 */
public final class ModdedWoods {
    private static boolean detected;

    private ModdedWoods() {
    }

    /** Whether detection has run (it happens while items are registered). */
    public static boolean detected() {
        return detected;
    }

    /** Mod bus listener: finds modded woods and registers their furniture while items are registered. */
    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM) || detected) {
            return;
        }
        detected = true;
        detect();
        if (FurnitureWood.modded().isEmpty()) {
            return;
        }
        int count = 0;
        for (FurnitureWood wood : FurnitureWood.modded()) {
            for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
                FurnitureRegistry.registerModded(set, wood, (name, item) -> event.register(Registries.ITEM, ReikosFurniture.id(name), () -> item));
                count++;
            }
        }
        ReikosFurniture.LOGGER.info("Added {} furniture blocks for {} woods from other mods: {}", count, FurnitureWood.modded().size(),
                FurnitureWood.modded());
    }

    /** Looks through every registered block for other mods' woods. */
    static void detect() {
        List<ResourceLocation> ids = new ArrayList<>(BuiltInRegistries.BLOCK.keySet());
        ids.sort(Comparator.comparing(ResourceLocation::toString));
        for (ResourceLocation id : ids) {
            String namespace = id.getNamespace();
            String path = id.getPath();
            if (namespace.equals("minecraft") || namespace.equals(ReikosFurniture.MODID) || !path.endsWith("_planks")) {
                continue;
            }
            String name = path.substring(0, path.length() - "_planks".length());
            if (name.isEmpty()) {
                continue;
            }
            Block planks = BuiltInRegistries.BLOCK.get(id);
            Block log = find(namespace, name + "_log", name + "_stem", name + "_block");
            Block slab = find(namespace, name + "_slab");
            if (log == null || slab == null) {
                continue;
            }
            Block stripped = find(namespace, "stripped_" + name + "_log", "stripped_" + name + "_stem", "stripped_" + name + "_block");
            boolean flammable = planks.defaultBlockState().ignitedByLava();
            FurnitureWood.modded(namespace + "_" + name, FurnitureRegistry.titleCase(name), planks, log,
                    stripped != null ? stripped : log, slab, flammable);
        }
    }

    @Nullable
    private static Block find(String namespace, String... paths) {
        for (String path : paths) {
            Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(namespace, path));
            if (block.isPresent()) {
                return block.get();
            }
        }
        return null;
    }

    /**
     * Mod bus listener: modded pieces use the same block entities as their oak versions (storage,
     * shelves, wall clocks, record players, ...).
     */
    public static void onAddBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
        if (FurnitureWood.modded().isEmpty()) {
            return;
        }
        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            BlockState oak = set.get(FurnitureWood.OAK).get().defaultBlockState();
            Block[] modded = FurnitureWood.modded().stream().map(wood -> (Block) set.get(wood).get()).toArray(Block[]::new);
            event.modify((key, type) -> type.isValid(oak), modded);
        }
    }
}
