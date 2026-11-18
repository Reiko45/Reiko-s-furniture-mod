package com.reiko45.furniture.livingroom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Draws the vanilla clock on the clock face; its dial follows the time of day. Client only. */
public class WallClockRenderer implements BlockEntityRenderer<WallClockBlockEntity> {
    private static final ItemStack CLOCK = new ItemStack(Items.CLOCK);
    private final ItemRenderer itemRenderer;

    public WallClockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(WallClockBlockEntity clock, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        Direction facing = clock.getBlockState().getValue(FurnitureBlock.FACING);
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180) % 360)));
        pose.translate(0.0F, 0.0F, 14.6F / 16F - 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        pose.scale(0.5F, 0.5F, 0.5F);
        itemRenderer.renderStatic(CLOCK, ItemDisplayContext.FIXED, light, overlay, pose, buffers, clock.getLevel(),
                (int) clock.getBlockPos().asLong());
        pose.popPose();
    }
}
