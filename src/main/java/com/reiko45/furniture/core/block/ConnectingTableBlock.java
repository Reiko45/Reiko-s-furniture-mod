package com.reiko45.furniture.core.block;

import com.reiko45.furniture.core.ShapeUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A table that joins with the same table next to it. Each corner has a leg only when neither side
 * of that corner is joined, so several tables placed together form one long table.
 */
public class ConnectingTableBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final VoxelShape top;
    /** Leg shapes in the order north-west, north-east, south-east, south-west. */
    private final VoxelShape[] legs = new VoxelShape[4];
    private final Map<Integer, VoxelShape> shapeCache = new HashMap<>();

    /**
     * @param top          shape of the table top
     * @param northWestLeg shape of the leg in the north-west corner; the other legs are rotated copies
     */
    public ConnectingTableBlock(Properties properties, VoxelShape top, VoxelShape northWestLeg) {
        super(properties);
        this.top = top;
        VoxelShape leg = northWestLeg;
        for (int i = 0; i < 4; i++) {
            legs[i] = leg;
            leg = ShapeUtil.rotateClockwise(leg);
        }
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    public static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> throw new IllegalArgumentException("Tables only connect horizontally");
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }

    protected boolean connectsTo(BlockState neighbor) {
        return neighbor.is(this);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = defaultBlockState()
                .setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            state = state.setValue(property(dir), connectsTo(level.getBlockState(pos.relative(dir))));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (direction.getAxis().isHorizontal()) {
            return state.setValue(property(direction), connectsTo(neighborState));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean n = state.getValue(NORTH), e = state.getValue(EAST), s = state.getValue(SOUTH), w = state.getValue(WEST);
        int key = (n ? 1 : 0) | (e ? 2 : 0) | (s ? 4 : 0) | (w ? 8 : 0);
        return shapeCache.computeIfAbsent(key, k -> {
            VoxelShape shape = top;
            if (!n && !w) shape = Shapes.or(shape, legs[0]);
            if (!n && !e) shape = Shapes.or(shape, legs[1]);
            if (!s && !e) shape = Shapes.or(shape, legs[2]);
            if (!s && !w) shape = Shapes.or(shape, legs[3]);
            return shape.optimize();
        });
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState result = state;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            result = result.setValue(property(rotation.rotate(dir)), state.getValue(property(dir)));
        }
        return result;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState result = state;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            result = result.setValue(property(mirror.mirror(dir)), state.getValue(property(dir)));
        }
        return result;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
