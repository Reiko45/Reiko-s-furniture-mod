package com.reiko45.furniture.studio;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Client only: the canvas item shows its painting, in the inventory, in hand and in item frames. */
public class CanvasItemRenderer extends BlockEntityWithoutLevelRenderer {
    public CanvasItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffers,
                             int light, int overlay) {
        pose.pushPose();
        // Like a flat item: centred in the 16 pixel cube.
        pose.translate(1F / 16, 1F / 16, 0.5F);
        pose.scale(14F / 16, 14F / 16, 1.0F);
        CanvasTextures.draw(CanvasItem.painting(stack), pose, buffers, light);
        pose.popPose();
    }
}
