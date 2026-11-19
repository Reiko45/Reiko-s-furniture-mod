package com.reiko45.furniture.theater;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.block.SeatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A padded recliner. Right-click with an empty hand to sit; sneak and right-click (empty-handed) to tilt
 * the back and pull out the footrest, or put them back.
 */
public class ReclinerBlock extends SeatBlock {
    public static final BooleanProperty RECLINED = BooleanProperty.create("reclined");
    /** How far around a seat to look for a screen that is showing a film. */
    private static final int SCREEN_SEARCH = 14;

    public ReclinerBlock(Properties properties, VoxelShape northShape, double seatHeight) {
        super(properties, northShape, seatHeight);
        registerDefaultState(defaultBlockState().setValue(RECLINED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RECLINED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isSecondaryUseActive() && !player.isPassenger()) {
            if (!level.isClientSide) {
                boolean reclined = !state.getValue(RECLINED);
                level.setBlock(pos, state.setValue(RECLINED, reclined), Block.UPDATE_ALL);
                level.playSound(null, pos, reclined ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE,
                        SoundSource.BLOCKS, 0.5F, 0.7F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        if (!level.isClientSide && player.isPassenger() && filmShowingNear(level, pos)) {
            FurnitureActionTrigger.award(player, FurnitureActionTrigger.MOVIE_NIGHT);
        }
        return result;
    }

    /** Whether a projector screen nearby is showing a film. */
    static boolean filmShowingNear(Level level, BlockPos pos) {
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-SCREEN_SEARCH, -4, -SCREEN_SEARCH),
                pos.offset(SCREEN_SEARCH, 6, SCREEN_SEARCH))) {
            BlockState state = level.getBlockState(near);
            if (state.getBlock() instanceof ProjectorScreenBlock && state.getValue(ProjectorScreenBlock.LIT)) {
                return true;
            }
        }
        return false;
    }
}
