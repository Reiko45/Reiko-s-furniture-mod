package com.reiko45.furniture.decor;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * A wooden-framed window pane. Windows of the same wood facing the same way join into one big window:
 * the frame only shows on the outer edges. Right-click to tilt a window open.
 */
public class FramedWindowBlock extends FurnitureBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    /** Joined on the west side of a north-facing window (the left of the model). */
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    /** Joined on the east side of a north-facing window. */
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    public FramedWindowBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 0, 7, 16, 16, 9)));
        registerDefaultState(defaultBlockState().setValue(OPEN, false).setValue(UP, false).setValue(DOWN, false)
                .setValue(LEFT, false).setValue(RIGHT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, UP, DOWN, LEFT, RIGHT);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : connect(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return connect(super.updateShape(state, direction, neighborState, level, pos, neighborPos), level, pos);
    }

    private BlockState connect(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return state.setValue(UP, joins(state, level.getBlockState(pos.above())))
                .setValue(DOWN, joins(state, level.getBlockState(pos.below())))
                .setValue(LEFT, joins(state, level.getBlockState(pos.relative(facing.getCounterClockWise()))))
                .setValue(RIGHT, joins(state, level.getBlockState(pos.relative(facing.getClockWise()))));
    }

    private boolean joins(BlockState state, BlockState other) {
        return other.is(this) && other.getValue(FACING) == state.getValue(FACING);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean open = !state.getValue(OPEN);
            level.setBlock(pos, state.setValue(OPEN, open), Block.UPDATE_ALL);
            level.playSound(null, pos, open ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE,
                    SoundSource.BLOCKS, 0.7F, 1.2F);
            level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState mirrored = super.mirror(state, mirror);
        return mirror == Mirror.NONE ? mirrored : mirrored.setValue(LEFT, state.getValue(RIGHT)).setValue(RIGHT, state.getValue(LEFT));
    }
}
