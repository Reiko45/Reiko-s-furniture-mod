package com.reiko45.furniture.livingroom;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Two-block-tall floor lamp. Right-click either half, or power either half with redstone, to switch it.
 * The shade on top gives the light.
 */
public class FloorLampBlock extends SwitchableFurnitureBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = TallBlockHelper.HALF;

    private final Map<Direction, VoxelShape> upperShapes;

    public FloorLampBlock(Properties properties, VoxelShape lowerShape, VoxelShape upperShape) {
        super(properties, lowerShape);
        this.upperShapes = ShapeUtil.horizontal(upperShape);
        registerDefaultState(defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    /** Light level for {@code Properties.lightLevel}: only the lit upper half (the shade) gives light. */
    public static int lightLevel(BlockState state) {
        return state.getValue(LIT) && TallBlockHelper.isUpper(state) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return TallBlockHelper.isUpper(state)
                ? upperShapes.get(state.getValue(FACING))
                : super.getShape(state, level, pos, context);
    }

    @Override
    protected void setSwitched(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        BlockPos otherPos = TallBlockHelper.otherHalf(oldState, pos);
        BlockState other = level.getBlockState(otherPos);
        if (other.is(this)) {
            level.setBlock(otherPos, other.setValue(LIT, newState.getValue(LIT))
                    .setValue(POWERED, newState.getValue(POWERED)), Block.UPDATE_ALL);
        }
    }

    @Override
    protected boolean isPowered(Level level, BlockPos pos, BlockState state) {
        return level.hasNeighborSignal(pos) || level.hasNeighborSignal(TallBlockHelper.otherHalf(state, pos));
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
        return state.setValue(POWERED, powered).setValue(LIT, powered);
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
