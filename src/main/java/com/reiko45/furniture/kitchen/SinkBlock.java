package com.reiko45.furniture.kitchen;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A kitchen sink with running water. Right-click with an empty bucket or glass bottle to fill it,
 * or with a water bucket to pour it out.
 */
public class SinkBlock extends FurnitureBlock {
    public SinkBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.BUCKET)) {
            return exchange(level, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET),
                    SoundEvents.BUCKET_FILL, GameEvent.FLUID_PICKUP);
        }
        if (stack.is(Items.GLASS_BOTTLE)) {
            return exchange(level, pos, player, hand, stack, PotionContents.createItemStack(Items.POTION, Potions.WATER),
                    SoundEvents.BOTTLE_FILL, GameEvent.FLUID_PICKUP);
        }
        if (stack.is(Items.WATER_BUCKET)) {
            return exchange(level, pos, player, hand, stack, new ItemStack(Items.BUCKET),
                    SoundEvents.BUCKET_EMPTY, GameEvent.FLUID_PLACE);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    private static ItemInteractionResult exchange(Level level, BlockPos pos, Player player, InteractionHand hand,
                                                  ItemStack used, ItemStack result, SoundEvent sound,
                                                  net.minecraft.core.Holder<GameEvent> event) {
        if (!level.isClientSide) {
            player.awardStat(Stats.ITEM_USED.get(used.getItem()));
            player.setItemInHand(hand, ItemUtils.createFilledResult(used, player, result));
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, event, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
