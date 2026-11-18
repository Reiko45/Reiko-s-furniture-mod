package com.reiko45.furniture.bathroom;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Two-block-tall shower. Right-click either half to turn the water on or off. Running water
 * puts out anyone standing in it who is on fire.
 */
public class ShowerBlock extends FurnitureBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = TallBlockHelper.HALF;
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    private static final VoxelShape LOWER = ShapeUtil.boxes(ShapeUtil.box(0, 0, 0, 16, 1, 16),
            ShapeUtil.box(0, 1, 15, 16, 16, 16), ShapeUtil.box(0, 1, 0, 0.5, 16, 15), ShapeUtil.box(15.5, 1, 0, 16, 16, 15));
    private static final VoxelShape UPPER = ShapeUtil.boxes(ShapeUtil.box(0, 0, 15, 16, 14, 16),
            ShapeUtil.box(0, 0, 0, 0.5, 14, 15), ShapeUtil.box(15.5, 0, 0, 16, 14, 15), ShapeUtil.box(0, 14, 0, 16, 15, 16));
    private final Map<Direction, VoxelShape> upperShapes = ShapeUtil.horizontal(UPPER);

    public ShowerBlock(Properties properties) {
        super(properties, LOWER);
        registerDefaultState(defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(RUNNING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, RUNNING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return TallBlockHelper.isUpper(state) ? upperShapes.get(state.getValue(FACING)) : super.getShape(state, level, pos, context);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean running = !state.getValue(RUNNING);
            level.setBlock(pos, state.setValue(RUNNING, running), Block.UPDATE_ALL);
            BlockPos otherPos = TallBlockHelper.otherHalf(state, pos);
            BlockState other = level.getBlockState(otherPos);
            if (other.is(this)) {
                level.setBlock(otherPos, other.setValue(RUNNING, running), Block.UPDATE_ALL);
            }
            level.playSound(null, pos, running ? SoundEvents.BUCKET_EMPTY : SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6F, 1.2F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && state.getValue(RUNNING) && entity.isOnFire()) {
            entity.clearFire();
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.5F);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(RUNNING) || !TallBlockHelper.isUpper(state)) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + 0.4 + random.nextDouble() * 0.2;
            double z = pos.getZ() + 0.4 + random.nextDouble() * 0.2;
            level.addParticle(ParticleTypes.FALLING_WATER, x, pos.getY() + 0.6, z, 0, 0, 0);
        }
        if (random.nextInt(10) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.WEATHER_RAIN, SoundSource.BLOCKS,
                    0.15F, 1.2F, false);
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return TallBlockHelper.placementState(super.getStateForPlacement(context), context);
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
