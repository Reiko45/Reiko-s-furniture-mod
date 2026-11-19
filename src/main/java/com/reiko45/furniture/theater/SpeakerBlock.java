package com.reiko45.furniture.theater;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A floor-standing speaker. While a jukebox or record player within {@link #RANGE} blocks has a record
 * in it, notes come out of the speaker's front.
 */
public class SpeakerBlock extends FurnitureBlock {
    public static final int RANGE = 8;

    public SpeakerBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    /** Whether music is playing nearby: a jukebox (or record player) with a record in it. */
    public static boolean musicNear(Level level, BlockPos pos) {
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-RANGE, -RANGE, -RANGE), pos.offset(RANGE, RANGE, RANGE))) {
            BlockState state = level.getBlockState(near);
            if (state.getBlock() instanceof JukeboxBlock && state.getValue(JukeboxBlock.HAS_RECORD)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!musicNear(level, pos)) {
            return;
        }
        Direction front = state.getValue(FACING);
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.5 + front.getStepX() * 0.45 + (random.nextDouble() - 0.5) * 0.3;
            double z = pos.getZ() + 0.5 + front.getStepZ() * 0.45 + (random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 0.4 + random.nextDouble() * 0.6;
            // For notes the x speed is the note's color, from 0 to 1.
            level.addParticle(ParticleTypes.NOTE, x, y, z, random.nextInt(25) / 24.0, 0.0, 0.0);
        }
    }
}
