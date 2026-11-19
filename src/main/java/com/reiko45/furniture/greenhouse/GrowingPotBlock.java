package com.reiko45.furniture.greenhouse;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A big pot that a flower or sapling grows in. The plant starts small and grows through
 * {@link #GROWTH} stages on its own (random ticks), faster with bone meal or a watering can. Taking the
 * plant out empties the pot, so the next one starts small again.
 */
public class GrowingPotBlock extends PlantHolderBlock implements BonemealableBlock {
    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 0, 3);
    public static final int FULL = 3;

    public GrowingPotBlock(Properties properties, VoxelShape northShape, List<DisplaySlot> slots) {
        super(properties, northShape, slots);
        registerDefaultState(defaultBlockState().setValue(GROWTH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GROWTH);
    }

    /** From 0.6 of its size when just planted to 1.4 when fully grown. */
    @Override
    public float plantScale(BlockState state) {
        return 0.6F + state.getValue(GROWTH) * (0.8F / FULL);
    }

    public static boolean hasPlant(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof DisplayBlockEntity display && !display.isEmpty();
    }

    /**
     * Grows the plant one stage, if there is one and it isn't fully grown.
     *
     * @param player who made it grow (bone meal, watering), for the Green Thumb advancement
     * @return whether it grew
     */
    public static boolean grow(Level level, BlockPos pos, @Nullable Player player) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof GrowingPotBlock) || state.getValue(GROWTH) >= FULL || !hasPlant(level, pos)) {
            return false;
        }
        int growth = state.getValue(GROWTH) + 1;
        level.setBlock(pos, state.setValue(GROWTH, growth), Block.UPDATE_ALL);
        if (growth == FULL && player != null) {
            FurnitureActionTrigger.award(player, FurnitureActionTrigger.GREEN_THUMB);
        }
        return true;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(GROWTH) < FULL;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            grow(level, pos, null);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.BONE_MEAL) && state.getValue(GROWTH) < FULL && hasPlant(level, pos)) {
            if (!level.isClientSide && grow(level, pos, player)) {
                stack.consume(1, player);
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 15);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        if (!level.isClientSide && !hasPlant(level, pos) && state.getValue(GROWTH) > 0) {
            level.setBlock(pos, level.getBlockState(pos).setValue(GROWTH, 0), Block.UPDATE_ALL);
        }
        return result;
    }

    // Dispensers with bone meal.

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(GROWTH) < FULL && hasPlant(level, pos);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        grow(level, pos, null);
    }
}
