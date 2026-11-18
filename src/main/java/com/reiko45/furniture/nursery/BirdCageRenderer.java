package com.reiko45.furniture.nursery;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

/** Draws the parrot sitting on the cage's perch. Client only. */
public class BirdCageRenderer implements BlockEntityRenderer<BirdCageBlockEntity> {
    public BirdCageRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BirdCageBlockEntity cage, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (!cage.hasBird() || cage.getLevel() == null) {
            return;
        }
        if (cage.drawn == null) {
            cage.drawn = EntityType.create(cage.bird(), cage.getLevel()).orElse(null);
            if (cage.drawn == null) {
                return;
            }
        }
        Entity bird = cage.drawn;
        bird.setOnGround(true);
        if (bird instanceof TamableAnimal pet) {
            pet.setInSittingPose(true);
        }
        float yaw = cage.getBlockState().getValue(FurnitureBlock.FACING).toYRot();
        bird.setYRot(yaw);
        bird.yRotO = yaw;
        if (bird instanceof LivingEntity living) {
            living.yBodyRot = yaw;
            living.yBodyRotO = yaw;
            living.yHeadRot = yaw;
            living.yHeadRotO = yaw;
        }
        bird.tickCount = (int) cage.getLevel().getGameTime();
        pose.pushPose();
        pose.translate(0.5, 4.5 / 16, 0.5);
        pose.scale(0.45F, 0.45F, 0.45F); // a parrot is almost a block tall; it has to fit under the dome
        Minecraft.getInstance().getEntityRenderDispatcher().render(bird, 0, 0, 0, yaw, partialTick, pose, buffers, light);
        pose.popPose();
    }
}
