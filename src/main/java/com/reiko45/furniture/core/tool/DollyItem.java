package com.reiko45.furniture.core.tool;

import com.reiko45.furniture.core.stain.Stains;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.config.FurnitureConfig;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.registry.ModDataComponents.CarriedFurniture;
import com.reiko45.furniture.office.SafeBlockEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

/**
 * Moving dolly: right-click storage furniture to pick it up with everything inside, then right-click
 * where it should go to put it down, facing you. It carries one piece at a time.
 */
public class DollyItem extends Item {
    public DollyItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /** Why a piece couldn't be picked up or put down, or null when it worked. */
    public enum Problem {
        NOT_STORAGE("dolly_not_storage"), NOT_YOURS("dolly_not_yours"), NOT_EMPTY("dolly_not_empty"), NO_ROOM("dolly_no_room");

        private final String message;

        Problem(String message) {
            this.message = message;
        }

        public Component message() {
            return Component.translatable("message.reikos_furniture." + message);
        }
    }

    /** Runs before the block's own right-click action, so clicking a cabinet picks it up instead of opening it. */
    @Override
    public InteractionResult onItemUseFirst(ItemStack dolly, UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide) {
            boolean applies = dolly.has(ModDataComponents.CARRIED_FURNITURE.get())
                    || level.getBlockState(context.getClickedPos()).getBlock() instanceof FurnitureStorageBlock;
            return applies ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        Problem problem;
        if (dolly.has(ModDataComponents.CARRIED_FURNITURE.get())) {
            BlockPlaceContext place = new BlockPlaceContext(context);
            problem = putDown(level, place.getClickedPos(), place.getHorizontalDirection().getOpposite(), dolly);
        } else {
            problem = pickUp(level, context.getClickedPos(), player, dolly);
        }
        if (problem != null) {
            if (player != null) {
                player.displayClientMessage(problem.message(), true);
            }
            return problem == Problem.NOT_STORAGE ? InteractionResult.PASS : InteractionResult.FAIL;
        }
        if (player != null) {
            Tools.trigger(player, "dolly");
        }
        return InteractionResult.CONSUME;
    }

    /** Picks up the storage piece at {@code pos} (either half of a tall one) onto the dolly. */
    @Nullable
    public static Problem pickUp(Level level, BlockPos pos, @Nullable Player player, ItemStack dolly) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof FurnitureStorageBlock storage)) {
            return Problem.NOT_STORAGE;
        }
        BlockPos lowerPos = storage.storagePos(state, pos);
        BlockState lower = level.getBlockState(lowerPos);
        if (!(level.getBlockEntity(lowerPos) instanceof FurnitureStorageBlockEntity inventory)) {
            return Problem.NOT_STORAGE;
        }
        if (inventory instanceof SafeBlockEntity safe && (player == null || !safe.canAccess(player))) {
            return Problem.NOT_YOURS;
        }
        if (!inventory.isEmpty() && !FurnitureConfig.dollyCarriesFullStorage()) {
            return Problem.NOT_EMPTY;
        }
        CompoundTag data = inventory.saveWithoutMetadata(level.registryAccess());
        inventory.clearContent(); // so nothing spills when the block is removed
        BlockState carried = lower.hasProperty(BlockStateProperties.OPEN) ? lower.setValue(BlockStateProperties.OPEN, false) : lower;
        dolly.set(ModDataComponents.CARRIED_FURNITURE.get(), new CarriedFurniture(carried, data));
        // The stain goes along on the dolly.
        DyeColor stain = Stains.getPiece(level, lowerPos);
        if (stain != null) {
            dolly.set(ModDataComponents.STAIN.get(), stain);
        }
        Stains.setPiece(level, lowerPos, null);
        if (lower.hasProperty(TallBlockHelper.HALF)) {
            level.setBlock(lowerPos.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
        level.setBlock(lowerPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, lowerPos, SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS, 1.0F, 0.8F);
        return null;
    }

    /** Puts the carried piece down at {@code pos}, facing {@code facing}, with its contents. */
    @Nullable
    public static Problem putDown(Level level, BlockPos pos, Direction facing, ItemStack dolly) {
        CarriedFurniture carried = dolly.get(ModDataComponents.CARRIED_FURNITURE.get());
        if (carried == null) {
            return Problem.NOT_STORAGE;
        }
        BlockState state = carried.state();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        boolean tall = state.hasProperty(TallBlockHelper.HALF);
        if (!level.getBlockState(pos).canBeReplaced() || (tall && !level.getBlockState(pos.above()).canBeReplaced())
                || !state.canSurvive(level, pos)) {
            return Problem.NO_ROOM;
        }
        if (!tall) {
            // Corner counters and similar pieces join their new neighbors. (A tall piece would see its
            // missing upper half here, so it's placed as is.)
            state = Block.updateFromNeighbourShapes(state, level, pos);
        }
        level.setBlock(pos, state, Block.UPDATE_ALL);
        if (tall) {
            level.setBlock(pos.above(), state.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) {
            entity.loadWithComponents(carried.data(), level.registryAccess());
            entity.setChanged();
        }
        dolly.remove(ModDataComponents.CARRIED_FURNITURE.get());
        Stains.setPiece(level, pos, dolly.remove(ModDataComponents.STAIN.get()));
        level.playSound(null, pos, state.getSoundType(level, pos, null).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.9F);
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CarriedFurniture carried = stack.get(ModDataComponents.CARRIED_FURNITURE.get());
        if (carried == null) {
            tooltip.add(Component.translatable("item.reikos_furniture.moving_dolly.tooltip").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.reikos_furniture.moving_dolly.carrying", carried.state().getBlock().getName())
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
