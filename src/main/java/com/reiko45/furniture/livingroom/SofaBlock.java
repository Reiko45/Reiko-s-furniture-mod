package com.reiko45.furniture.livingroom;

import com.reiko45.furniture.core.ShapeUtil;
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
 * only the two ends keep their armrests. Any colors can be joined.
 */
public class SofaBlock extends SeatBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private static final VoxelShape BASE = ShapeUtil.boxes(ShapeUtil.box(0, 0, 1, 16, 8, 16), ShapeUtil.box(0, 8, 11.5, 16, 15, 16));
    private static final VoxelShape LEFT_ARM = ShapeUtil.boxes(ShapeUtil.box(0, 8, 1, 2.5, 11, 11.5));
    private static final VoxelShape RIGHT_ARM = ShapeUtil.boxes(ShapeUtil.box(13.5, 8, 1, 16, 11, 11.5));

    private final Map<Part, Map<Direction, VoxelShape>> shapes = new EnumMap<>(Part.class);

    public SofaBlock(Properties properties) {
        super(properties, BASE, 8);
        for (Part part : Part.values()) {
            VoxelShape shape = BASE;
            if (part.hasLeftArm()) {
                shape = Shapes.or(shape, LEFT_ARM);
            }
            if (part.hasRightArm()) {
                shape = Shapes.or(shape, RIGHT_ARM);
            }
            shapes.put(part, ShapeUtil.horizontal(shape.optimize()));
        }
        registerDefaultState(defaultBlockState().setValue(PART, Part.SINGLE));
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

    /** Works out which part of a long sofa this block is, from the sofas on its left and right. */
    private static Part partFor(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        // Seen by someone sitting on the sofa (who faces the same way as the sofa).
        boolean left = isSofaFacing(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
        boolean right = isSofaFacing(level.getBlockState(pos.relative(facing.getClockWise())), facing);
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

    private static boolean isSofaFacing(BlockState state, Direction facing) {
        return state.getBlock() instanceof SofaBlock && state.getValue(FACING) == facing;
    }

    /** Which part of a (long) sofa a block is. The left end keeps its left armrest, and so on. */
    public enum Part implements StringRepresentable {
        SINGLE("single"),
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

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
