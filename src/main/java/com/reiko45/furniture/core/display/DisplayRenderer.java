package com.reiko45.furniture.core.display;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.reiko45.furniture.core.block.FurnitureBlock;
import java.util.List;
import com.reiko45.furniture.decor.PictureFrameBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

/** Draws the items on display blocks at the block's slot positions. Client only. */
public class DisplayRenderer implements BlockEntityRenderer<DisplayBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public DisplayRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    /** Plants in pots grow above the block. */
    @Override
    public AABB getRenderBoundingBox(DisplayBlockEntity display) {
        return new AABB(display.getBlockPos()).inflate(0.5, 0.0, 0.5).expandTowards(0.0, 1.0, 0.0);
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
            if (block instanceof PictureFrameBlock && stack.has(DataComponents.MAP_ID)) {
                renderMap(display, stack.get(DataComponents.MAP_ID), facing, pose, buffers, light);
                continue;
            }
            if (slot.plant() && stack.getItem() instanceof BlockItem plant) {
                renderPlant(plant.getBlock().defaultBlockState(), slot, block.plantScale(display.getBlockState()), facing,
                        pose, buffers, light, overlay);
                continue;
            }
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

    /** A flower or sapling standing in the soil at the slot, drawn as the block it plants (both halves of tall flowers). */
    private void renderPlant(BlockState plant, DisplaySlot slot, float grown, Direction facing, PoseStack pose,
                             MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180) % 360)));
        pose.translate(slot.x() / 16F - 0.5F, slot.y() / 16F, slot.z() / 16F - 0.5F);
        boolean tall = plant.getBlock() instanceof DoublePlantBlock && plant.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
        // A tall flower is drawn at half size, so it stands as high as a small flower would.
        float scale = slot.scale() * grown * (tall ? 0.5F : 1.0F);
        pose.scale(scale, scale, scale);
        pose.translate(-0.5F, 0.0F, -0.5F);
        blockRenderer.renderSingleBlock(plant, pose, buffers, light, overlay, ModelData.EMPTY, null);
        if (tall) {
            pose.translate(0.0F, 1.0F, 0.0F);
            blockRenderer.renderSingleBlock(plant.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER),
                    pose, buffers, light, overlay, ModelData.EMPTY, null);
        }
        pose.popPose();
    }

    /** A map fills the frame's picture area, like a map in an item frame. */
    private static void renderMap(DisplayBlockEntity display, MapId id, Direction facing, PoseStack pose, MultiBufferSource buffers, int light) {
        MapItemSavedData data = MapItem.getSavedData(id, display.getLevel());
        if (data == null) {
            return;
        }
        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180) % 360)));
        // Just in front of the frame's backing, centred in the frame, facing out of the wall.
        pose.translate(0.0F, 0.5F, 14.45F / 16F - 0.5F);
        // Same as an item frame on a north wall: the map's front faces north, out of the wall.
        pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float scale = PictureFrameBlock.PICTURE_SIZE / 16F / 128F;
        pose.scale(scale, scale, scale);
        pose.translate(-64.0F, -64.0F, 0.0F);
        Minecraft.getInstance().gameRenderer.getMapRenderer().render(pose, buffers, id, data, true, light);
        pose.popPose();
    }
}
