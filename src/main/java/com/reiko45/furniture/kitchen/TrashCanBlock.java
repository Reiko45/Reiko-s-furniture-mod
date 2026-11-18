package com.reiko45.furniture.kitchen;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Trash can: whatever is inside is destroyed when the lid closes. Hoppers feeding it destroy items at once. */
public class TrashCanBlock extends FurnitureStorageBlock {
    public TrashCanBlock(Properties properties, VoxelShape northShape) {
        super(properties, 1, northShape);
    }

    @Override
    public boolean usesSharedStorage() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.IRON_TRAPDOOR_OPEN;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.IRON_TRAPDOOR_CLOSE;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrashCanBlockEntity(pos, state);
    }
}
