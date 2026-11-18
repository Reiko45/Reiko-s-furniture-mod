package com.reiko45.furniture.core.item;

import com.reiko45.furniture.core.Room;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/** Block item that shows which room section the furniture belongs to (if any). */
public class FurnitureBlockItem extends BlockItem {
    private final Room room;

    public FurnitureBlockItem(Block block, Properties properties, @Nullable Room room) {
        super(block, properties);
        this.room = room;
    }

    @Nullable
    public Room room() {
        return room;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (room != null) {
            tooltip.add(Component.translatable(room.translationKey()).withStyle(ChatFormatting.GRAY));
        }
    }
}
