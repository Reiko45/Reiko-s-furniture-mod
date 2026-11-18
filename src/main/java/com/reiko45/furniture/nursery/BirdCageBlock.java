package com.reiko45.furniture.nursery;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * A bird cage. Right-click it with a parrot on your shoulder to let the parrot in; right-click it
 * again to let the parrot out. Breaking the cage frees the parrot too.
 */
public class BirdCageBlock extends FurnitureBlock implements EntityBlock {
    public BirdCageBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(4, 0, 4, 12, 13.5, 12)));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BirdCageBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof BirdCageBlockEntity cage)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (cage.hasBird()) {
            release(level, pos, cage);
        } else if (!player.getShoulderEntityLeft().isEmpty()) {
            cage.setBird(player.getShoulderEntityLeft());
            player.setShoulderEntityLeft(new CompoundTag());
            level.playSound(null, pos, SoundEvents.PARROT_AMBIENT, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (!player.getShoulderEntityRight().isEmpty()) {
            cage.setBird(player.getShoulderEntityRight());
            player.setShoulderEntityRight(new CompoundTag());
            level.playSound(null, pos, SoundEvents.PARROT_AMBIENT, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else {
            player.displayClientMessage(Component.translatable("message.reikos_furniture.bird_cage_empty"), true);
        }
        return InteractionResult.SUCCESS;
    }

    /** Lets the parrot out above the cage. */
    public static void release(Level level, BlockPos pos, BirdCageBlockEntity cage) {
        CompoundTag bird = cage.bird();
        cage.setBird(new CompoundTag());
        if (level instanceof ServerLevel server) {
            EntityType.create(bird, level).ifPresent(parrot -> {
                parrot.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, parrot.getYRot(), 0);
                server.addWithUUID(parrot);
            });
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof BirdCageBlockEntity cage && cage.hasBird()) {
            release(level, pos, cage);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
