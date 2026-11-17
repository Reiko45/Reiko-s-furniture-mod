package com.reiko45.furniture.core.blockentity;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Inventory for every storage piece. The number of rows comes from the {@link FurnitureStorageBlock},
 * and the vanilla chest screen is used, so no custom screen is needed.
 */
public class FurnitureStorageBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items;

    public FurnitureStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STORAGE.get(), pos, state);
        this.items = NonNullList.withSize(rowsFor(state) * 9, ItemStack.EMPTY);
    }

    private static int rowsFor(BlockState state) {
        return state.getBlock() instanceof FurnitureStorageBlock storage ? storage.rows() : 3;
    }

    public int rows() {
        return items.size() / 9;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ChestMenu(menuType(rows()), containerId, inventory, this, rows());
    }

    private static MenuType<ChestMenu> menuType(int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            default -> MenuType.GENERIC_9x6;
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items, registries);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator() && getBlockState().getBlock() instanceof FurnitureStorageBlock storage) {
            playSound(storage.openSound());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator() && getBlockState().getBlock() instanceof FurnitureStorageBlock storage) {
            playSound(storage.closeSound());
        }
    }

    private void playSound(SoundEvent sound) {
        if (level != null && !isRemoved()) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }
}
