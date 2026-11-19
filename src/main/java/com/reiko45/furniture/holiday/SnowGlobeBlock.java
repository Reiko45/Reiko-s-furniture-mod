package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/** A snow globe. Right-click to shake it: snow swirls inside the glass for a few seconds. */
public class SnowGlobeBlock extends FurnitureBlock {
    public static final BooleanProperty SHAKEN = BooleanProperty.create("shaken");
    private static final int SNOW_TICKS = 80;

    public SnowGlobeBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(4, 0, 4, 12, 9.5, 12)));
        registerDefaultState(defaultBlockState().setValue(SHAKEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAKEN);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(SHAKEN, true), Block.UPDATE_CLIENTS);
            level.scheduleTick(pos, this, SNOW_TICKS);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.6F, 1.4F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(SHAKEN)) {
            level.setBlock(pos, state.setValue(SHAKEN, false), Block.UPDATE_CLIENTS);
        }
    }

    /** Snow drifting down inside the glass. animateTick is only called now and then, so a few flakes each time. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(SHAKEN)) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            level.addParticle(ParticleTypes.WHITE_ASH, pos.getX() + 0.35 + random.nextDouble() * 0.3,
                    pos.getY() + 0.25 + random.nextDouble() * 0.3, pos.getZ() + 0.35 + random.nextDouble() * 0.3, 0, -0.01, 0);
        }
    }
}
