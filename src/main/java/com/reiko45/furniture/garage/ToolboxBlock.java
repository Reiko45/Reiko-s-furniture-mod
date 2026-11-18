package com.reiko45.furniture.garage;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Metal storage (toolbox, oil drum) that opens with a latch click. */
public class ToolboxBlock extends FurnitureStorageBlock {
    public ToolboxBlock(Properties properties, int rows, VoxelShape northShape) {
        super(properties, rows, northShape);
    }

    @Override
    public SoundEvent openSound() {
        return ModSounds.TOOLBOX_LATCH.get();
    }

    @Override
    public SoundEvent closeSound() {
        return ModSounds.TOOLBOX_LATCH.get();
    }
}
