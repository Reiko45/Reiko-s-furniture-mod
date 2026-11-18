package com.reiko45.furniture.kitchen;

import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Holds items only while someone has the lid open; everything is thrown away when it closes. */
public class TrashCanBlockEntity extends FurnitureStorageBlockEntity {
    public TrashCanBlockEntity(BlockPos pos, BlockState state) {
        super(KitchenFurniture.TRASH_CAN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (openerCount() == 0) {
            return; // nobody is looking (a hopper): throw it away straight away
        }
        super.setItem(slot, stack);
    }

    @Override
    protected void onClosed() {
        clearContent();
        setChanged();
    }
}
