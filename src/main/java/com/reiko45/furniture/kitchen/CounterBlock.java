package com.reiko45.furniture.kitchen;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Kitchen counter with storage. Like vanilla stairs, counters in an L shape turn into corner pieces:
 * an inner corner (front hidden by the next counter) gets a plain panel, and an outer corner gets
 * doors on its open side as well as its front. Sinks count as counters.
 */
public class CounterBlock extends FurnitureStorageBlock {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public CounterBlock(Properties properties, int rows, VoxelShape northShape) {
        super(properties, rows, northShape);
        registerDefaultState(defaultBlockState().setValue(SHAPE, Shape.STRAIGHT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(SHAPE, shapeFor(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        return direction.getAxis().isHorizontal() ? updated.setValue(SHAPE, shapeFor(updated, level, pos)) : updated;
    }

    private static Shape shapeFor(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockState front = level.getBlockState(pos.relative(facing));
        if (isCounter(front) && front.getValue(FACING).getAxis() != facing.getAxis()) {
            return Shape.INNER;
        }
        BlockState back = level.getBlockState(pos.relative(facing.getOpposite()));
        if (isCounter(back) && back.getValue(FACING).getAxis() != facing.getAxis()) {
            return back.getValue(FACING) == facing.getCounterClockWise() ? Shape.OUTER_LEFT : Shape.OUTER_RIGHT;
        }
        return Shape.STRAIGHT;
    }

    private static boolean isCounter(BlockState state) {
        return state.getBlock() instanceof CounterBlock || state.getBlock() instanceof SinkBlock;
    }

    /** Shape of a counter in a row or at a corner. */
    public enum Shape implements StringRepresentable {
        STRAIGHT("straight"),
        /** Inner corner of an L: the front is covered by the next counter. */
        INNER("inner"),
        /** Outer corner with doors on the front and the left side. */
        OUTER_LEFT("outer_left"),
        /** Outer corner with doors on the front and the right side. */
        OUTER_RIGHT("outer_right");

        private final String name;

        Shape(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
