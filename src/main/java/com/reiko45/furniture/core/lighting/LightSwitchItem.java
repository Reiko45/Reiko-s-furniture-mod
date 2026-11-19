package com.reiko45.furniture.core.lighting;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** The light switch item: sneak and right-click lamps to link or unlink them, then place the switch. */
public class LightSwitchItem extends FurnitureBlockItem {
    public LightSwitchItem(Block block, Properties properties, @Nullable Room room) {
        super(block, properties, room);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (player != null && player.isSecondaryUseActive() && Lighting.isLamp(state)) {
            if (!level.isClientSide) {
                link(context.getItemInHand(), Lighting.lampPos(state, context.getClickedPos()), player);
                level.playSound(null, context.getClickedPos(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.4F, 1.5F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    /** Adds the lamp to the switch's list, or takes it off if it is already linked. */
    public static void link(ItemStack stack, BlockPos lamp, @Nullable Player player) {
        List<BlockPos> lamps = new ArrayList<>(stack.getOrDefault(ModDataComponents.LINKED_LAMPS.get(), List.of()));
        String message;
        if (lamps.remove(lamp)) {
            message = "message.reikos_furniture.lamp_unlinked";
        } else if (lamps.size() >= Lighting.MAX_LAMPS) {
            message = "message.reikos_furniture.switch_full";
        } else {
            lamps.add(lamp.immutable());
            message = "message.reikos_furniture.lamp_linked";
        }
        if (lamps.isEmpty()) {
            stack.remove(ModDataComponents.LINKED_LAMPS.get());
        } else {
            stack.set(ModDataComponents.LINKED_LAMPS.get(), List.copyOf(lamps));
        }
        if (player != null) {
            player.displayClientMessage(Component.translatable(message, lamps.size(), Lighting.MAX_LAMPS), true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int linked = stack.getOrDefault(ModDataComponents.LINKED_LAMPS.get(), List.of()).size();
        tooltip.add(Component.translatable("item.reikos_furniture.light_switch.tooltip", linked).withStyle(ChatFormatting.GRAY));
    }
}
