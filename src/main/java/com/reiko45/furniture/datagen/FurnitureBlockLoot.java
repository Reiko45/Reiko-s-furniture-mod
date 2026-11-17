package com.reiko45.furniture.datagen;

import com.reiko45.furniture.core.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/** Every block drops itself. Two-block-tall pieces drop only from the lower half. */
public class FurnitureBlockLoot extends BlockLootSubProvider {
    public FurnitureBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        for (Block block : getKnownBlocks()) {
            if (block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                add(block, createSinglePropConditionTable(block, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
            } else {
                dropSelf(block);
            }
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value).map(Block.class::cast).toList();
    }
}
