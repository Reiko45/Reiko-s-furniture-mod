package com.reiko45.furniture.nursery;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** Draws the aquarium's fish swimming slow loops in the tank. Client only. */
public class AquariumRenderer implements BlockEntityRenderer<AquariumBlockEntity> {
    public AquariumRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AquariumBlockEntity tank, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        Level level = tank.getLevel();
        if (level == null) {
            return;
        }
        long time = level.getGameTime();
        for (int i = 0; i < AquariumBlockEntity.MAX_FISH; i++) {
            ItemStack bucket = tank.fish().get(i);
            if (bucket.isEmpty()) {
                tank.drawn[i] = null;
                continue;
            }
            Entity fish = tank.drawn[i];
            EntityType<?> type = AquariumBlockEntity.FISH.get(bucket.getItem());
            if (fish == null || fish.getType() != type) {
                fish = type.create(level);
                if (fish == null) {
                    continue;
                }
                if (fish instanceof Bucketable bucketable) {
                    bucketable.loadFromBucketTag(bucket.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag());
                }
                tank.drawn[i] = fish;
            }
            fish.wasTouchingWater = true; // swimming pose, not flopping on its side
            fish.tickCount = (int) time;
            // Each fish swims its own slow oval at its own depth.
            float t = (time + partialTick) / 35F + i * 2.1F;
            double x = 0.5 + 0.27 * Math.sin(t);
            double z = 0.5 + 0.2 * Math.cos(t * 0.9F + i);
            double y = 0.22 + i * 0.13 + 0.03 * Math.sin(t * 2);
            double dx = 0.27 * Math.cos(t);
            double dz = -0.18 * Math.sin(t * 0.9F + i);
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            fish.setYRot(yaw);
            fish.yRotO = yaw;
            if (fish instanceof LivingEntity living) {
                living.yBodyRot = yaw;
                living.yBodyRotO = yaw;
                living.yHeadRot = yaw;
                living.yHeadRotO = yaw;
            }
            pose.pushPose();
            pose.translate(x, y, z);
            pose.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getEntityRenderDispatcher().render(fish, 0, 0, 0, yaw, partialTick, pose, buffers, light);
            pose.popPose();
        }
    }
}
