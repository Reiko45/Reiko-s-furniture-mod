package com.reiko45.furniture.villager;

import com.mojang.datafixers.util.Pair;
import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.config.FurnitureConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/**
 * Adds a furniture maker's workshop to the house pools of the five vanilla village types, so new
 * villages can have one (with a furniture workbench, so a villager there becomes a furniture maker).
 * How often is set by {@code village_workshop_weight} in the common config; 0 turns it off.
 */
public final class VillageWorkshops {
    /** The village types; each has {@code minecraft:village/<type>/houses} and our {@code village/<type>/furniture_workshop}. */
    public static final List<String> VILLAGES = List.of("plains", "desert", "savanna", "snowy", "taiga");

    private VillageWorkshops() {
    }

    public static ResourceLocation template(String village) {
        return ReikosFurniture.id("village/" + village + "/furniture_workshop");
    }

    public static ResourceLocation pool(String village) {
        return ResourceLocation.withDefaultNamespace("village/" + village + "/houses");
    }

    /** Game bus listener. The pools are data, loaded fresh for every world, so this runs at each server start. */
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        int weight = FurnitureConfig.villageWorkshopWeight();
        if (weight > 0) {
            addToPools(event.getServer().registryAccess(), weight);
        }
    }

    static void addToPools(RegistryAccess registries, int weight) {
        Registry<StructureTemplatePool> pools = registries.registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureProcessorList> noProcessors = registries.registryOrThrow(Registries.PROCESSOR_LIST)
                .getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.withDefaultNamespace("empty")));
        for (String village : VILLAGES) {
            StructureTemplatePool pool = pools.get(pool(village));
            if (pool == null) {
                ReikosFurniture.LOGGER.warn("No village house pool {}, no furniture workshop there", pool(village));
                continue;
            }
            StructurePoolElement workshop = StructurePoolElement.legacy(template(village).toString(), noProcessors)
                    .apply(StructureTemplatePool.Projection.RIGID);
            // The pool picks from `templates`, which has each piece once per point of weight.
            for (int i = 0; i < weight; i++) {
                pool.templates.add(workshop);
            }
            List<Pair<StructurePoolElement, Integer>> raw = new ArrayList<>(pool.rawTemplates);
            raw.add(Pair.of(workshop, weight));
            pool.rawTemplates = raw;
        }
    }
}
