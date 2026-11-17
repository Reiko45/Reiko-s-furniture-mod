package com.reiko45.furniture.core.block;

import com.reiko45.furniture.core.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Furniture you can sit on by right-clicking it with an empty hand. */
public class SeatBlock extends FurnitureBlock {
    private final double seatHeight;

    /**
     * @param seatHeight height of the seat surface in pixels (0-16)
     */
    public SeatBlock(Properties properties, VoxelShape northShape, double seatHeight) {
        super(properties, northShape);
        this.seatHeight = seatHeight;
    }

    /** Height of the seat surface in blocks. */
    public double seatHeight() {
        return seatHeight / 16.0;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (player.isPassenger() || player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return SeatEntity.sit(level, pos, seatHeight(), player) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }
}
