package com.reiko45.furniture.garage;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A workshop bench with a vise. Right-click to craft on it, like a crafting table. */
public class WorkshopBenchBlock extends FurnitureBlock {
    private static final Component TITLE = Component.translatable("container.crafting");

    public WorkshopBenchBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new BenchMenu(id, inventory,
                    ContainerLevelAccess.create(level, pos), this), TITLE));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** The vanilla crafting menu, but it stays open for this block instead of a crafting table. */
    public static class BenchMenu extends CraftingMenu {
        private final ContainerLevelAccess access;
        private final Block bench;

        public BenchMenu(int id, Inventory inventory, ContainerLevelAccess access, Block bench) {
            super(id, inventory, access);
            this.access = access;
            this.bench = bench;
        }

        @Override
        public boolean stillValid(Player player) {
            return stillValid(access, player, bench);
        }
    }
}
