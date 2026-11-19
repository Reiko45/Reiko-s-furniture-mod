package com.reiko45.furniture.studio;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * A canvas. Put it on an easel to paint it; place it on a wall (or in a picture frame) to show it. A
 * signed canvas is named after its title.
 */
public class CanvasItem extends FurnitureBlockItem {
    public CanvasItem(Block block, Properties properties, @Nullable Room room) {
        super(block, properties, room);
    }

    public static Painting painting(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.PAINTING.get(), Painting.BLANK);
    }

    @Override
    public Component getName(ItemStack stack) {
        Painting painting = painting(stack);
        return painting.signed() ? Component.literal(painting.title()) : super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        Painting painting = painting(stack);
        if (painting.signed()) {
            tooltip.add(Component.translatable("item.reikos_furniture.canvas.by", painting.author()).withStyle(ChatFormatting.GRAY));
        } else if (!painting.isBlank()) {
            tooltip.add(Component.translatable("item.reikos_furniture.canvas.unfinished").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.reikos_furniture.canvas.tooltip").withStyle(ChatFormatting.GRAY));
        }
    }
}
