package com.reiko45.furniture.livingroom;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.block.SeatBlock;
import java.util.EnumMap;
import java.util.Map;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A sofa you can sit on. Sofas facing the same way join side by side into one long sofa:
 * only the two ends keep their armrests. Two rows at a right angle join into an L-shaped sofa,
 * with a corner piece that has a backrest on two sides. Any colors can be joined.
 */
public class SofaBlock extends SeatBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private final Map<Part, Map<Direction, VoxelShape>> shapes = new EnumMap<>(Part.class);

    @Nullable
    private final Style style;

    public SofaBlock(Properties properties) {
        this(properties, 8, null);
    }

    /**
     * A sofa whose seat is {@code seatHeight} pixels up (8 for the classic sofa). The backrest, arms and
     * corner backrest sit on the seat, 7, 3 and 7 pixels tall. Sofas only join sofas of the same
     * style ({@code null} for the classic sofa).
     */
    public SofaBlock(Properties properties, double seatHeight, @Nullable Style style) {
        super(properties, base(seatHeight), seatHeight);
        this.style = style;
        double h = seatHeight;
        VoxelShape leftArm = ShapeUtil.boxes(ShapeUtil.box(0, h, 1, 2.5, h + 3, 11.5));
        VoxelShape rightArm = ShapeUtil.boxes(ShapeUtil.box(13.5, h, 1, 16, h + 3, 11.5));
        VoxelShape leftBack = ShapeUtil.boxes(ShapeUtil.box(0, h, 0, 4.5, h + 7, 11.5));
        VoxelShape rightBack = ShapeUtil.boxes(ShapeUtil.box(11.5, h, 0, 16, h + 7, 11.5));
        for (Part part : Part.values()) {
            VoxelShape shape = base(h);
            if (part.hasLeftArm()) {
                shape = Shapes.or(shape, leftArm);
            }
            if (part.hasRightArm()) {
                shape = Shapes.or(shape, rightArm);
            }
            if (part == Part.CORNER_LEFT) {
                shape = Shapes.or(shape, leftBack);
            }
            if (part == Part.CORNER_RIGHT) {
                shape = Shapes.or(shape, rightBack);
            }
            shapes.put(part, ShapeUtil.horizontal(shape.optimize()));
        }
        registerDefaultState(defaultBlockState().setValue(PART, Part.SINGLE));
    }

    private static VoxelShape base(double h) {
        return ShapeUtil.boxes(ShapeUtil.box(0, 0, 1, 16, h, 16), ShapeUtil.box(0, h, 11.5, 16, h + 7, 16));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(PART, partFor(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        return direction.getAxis().isHorizontal() ? updated.setValue(PART, partFor(updated, level, pos)) : updated;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(PART)).get(state.getValue(FACING));
    }

    /** Whether this sofa joins up with {@code other}: any sofa of the same style, whatever its color. */
    public boolean joins(Block other) {
        return other instanceof SofaBlock sofa && sofa.style == style;
    }

    /** Works out which part of a long or L-shaped sofa this block is, from the sofas around it. */
    private Part partFor(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        // A sofa right in front, turned sideways, makes this the inner corner of an L.
        BlockState front = level.getBlockState(pos.relative(facing));
        if (joins(front.getBlock())) {
            if (front.getValue(FACING) == facing.getClockWise()) {
                return Part.CORNER_LEFT;
            }
            if (front.getValue(FACING) == facing.getCounterClockWise()) {
                return Part.CORNER_RIGHT;
            }
        }
        // Seen by someone sitting on the sofa (who faces the same way as the sofa).
        boolean left = connects(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing.getClockWise(), facing);
        boolean right = connects(level.getBlockState(pos.relative(facing.getClockWise())), facing.getCounterClockWise(), facing);
        if (left && right) {
            return Part.MIDDLE;
        }
        if (right) {
            return Part.LEFT;
        }
        if (left) {
            return Part.RIGHT;
        }
        return Part.SINGLE;
    }

    /**
     * A neighbour on the left or right joins if it faces the same way, or if it is the corner of an L
     * (it then faces {@code cornerFacing}, and this sofa is the one in front of it).
     */
    private boolean connects(BlockState neighbor, Direction cornerFacing, Direction facing) {
        if (!joins(neighbor.getBlock())) {
            return false;
        }
        Direction other = neighbor.getValue(FACING);
        return other == facing || other == cornerFacing;
    }

    /** Which part of a (long) sofa a block is. The left end keeps its left armrest, and so on. */
    public enum Part implements StringRepresentable {
        SINGLE("single"),
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right"),
        /** Inner corner of an L with the extra backrest on the left. */
        CORNER_LEFT("corner_left"),
        /** Inner corner of an L with the extra backrest on the right. */
        CORNER_RIGHT("corner_right");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        public boolean hasLeftArm() {
            return this == SINGLE || this == LEFT;
        }

        public boolean hasRightArm() {
            return this == SINGLE || this == RIGHT;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
