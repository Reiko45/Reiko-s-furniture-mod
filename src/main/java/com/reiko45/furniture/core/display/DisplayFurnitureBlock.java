package com.reiko45.furniture.core.display;

import com.reiko45.furniture.core.block.FurnitureBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Furniture that shows the items placed on it (shelves, plates, cutting boards). Right-click with an
 * item to put one down; right-click with an empty hand to take the last one back.
 */
public class DisplayFurnitureBlock extends FurnitureBlock implements EntityBlock {
    private final List<DisplaySlot> slots;

    public DisplayFurnitureBlock(Properties properties, VoxelShape northShape, List<DisplaySlot> slots) {
        super(properties, northShape);
        this.slots = List.copyOf(slots);
    }

    public List<DisplaySlot> slots() {
        return slots;
    }

    /** Which items may be put on it. Everything by default. */
    public boolean accepts(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.isEmpty() && accepts(stack) && level.getBlockEntity(pos) instanceof DisplayBlockEntity display) {
            if (!level.isClientSide && display.insert(stack, player)) {
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
                return ItemInteractionResult.SUCCESS;
            }
            return display.hasSpace() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof DisplayBlockEntity display && !display.isEmpty()) {
            if (!level.isClientSide) {
                ItemStack taken = display.takeLast();
                if (!player.getInventory().add(taken)) {
                    player.drop(taken, false);
                }
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof DisplayBlockEntity display) {
            display.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
