package com.reiko45.furniture.gameroom;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A dartboard. Right-click to throw a dart and see your score. Arrows and other projectiles that hit
 * it score too, and give a short redstone signal like a target block.
 */
public class DartboardBlock extends FurnitureBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public DartboardBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    /** A random dart score: 1-20, sometimes doubled or tripled, or 25 / 50 for the bull. */
    public static int randomScore(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 3) {
            return 50;
        }
        if (roll < 8) {
            return 25;
        }
        int number = 1 + random.nextInt(20);
        return roll < 15 ? number * 3 : roll < 25 ? number * 2 : number;
    }

    /** Scores a hit: plays the sound and gives a short redstone pulse. */
    public void hit(Level level, BlockPos pos, BlockState state, Player player) {
        level.playSound(null, pos, SoundEvents.ARROW_HIT, SoundSource.BLOCKS, 0.8F, 1.3F);
        if (player != null) {
            player.displayClientMessage(Component.translatable("message.reikos_furniture.dart_score", randomScore(level.random)), true);
        }
        level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_ALL);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            hit(level, pos, state, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide) {
            hit(level, hit.getBlockPos(), state, projectile.getOwner() instanceof Player player ? player : null);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_ALL);
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }
}
