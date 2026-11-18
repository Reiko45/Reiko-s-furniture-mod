package com.reiko45.furniture.core.display;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.reiko45.furniture.core.block.FurnitureBlock;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Draws the items on display blocks at the block's slot positions. Client only. */
public class DisplayRenderer implements BlockEntityRenderer<DisplayBlockEntity> {
    private final ItemRenderer itemRenderer;

    public DisplayRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DisplayBlockEntity display, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        if (!(display.getBlockState().getBlock() instanceof DisplayFurnitureBlock block)) {
            return;
        }
        Direction facing = display.getBlockState().getValue(FurnitureBlock.FACING);
        List<DisplaySlot> slots = block.slots();
        int seed = (int) display.getBlockPos().asLong();
        for (int i = 0; i < slots.size() && i < display.getItems().size(); i++) {
            ItemStack stack = display.getItems().get(i);
            if (stack.isEmpty()) {
                continue;
            }
            DisplaySlot slot = slots.get(i);
            pose.pushPose();
            // Rotate around the block centre so the slot positions follow the block's facing.
            pose.translate(0.5F, 0.0F, 0.5F);
            pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180) % 360)));
            pose.translate(slot.x() / 16F - 0.5F, slot.y() / 16F, slot.z() / 16F - 0.5F);
            if (slot.flat()) {
                pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                pose.translate(0.0F, 0.0F, -0.02F);
            } else {
                pose.translate(0.0F, slot.scale() / 2F, 0.0F);
                pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            }
            pose.scale(slot.scale(), slot.scale(), slot.scale());
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, buffers, display.getLevel(), seed + i);
            pose.popPose();
        }
    }
}
