package com.reiko45.furniture.decor;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.registry.ModSounds;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A two-block-tall sliding glass door in the wall line (the back of the block). Right-click or a
 * redstone signal slides the glass panel aside into the wall next to it, leaving the doorway open.
 */
public class SlidingDoorBlock extends FurnitureBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = TallBlockHelper.HALF;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape CLOSED = ShapeUtil.boxes(ShapeUtil.box(0, 0, 13, 16, 16, 16));
    private static final Map<Direction, VoxelShape> CLOSED_SHAPES = ShapeUtil.horizontal(CLOSED);
    private static final Map<Direction, VoxelShape> OPEN_LOWER = ShapeUtil.horizontal(ShapeUtil.boxes(
            ShapeUtil.box(0, 0, 13, 1, 16, 16), ShapeUtil.box(15, 0, 13, 16, 16, 16), ShapeUtil.box(1, 0, 13, 15, 0.5, 16)));
    private static final Map<Direction, VoxelShape> OPEN_UPPER = ShapeUtil.horizontal(ShapeUtil.boxes(
            ShapeUtil.box(0, 0, 13, 1, 16, 16), ShapeUtil.box(15, 0, 13, 16, 16, 16), ShapeUtil.box(1, 15, 13, 15, 16, 16)));

    public SlidingDoorBlock(Properties properties) {
        super(properties, CLOSED);
        registerDefaultState(defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, OPEN, POWERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (!state.getValue(OPEN)) {
            return CLOSED_SHAPES.get(facing);
        }
        return (TallBlockHelper.isUpper(state) ? OPEN_UPPER : OPEN_LOWER).get(facing);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return state.getValue(OPEN);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            setOpen(level, pos, state, !state.getValue(OPEN), player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(TallBlockHelper.otherHalf(state, pos));
        if (powered != state.getValue(POWERED)) {
            BlockState updated = state.setValue(POWERED, powered);
            level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
            BlockPos otherPos = TallBlockHelper.otherHalf(state, pos);
            BlockState other = level.getBlockState(otherPos);
            if (other.is(this)) {
                level.setBlock(otherPos, other.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
            }
            if (powered != state.getValue(OPEN)) {
                setOpen(level, pos, updated, powered, null);
            }
        }
    }

    /** Opens or closes both halves. */
    public void setOpen(Level level, BlockPos pos, BlockState state, boolean open, @Nullable Player player) {
        level.setBlock(pos, state.setValue(OPEN, open), Block.UPDATE_ALL);
        BlockPos otherPos = TallBlockHelper.otherHalf(state, pos);
        BlockState other = level.getBlockState(otherPos);
        if (other.is(this)) {
            level.setBlock(otherPos, other.setValue(OPEN, open), Block.UPDATE_ALL);
        }
        level.playSound(null, pos, ModSounds.SLIDING_DOOR.get(), SoundSource.BLOCKS, 0.8F, open ? 1.0F : 0.9F);
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = TallBlockHelper.placementState(super.getStateForPlacement(context), context);
        if (state == null) {
            return null;
        }
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos())
                || context.getLevel().hasNeighborSignal(context.getClickedPos().above());
        return state.setValue(POWERED, powered).setValue(OPEN, powered);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TallBlockHelper.placeUpperHalf(level, pos, state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState gone = TallBlockHelper.checkOtherHalf(this, state, direction, neighborState);
        return gone != null ? gone : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        TallBlockHelper.preventCreativeDrop(this, level, pos, state, player);
        return super.playerWillDestroy(level, pos, state, player);
    }
}
