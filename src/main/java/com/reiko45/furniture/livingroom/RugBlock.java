package com.reiko45.furniture.livingroom;

import com.reiko45.furniture.core.block.ConnectingTableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A flat rug with a white border. Rugs of the same color placed next to each other join into one big
 * rug: the border only shows around the outside. Like carpet, it needs something underneath.
 */
public class RugBlock extends Block {
    public static final BooleanProperty NORTH = ConnectingTableBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingTableBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingTableBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingTableBlock.WEST;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public RugBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            state = state.setValue(ConnectingTableBlock.property(dir),
                    context.getLevel().getBlockState(context.getClickedPos().relative(dir)).is(this));
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !level.isEmptyBlock(pos.below());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (direction.getAxis().isHorizontal()) {
            return state.setValue(ConnectingTableBlock.property(direction), neighborState.is(this));
        }
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState result = state;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            result = result.setValue(ConnectingTableBlock.property(rotation.rotate(dir)), state.getValue(ConnectingTableBlock.property(dir)));
        }
        return result;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState result = state;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            result = result.setValue(ConnectingTableBlock.property(mirror.mirror(dir)), state.getValue(ConnectingTableBlock.property(dir)));
        }
        return result;
    }
}
