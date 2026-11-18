package com.reiko45.furniture.core.block;

import com.reiko45.furniture.core.ShapeUtil;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A strip along the foot (baseboard) or top (crown molding) of a wall. It sits in the block in front
 * of the wall, facing into the room, and turns corners like stairs: where two walls meet it runs along
 * both (inner corner), and around a jutting wall corner it fills just the corner (outer corner).
 */
public class WallTrimBlock extends FurnitureBlock {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    private static final double DEPTH = 2;

    private final boolean top;
    private final Map<Shape, Map<Direction, VoxelShape>> shapes = new EnumMap<>(Shape.class);

    /** @param top true for crown molding (top of the block), false for a baseboard (bottom). */
    public WallTrimBlock(Properties properties, boolean top) {
        super(properties, northShape(Shape.STRAIGHT, top));
        this.top = top;
        for (Shape shape : Shape.values()) {
            shapes.put(shape, ShapeUtil.horizontal(northShape(shape, top)));
        }
        registerDefaultState(defaultBlockState().setValue(SHAPE, Shape.STRAIGHT));
    }

    private static VoxelShape northShape(Shape shape, boolean top) {
        double y1 = top ? 12.5 : 0;
        double y2 = top ? 16 : 3.5;
        double back = 16 - DEPTH;
        VoxelShape straight = ShapeUtil.boxes(ShapeUtil.box(0, y1, back, 16, y2, 16));
        return switch (shape) {
            case STRAIGHT -> straight;
            case INNER_LEFT -> Shapes.or(straight, ShapeUtil.boxes(ShapeUtil.box(back, y1, 0, 16, y2, back)));
            case INNER_RIGHT -> Shapes.or(straight, ShapeUtil.boxes(ShapeUtil.box(0, y1, 0, DEPTH, y2, back)));
            case OUTER_LEFT -> ShapeUtil.boxes(ShapeUtil.box(back, y1, back, 16, y2, 16));
            case OUTER_RIGHT -> ShapeUtil.boxes(ShapeUtil.box(0, y1, back, DEPTH, y2, 16));
        };
    }

    public boolean isTop() {
        return top;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(SHAPE)).get(state.getValue(FACING));
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

    /** Same rules as vanilla stairs: a crossing trim in front makes an inner corner, one behind an outer corner. */
    private Shape shapeFor(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockState front = level.getBlockState(pos.relative(facing));
        if (sameKind(front) && front.getValue(FACING).getAxis() != facing.getAxis()) {
            return front.getValue(FACING) == facing.getCounterClockWise() ? Shape.INNER_LEFT : Shape.INNER_RIGHT;
        }
        BlockState back = level.getBlockState(pos.relative(facing.getOpposite()));
        if (sameKind(back) && back.getValue(FACING).getAxis() != facing.getAxis()) {
            return back.getValue(FACING) == facing.getCounterClockWise() ? Shape.OUTER_LEFT : Shape.OUTER_RIGHT;
        }
        return Shape.STRAIGHT;
    }

    private boolean sameKind(BlockState state) {
        return state.getBlock() instanceof WallTrimBlock other && other.top == top;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState mirrored = super.mirror(state, mirror);
        return mirror == Mirror.NONE ? mirrored : mirrored.setValue(SHAPE, state.getValue(SHAPE).mirrored());
    }

    /** Straight, or a corner. LEFT corners have their extra part on the east side of a north-facing trim. */
    public enum Shape implements StringRepresentable {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        Shape(String name) {
            this.name = name;
        }

        Shape mirrored() {
            return switch (this) {
                case INNER_LEFT -> INNER_RIGHT;
                case INNER_RIGHT -> INNER_LEFT;
                case OUTER_LEFT -> OUTER_RIGHT;
                case OUTER_RIGHT -> OUTER_LEFT;
                default -> this;
            };
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
