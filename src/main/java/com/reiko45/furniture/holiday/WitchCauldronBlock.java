package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** A witch's cauldron full of glowing green brew that bubbles and sparkles. Just for show. */
public class WitchCauldronBlock extends FurnitureBlock {
    public WitchCauldronBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(1, 0, 1, 15, 13, 15)));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 3; i++) {
            level.addParticle(ParticleTypes.WITCH, pos.getX() + 0.25 + random.nextDouble() * 0.5, pos.getY() + 0.85,
                    pos.getZ() + 0.25 + random.nextDouble() * 0.5, 0, 0.05, 0);
        }
        level.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + 0.3 + random.nextDouble() * 0.4, pos.getY() + 0.82,
                pos.getZ() + 0.3 + random.nextDouble() * 0.4, 0, 0.02, 0);
        if (random.nextInt(4) == 0) {
            level.playLocalSound(pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.4F, 0.8F, false);
        }
    }
}
