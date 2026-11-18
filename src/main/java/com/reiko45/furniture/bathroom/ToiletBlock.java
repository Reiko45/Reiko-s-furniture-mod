package com.reiko45.furniture.bathroom;

import com.reiko45.furniture.core.block.SeatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A toilet. Right-click to sit on it; sneak and right-click to flush. */
public class ToiletBlock extends SeatBlock {
    public ToiletBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape, 9);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (player.isSecondaryUseActive()) {
            if (level instanceof ServerLevel server) {
                server.playSound(null, pos, ModSounds.TOILET_FLUSH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                server.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                        12, 0.2, 0.05, 0.2, 0.02);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }
}
