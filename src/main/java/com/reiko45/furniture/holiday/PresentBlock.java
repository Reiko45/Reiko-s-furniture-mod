package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A wrapped present with one row of storage. Like a shulker box, it keeps what is inside when broken, so
 * it can be given away; the tooltip only says that something is in it.
 */
public class PresentBlock extends FurnitureStorageBlock {
    public PresentBlock(Properties properties, VoxelShape northShape) {
        super(properties, 1, northShape);
    }

    @Override
    protected boolean dropsContents() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (contents.nonEmptyStream().findAny().isPresent()) {
            tooltip.add(Component.translatable("block.reikos_furniture.present.full").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
