package com.reiko45.furniture.core.tool;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bathroom.BathtubBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

/**
 * Turns placed furniture: right-click to turn it clockwise, sneak and right-click for the other way.
 * Two-block-tall pieces turn as a whole. Pieces made of two side-by-side blocks (beds, bathtubs,
 * doors) can't be turned this way.
 */
public class HammerItem extends Item {
    public HammerItem(Properties properties) {
        super(properties);
    }

    /** What happened when trying to turn a block. */
    public enum Result { TURNED, NOT_FURNITURE, CANNOT_TURN }

    /** Runs before the block's own right-click action, so clicking a cabinet turns it instead of opening it. */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        Result result = rotate(level, context.getClickedPos(), player == null || !player.isShiftKeyDown());
        if (result == Result.NOT_FURNITURE) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && player != null) {
            if (result == Result.CANNOT_TURN) {
                player.displayClientMessage(Component.translatable("message.reikos_furniture.hammer_cannot_turn"), true);
            } else {
                context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                Tools.trigger(player, "hammer");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Turns the furniture at {@code pos} a quarter turn. Works on both halves of tall pieces. */
    public static Result rotate(Level level, BlockPos pos, boolean clockwise) {
        BlockState state = level.getBlockState(pos);
        if (!BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(ReikosFurniture.MODID)
                || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return Result.NOT_FURNITURE;
        }
        if (state.getBlock() instanceof BedBlock || state.getBlock() instanceof DoorBlock || state.getBlock() instanceof BathtubBlock) {
            return Result.CANNOT_TURN;
        }
        if (level.isClientSide) {
            return Result.TURNED;
        }
        Rotation rotation = clockwise ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
        @Nullable BlockPos otherPos = state.hasProperty(TallBlockHelper.HALF) ? TallBlockHelper.otherHalf(state, pos) : null;
        turn(level, pos, state, rotation);
        if (otherPos != null && level.getBlockState(otherPos).is(state.getBlock())) {
            turn(level, otherPos, level.getBlockState(otherPos), rotation);
        }
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F);
        return Result.TURNED;
    }

    private static void turn(Level level, BlockPos pos, BlockState state, Rotation rotation) {
        BlockState turned = Block.updateFromNeighbourShapes(state.rotate(level, pos, rotation), level, pos);
        // UPDATE_KNOWN_SHAPE: the other half of a tall piece is turned next, so don't let it react yet.
        level.setBlock(pos, turned, Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        level.updateNeighborsAt(pos, turned.getBlock());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.reikos_furniture.hammer.tooltip").withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}
