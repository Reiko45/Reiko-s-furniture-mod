package com.reiko45.furniture.studio;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

/** Client only: draws the painting on a canvas hung on a wall. */
public class CanvasRenderer implements BlockEntityRenderer<CanvasBlockEntity> {
    public CanvasRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CanvasBlockEntity canvas, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        Direction facing = canvas.getBlockState().getValue(FurnitureBlock.FACING);
        pose.pushPose();
        facingNorth(pose, facing);
        // The painting covers the canvas inside its 1-pixel stretcher, just in front of it.
        onNorthFace(pose, 15F / 16, 1F / 16, 14.95F / 16, 14F / 16);
        CanvasTextures.draw(canvas.painting(), pose, buffers, light);
        pose.popPose();
    }

    /** Turns the pose so that a block facing {@code facing} can be drawn as if it faced north. */
    public static void facingNorth(PoseStack pose, Direction facing) {
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180) % 360)));
        pose.translate(-0.5F, 0.0F, -0.5F);
    }

    /**
     * Sets up a square of {@code size} facing north (out of the front of a north-facing block), whose
     * bottom corner on the viewer's left is at ({@code x}, {@code y}, {@code z}); then
     * {@link CanvasTextures#draw} draws on it.
     */
    public static void onNorthFace(PoseStack pose, float x, float y, float z, float size) {
        pose.translate(x, y, z);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        pose.scale(size, size, size);
    }
}
