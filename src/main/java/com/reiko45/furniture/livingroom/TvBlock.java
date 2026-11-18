package com.reiko45.furniture.livingroom;

import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Flat-screen TV with four channels. Each right-click goes to the next channel:
 * off, channel 1, 2, 3, 4, then off again. Redstone power switches it on and off.
 */
public class TvBlock extends SwitchableFurnitureBlock {
    public static final int CHANNELS = 4;
    public static final IntegerProperty CHANNEL = IntegerProperty.create("channel", 1, CHANNELS);

    public TvBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(CHANNEL, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CHANNEL);
    }

    @Override
    protected void playSwitchSound(Level level, BlockPos pos, BlockState before, BlockState after) {
        if (after.getValue(LIT)) {
            // Static when switching on or changing channel.
            level.playSound(null, pos, ModSounds.TV_STATIC.get(), SoundSource.BLOCKS, 0.6F, 1.0F);
        } else {
            super.playSwitchSound(level, pos, before, after);
        }
    }

    @Override
    protected BlockState nextState(BlockState state) {
        if (!state.getValue(LIT)) {
            return state.setValue(LIT, true).setValue(CHANNEL, 1);
        }
        int channel = state.getValue(CHANNEL);
        return channel < CHANNELS ? state.setValue(CHANNEL, channel + 1) : state.setValue(LIT, false).setValue(CHANNEL, 1);
    }
}
