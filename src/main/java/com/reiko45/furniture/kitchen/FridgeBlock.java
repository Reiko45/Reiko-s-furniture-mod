package com.reiko45.furniture.kitchen;

import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Two-block-tall fridge. Same as other tall storage, but with metal door sounds. */
public class FridgeBlock extends TallStorageFurnitureBlock {
    public FridgeBlock(Properties properties, int rows, VoxelShape lowerShape, VoxelShape upperShape) {
        super(properties, rows, lowerShape, upperShape);
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.IRON_TRAPDOOR_OPEN;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.IRON_TRAPDOOR_CLOSE;
    }
}
