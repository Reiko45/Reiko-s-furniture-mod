package com.reiko45.furniture.kitchen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Microwave inventory. Uses smoker recipes and the vanilla smoker screen; the block ticks it twice as often. */
public class MicrowaveBlockEntity extends AbstractFurnaceBlockEntity {
    public MicrowaveBlockEntity(BlockPos pos, BlockState state) {
        super(KitchenFurniture.MICROWAVE_BLOCK_ENTITY.get(), pos, state, RecipeType.SMOKING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.reikos_furniture.microwave");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SmokerMenu(containerId, inventory, this, this.dataAccess);
    }
}
