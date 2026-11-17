package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.WoodSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Wooden and fabric pieces are mined with an axe, metal appliances with a pickaxe. */
public class FurnitureBlockTagsProvider extends BlockTagsProvider {
    public FurnitureBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                      ExistingFileHelper files) {
        super(output, registries, ReikosFurniture.MODID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        Set<Block> axe = new HashSet<>();
        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            set.all().forEach(block -> axe.add(block.get()));
        }
        for (ColorSet<?> set : FurnitureRegistry.colorSets()) {
            set.all().forEach(block -> axe.add(block.get()));
        }
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            Block block = holder.get();
            tag(axe.contains(block) ? BlockTags.MINEABLE_WITH_AXE : BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        }
        DataGenerators.ROOMS.forEach(room -> room.tags(this));
    }

    public IntrinsicTagAppender<Block> tagFor(TagKey<Block> tag) {
        return tag(tag);
    }
}
