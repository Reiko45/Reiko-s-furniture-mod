package com.reiko45.furniture.core.block;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Furniture that turns on and off when right-clicked, such as lamps and the TV. */
public class SwitchableFurnitureBlock extends FurnitureBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public SwitchableFurnitureBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    /** Light level function for {@code Properties.lightLevel}: {@code level} when on, 0 when off. */
    public static ToIntFunction<BlockState> lightWhenOn(int level) {
        return state -> state.getValue(LIT) ? level : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockState toggled = state.cycle(LIT);
            level.setBlock(pos, toggled, Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F,
                    toggled.getValue(LIT) ? 0.6F : 0.5F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
