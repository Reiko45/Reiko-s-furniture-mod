package com.reiko45.furniture.greenhouse;

import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A pot, stand or box that flowers and saplings are planted in (anything in {@code #reikos_furniture:plants}).
 * Right-click with a plant to plant it, with an empty hand to take the last one out. Other items in hand
 * (a watering can, bone meal, a hammer) are used as normal and leave the plants alone.
 */
public class PlantHolderBlock extends DisplayFurnitureBlock {
    public PlantHolderBlock(Properties properties, VoxelShape northShape, List<DisplaySlot> slots) {
        super(properties, northShape, slots);
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.is(FurnitureTags.PLANTS);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.isEmpty() && !accepts(stack)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}
