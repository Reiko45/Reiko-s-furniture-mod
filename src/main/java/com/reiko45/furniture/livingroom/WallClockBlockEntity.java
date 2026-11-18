package com.reiko45.furniture.livingroom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Holds no data; it only exists so the clock dial can be drawn by {@link WallClockRenderer}. */
public class WallClockBlockEntity extends BlockEntity {
    public WallClockBlockEntity(BlockPos pos, BlockState state) {
        super(LivingRoomFurniture.WALL_CLOCK_BLOCK_ENTITY.get(), pos, state);
    }
}
