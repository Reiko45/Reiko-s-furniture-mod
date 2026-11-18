package com.reiko45.furniture.outdoor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Draws the food lying on the grill, the same way the vanilla campfire does. Client only. */
public class GrillRenderer implements BlockEntityRenderer<GrillBlockEntity> {
    private final ItemRenderer itemRenderer;

    public GrillRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(GrillBlockEntity grill, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        Direction facing = grill.getBlockState().getValue(FurnitureBlock.FACING);
        NonNullList<ItemStack> items = grill.getItems();
        int seed = (int) grill.getBlockPos().asLong();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            pose.pushPose();
            pose.translate(0.5F, 0.6875F, 0.5F);
            Direction spot = Direction.from2DDataValue((i + facing.get2DDataValue()) % 4);
            pose.mulPose(Axis.YP.rotationDegrees(-spot.toYRot()));
            pose.mulPose(Axis.XP.rotationDegrees(90.0F));
            pose.translate(-0.2F, -0.2F, 0.0F);
            pose.scale(0.32F, 0.32F, 0.32F);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, buffers, grill.getLevel(), seed + i);
            pose.popPose();
        }
    }
}
