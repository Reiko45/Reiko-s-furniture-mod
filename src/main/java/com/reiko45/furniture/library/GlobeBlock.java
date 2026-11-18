package com.reiko45.furniture.library;

import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A globe on a stand. Right-click to spin it; it turns in 22.5 degree steps and slowly comes to a stop. */
public class GlobeBlock extends FurnitureBlock {
    public static final IntegerProperty ANGLE = IntegerProperty.create("angle", 0, 15);
    public static final IntegerProperty SPEED = IntegerProperty.create("speed", 0, 3);

    public GlobeBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(ANGLE, 0).setValue(SPEED, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ANGLE, SPEED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(SPEED, 3), Block.UPDATE_ALL);
            level.scheduleTick(pos, this, 2);
            level.playSound(null, pos, ModSounds.GLOBE_SPIN.get(), SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int speed = state.getValue(SPEED);
        if (speed == 0) {
            return;
        }
        int angle = (state.getValue(ANGLE) + speed) % 16;
        if (random.nextInt(6) == 0) {
            speed--;
        }
        level.setBlock(pos, state.setValue(ANGLE, angle).setValue(SPEED, speed), Block.UPDATE_CLIENTS);
        if (speed > 0) {
            level.scheduleTick(pos, this, 2);
        }
    }
}
