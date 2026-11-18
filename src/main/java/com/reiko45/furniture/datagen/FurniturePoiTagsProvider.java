package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.villager.FurnitureMaker;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Lets unemployed villagers take the furniture workbench as their job site. */
public class FurniturePoiTagsProvider extends TagsProvider<PoiType> {
    public FurniturePoiTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, lookup, ReikosFurniture.MODID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(FurnitureMaker.WORKBENCH_POI.getKey());
    }
}
