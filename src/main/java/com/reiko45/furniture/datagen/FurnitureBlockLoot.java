package com.reiko45.furniture.datagen;

import com.reiko45.furniture.core.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import com.reiko45.furniture.core.lighting.LightSwitchBlock;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.holiday.PresentBlock;
import com.reiko45.furniture.studio.CanvasBlock;
import net.minecraft.core.component.DataComponents;
import com.reiko45.furniture.theater.ProjectorScreenBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/** Every block drops itself. Two-block-tall pieces drop only from the lower half, beds from the head. */
public class FurnitureBlockLoot extends BlockLootSubProvider {
    public FurnitureBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        for (Block block : getKnownBlocks()) {
            if (block.asItem() == Items.AIR) {
                add(block, noDrop()); // parts without an item, like the top of a bunk bed
            } else if (block instanceof LightSwitchBlock) {
                // The switch keeps the lamps it controls.
                add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(
                                CopyComponentsFunction.Source.BLOCK_ENTITY).include(ModDataComponents.LINKED_LAMPS.get()))))));
            } else if (block instanceof PresentBlock) {
                // Presents keep what is inside, like shulker boxes.
                add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(
                                CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.CONTAINER)
                                .include(DataComponents.CUSTOM_NAME))))));
            } else if (block instanceof CanvasBlock) {
                // The canvas keeps its painting.
                add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(
                                CopyComponentsFunction.Source.BLOCK_ENTITY).include(ModDataComponents.PAINTING.get()))))));
            } else if (block instanceof ProjectorScreenBlock) {
                // Six blocks, one item: only the bottom middle drops it.
                add(block, createSinglePropConditionTable(block, ProjectorScreenBlock.PART, ProjectorScreenBlock.Part.BOTTOM_MIDDLE));
            } else if (block instanceof SlabBlock) {
                add(block, createSlabItemTable(block)); // a double slab drops two
            } else if (block.defaultBlockState().hasProperty(BlockStateProperties.BED_PART)) {
                add(block, createSinglePropConditionTable(block, BlockStateProperties.BED_PART, BedPart.HEAD));
            } else if (block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
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
