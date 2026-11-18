package com.reiko45.furniture.core.block;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Furniture that turns on and off, such as lamps and the TV. Right-click to switch it, or use
 * redstone: it turns on when it starts receiving power and off when the power stops.
 */
public class SwitchableFurnitureBlock extends FurnitureBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SwitchableFurnitureBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(LIT, false).setValue(POWERED, false));
    }

    /** Light level function for {@code Properties.lightLevel}: {@code level} when on, 0 when off. */
    public static ToIntFunction<BlockState> lightWhenOn(int level) {
        return state -> state.getValue(LIT) ? level : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, POWERED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return state.setValue(POWERED, powered).setValue(LIT, powered);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockState switched = nextState(state);
            setSwitched(level, pos, state, switched);
            playClick(level, pos, switched.getValue(LIT));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** The state after a right-click. Lamps simply turn on or off; the TV also changes channel. */
    protected BlockState nextState(BlockState state) {
        return state.cycle(LIT);
    }

    /** Applies a new state. Two-block-tall pieces override this to update the other half too. */
    protected void setSwitched(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        level.setBlock(pos, newState, Block.UPDATE_ALL);
    }

    /** Whether this block (or, for tall pieces, any part of it) receives redstone power. */
    protected boolean isPowered(Level level, BlockPos pos, BlockState state) {
        return level.hasNeighborSignal(pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                   BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }
        boolean powered = isPowered(level, pos, state);
        if (powered != state.getValue(POWERED)) {
            // Follow the signal when it changes, but leave manual switching alone otherwise.
            BlockState switched = state.setValue(POWERED, powered).setValue(LIT, powered);
            setSwitched(level, pos, state, switched);
            if (switched.getValue(LIT) != state.getValue(LIT)) {
                playClick(level, pos, powered);
            }
        }
    }

    protected static void playClick(Level level, BlockPos pos, boolean on) {
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, on ? 0.6F : 0.5F);
    }
}
