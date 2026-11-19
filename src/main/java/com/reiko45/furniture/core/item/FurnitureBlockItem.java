package com.reiko45.furniture.core.item;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.repaint.Repaint;
import com.reiko45.furniture.core.stain.Stains;
import java.util.List;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/** Block item that shows which room section the furniture belongs to (if any), and its style. */
public class FurnitureBlockItem extends BlockItem {
    private final Room room;
    @Nullable
    private final Style style;

    public FurnitureBlockItem(Block block, Properties properties, @Nullable Room room) {
        this(block, properties, room, null);
    }

    public FurnitureBlockItem(Block block, Properties properties, @Nullable Room room, @Nullable Style style) {
        super(block, properties);
        this.room = room;
        this.style = style;
    }

    /** The furniture style, or null for classic pieces. */
    @Nullable
    public Style style() {
        return style;
    }

    @Nullable
    public Room room() {
        return room;
    }

    /** A stained item stains the piece it places; an unstained one clears any stain left at that spot. */
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        DyeColor stain = context.getItemInHand().get(ModDataComponents.STAIN.get());
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !context.getLevel().isClientSide && Repaint.isWooden(getBlock())) {
            Stains.setPiece(context.getLevel(), context.getClickedPos(), stain);
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (room != null) {
            tooltip.add(Component.translatable(room.translationKey()).withStyle(ChatFormatting.GRAY));
        }
        if (style != null) {
            tooltip.add(Component.translatable(style.translationKey()).withStyle(ChatFormatting.GOLD));
        }
        DyeColor stain = stack.get(ModDataComponents.STAIN.get());
        if (stain != null) {
            tooltip.add(Component.translatable("item.reikos_furniture.stain",
                    Component.translatable("color.minecraft." + stain.getName())).withStyle(ChatFormatting.GRAY));
        }
    }
}
