package com.reiko45.furniture.office;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A safe: three rows of storage that only the player who placed it can open or break (players in
 * creative mode can too). Hoppers can't take items out, and it survives explosions.
 */
public class SafeBlock extends FurnitureStorageBlock {
    public SafeBlock(Properties properties, VoxelShape northShape) {
        super(properties, 3, northShape);
    }

    @Override
    public boolean usesSharedStorage() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.IRON_DOOR_OPEN;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.IRON_DOOR_CLOSE;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SafeBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof SafeBlockEntity safe) {
            safe.setOwner(player);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof SafeBlockEntity safe && !safe.canAccess(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.reikos_furniture.safe_locked", safe.ownerName()), true);
                level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.4F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SafeBlockEntity safe && !safe.canAccess(player)) {
            return 0.0F; // only the owner can break it
        }
        return super.getDestroyProgress(state, player, level, pos);
    }
}
