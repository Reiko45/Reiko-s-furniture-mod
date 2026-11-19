package com.reiko45.furniture.datagen;

import com.reiko45.furniture.material.MaterialData;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

/**
 * NeoForge data maps: how copper furniture ages and which block waxing turns it into. Vanilla copper
 * uses the same maps, so honeycomb, axes and random ageing work on it like on copper blocks.
 */
public class FurnitureDataMapProvider extends DataMapProvider {
    public FurnitureDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        var oxidizables = builder(NeoForgeDataMaps.OXIDIZABLES);
        MaterialData.oxidizing().forEach((block, next) -> oxidizables.add(holder(block), new Oxidizable(next), false));
        var waxables = builder(NeoForgeDataMaps.WAXABLES);
        MaterialData.waxing().forEach((block, waxed) -> waxables.add(holder(block), new Waxable(waxed), false));
    }

    @SuppressWarnings("deprecation")
    private static net.minecraft.core.Holder<Block> holder(Block block) {
        return block.builtInRegistryHolder();
    }
}
