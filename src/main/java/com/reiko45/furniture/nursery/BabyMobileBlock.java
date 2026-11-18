package com.reiko45.furniture.nursery;

import com.reiko45.furniture.library.GlobeBlock;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A baby mobile hanging from the ceiling. Right-click to set it spinning (with a little chime). */
public class BabyMobileBlock extends GlobeBlock {
    public BabyMobileBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    protected SoundEvent spinSound() {
        return SoundEvents.NOTE_BLOCK_CHIME.value();
    }
}
