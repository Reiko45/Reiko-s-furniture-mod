package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A strand of fairy lights on a wall or under a ceiling. Strands side by side (same way round) join into
 * one long strand; the ends get a hook. They come on when placed; right-click, redstone or a light switch
 * turns them off and on.
 *
 * <p>{@link #WEST} and {@link #EAST} are the ends of the strand as in the north-facing model: west is
 * the block's counter-clockwise side.
 */
public class StringLightsBlock extends SwitchableFurnitureBlock {
    public static final EnumProperty<AttachFace> FACE = EnumProperty.create("face", AttachFace.class, AttachFace.WALL, AttachFace.CEILING);
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");

    private static final Map<Direction, VoxelShape> WALL_SHAPES = ShapeUtil.horizontal(ShapeUtil.boxes(ShapeUtil.box(0, 9, 14, 16, 14, 16)));
    private static final Map<Direction, VoxelShape> CEILING_SHAPES = ShapeUtil.horizontal(ShapeUtil.boxes(ShapeUtil.box(0, 11, 6, 16, 16, 10)));

    public StringLightsBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 9, 14, 16, 14, 16)));
        registerDefaultState(defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(WEST, false).setValue(EAST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACE, WEST, EAST);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(FACE) == AttachFace.CEILING ? CEILING_SHAPES : WALL_SHAPES).get(state.getValue(FACING));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        AttachFace face;
        Direction facing;
        if (clicked == Direction.DOWN) {
            face = AttachFace.CEILING;
            facing = context.getHorizontalDirection().getOpposite();
        } else if (clicked.getAxis().isHorizontal()) {
            face = AttachFace.WALL;
            facing = clicked;
        } else {
            return null; // they hang from walls and ceilings, not on the floor
        }
        BlockPos pos = context.getClickedPos();
        boolean powered = context.getLevel().hasNeighborSignal(pos);
        BlockState state = defaultBlockState().setValue(FACING, facing).setValue(FACE, face)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER)
                .setValue(POWERED, powered).setValue(LIT, true);
        return joined(state, context.getLevel(), pos);
    }

    /** The state with its ends joined to the strands beside it. */
    public BlockState joined(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return state.setValue(WEST, joins(state, level.getBlockState(pos.relative(facing.getCounterClockWise()))))
                .setValue(EAST, joins(state, level.getBlockState(pos.relative(facing.getClockWise()))));
    }

    private boolean joins(BlockState state, BlockState neighbor) {
        return neighbor.is(this) && neighbor.getValue(FACE) == state.getValue(FACE) && neighbor.getValue(FACING) == state.getValue(FACING);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level,
                                     BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighbor, level, pos, neighborPos);
        return direction.getAxis().isHorizontal() ? joined(updated, level, pos) : updated;
    }
}
