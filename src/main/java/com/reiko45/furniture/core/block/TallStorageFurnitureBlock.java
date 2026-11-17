package com.reiko45.furniture.core.block;

import com.reiko45.furniture.core.ShapeUtil;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Two-block-tall storage furniture (wardrobe, fridge). Works like a door: placing it fills the block
 * above, and breaking either half removes both. The inventory lives in the lower half.
 */
public class TallStorageFurnitureBlock extends FurnitureStorageBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private final Map<Direction, VoxelShape> upperShapes;

    public TallStorageFurnitureBlock(Properties properties, int rows, VoxelShape lowerShape, VoxelShape upperShape) {
        super(properties, rows, lowerShape);
        this.upperShapes = ShapeUtil.horizontal(upperShape);
        registerDefaultState(defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER
                ? upperShapes.get(state.getValue(FACING))
                : super.getShape(state, level, pos, context);
    }

    @Override
    public BlockPos storagePos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? super.newBlockEntity(pos, state) : null;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos above = pos.above();
        level.setBlock(above, state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, level.getFluidState(above).getType() == Fluids.WATER), Block.UPDATE_ALL);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean otherHalfSide = direction.getAxis() == Direction.Axis.Y
                && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP);
        if (otherHalfSide && !(neighborState.is(this) && neighborState.getValue(HALF) != half)) {
            return emptyState(state);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // In creative mode, breaking the top must not make the bottom drop an item.
        if (!level.isClientSide && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if (lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(below, emptyState(lower), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, below, Block.getId(lower));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static BlockState emptyState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }
}
