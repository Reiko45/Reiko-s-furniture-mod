package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Wooden and fabric pieces are mined with an axe, metal and ceramic pieces with a pickaxe. */
public class FurnitureBlockTagsProvider extends BlockTagsProvider {
    public FurnitureBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                      ExistingFileHelper files) {
        super(output, registries, ReikosFurniture.MODID, files);
    }

    /** Sounds of blocks made of wood or fabric; everything else (metal, stone, ceramic) needs a pickaxe. */
    private static final Set<SoundType> AXE_SOUNDS = Set.of(SoundType.WOOD, SoundType.CHERRY_WOOD,
            SoundType.BAMBOO_WOOD, SoundType.NETHER_WOOD, SoundType.WOOL);

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            Block block = holder.get();
            boolean axe = AXE_SOUNDS.contains(block.defaultBlockState().getSoundType());
            tag(axe ? BlockTags.MINEABLE_WITH_AXE : BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        }
        DataGenerators.ROOMS.forEach(room -> room.tags(this));
    }

    public IntrinsicTagAppender<Block> tagFor(TagKey<Block> tag) {
        return tag(tag);
    }
}
