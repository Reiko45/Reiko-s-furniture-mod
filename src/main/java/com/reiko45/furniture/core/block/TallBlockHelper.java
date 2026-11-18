package com.reiko45.furniture.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * Shared logic for two-block-tall furniture (wardrobe, fridge, floor lamp). It works like a door:
 * placing the item fills the block above too, and breaking either half removes both.
 * Blocks using it need the {@link #HALF} and {@link FurnitureBlock#WATERLOGGED} properties.
 */
public final class TallBlockHelper {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private TallBlockHelper() {
    }

    public static boolean isUpper(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    /** Position of the other half. */
    public static BlockPos otherHalf(BlockState state, BlockPos pos) {
        return isUpper(state) ? pos.below() : pos.above();
    }

    /** Returns the lower-half state if there is room above, or null to cancel placement. */
    @Nullable
    public static BlockState placementState(@Nullable BlockState state, BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (state == null || pos.getY() >= level.getMaxBuildHeight() - 1
                || !level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        return state.setValue(HALF, DoubleBlockHalf.LOWER);
    }

    /** Places the upper half; call from {@code setPlacedBy}. */
    public static void placeUpperHalf(Level level, BlockPos lowerPos, BlockState lowerState) {
        BlockPos above = lowerPos.above();
        level.setBlock(above, lowerState.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(FurnitureBlock.WATERLOGGED, level.getFluidState(above).getType() == Fluids.WATER), Block.UPDATE_ALL);
    }

    /**
     * Call from {@code updateShape}. Returns the state to turn into when the other half is gone,
     * or null if this half is still fine.
     */
    @Nullable
    public static BlockState checkOtherHalf(Block self, BlockState state, Direction direction, BlockState neighbor) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean towardsOtherHalf = direction.getAxis() == Direction.Axis.Y
                && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP);
        if (towardsOtherHalf && !(neighbor.is(self) && neighbor.getValue(HALF) != half)) {
            return emptyState(state);
        }
        return null;
    }

    /** Call from {@code playerWillDestroy}: in creative mode, breaking the top must not drop an item from the bottom. */
    public static void preventCreativeDrop(Block self, Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && isUpper(state)) {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if (lower.is(self) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(below, emptyState(lower), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, below, Block.getId(lower));
            }
        }
    }

    private static BlockState emptyState(BlockState state) {
        return state.getValue(FurnitureBlock.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }
}
