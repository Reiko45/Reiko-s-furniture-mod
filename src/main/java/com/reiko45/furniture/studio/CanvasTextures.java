package com.reiko45.furniture.studio;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.reiko45.furniture.ReikosFurniture;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/**
 * Client only: a 16x16 texture for each painting being shown (kept for the most recent ones), and drawing
 * a painting on a square.
 */
public final class CanvasTextures {
    private static final int KEEP = 256;
    private static int next;
    private static final Map<String, ResourceLocation> TEXTURES = new LinkedHashMap<>(64, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ResourceLocation> eldest) {
            if (size() > KEEP) {
                Minecraft.getInstance().getTextureManager().release(eldest.getValue());
                return true;
            }
            return false;
        }
    };

    private CanvasTextures() {
    }

    /** The texture for a painting's pixels, made the first time it is needed. */
    public static ResourceLocation texture(String pixels) {
        ResourceLocation location = TEXTURES.get(pixels);
        if (location == null) {
            DynamicTexture texture = new DynamicTexture(Painting.SIZE, Painting.SIZE, false);
            NativeImage image = texture.getPixels();
            for (int y = 0; y < Painting.SIZE; y++) {
                for (int x = 0; x < Painting.SIZE; x++) {
                    int argb = Painting.color(pixels.charAt(y * Painting.SIZE + x));
                    // NativeImage takes ABGR.
                    int abgr = (argb & 0xFF00FF00) | (argb >> 16 & 0xFF) | (argb & 0xFF) << 16;
                    image.setPixelRGBA(x, y, abgr);
                }
            }
            texture.upload();
            location = ReikosFurniture.id("canvas/" + next++);
            Minecraft.getInstance().getTextureManager().register(location, texture);
            TEXTURES.put(pixels, location);
        }
        return location;
    }

    /**
     * Draws a painting on the square from (0, 0) to (1, 1) at z = 0, facing +z with its top-left pixel at
     * (0, 1), and bare canvas on the back.
     */
    public static void draw(Painting painting, PoseStack pose, MultiBufferSource buffers, int light) {
        Matrix4f matrix = pose.last().pose();
        VertexConsumer front = buffers.getBuffer(RenderType.text(texture(painting.pixels())));
        vertex(front, matrix, 0, 0, 0, 0, 1, light);
        vertex(front, matrix, 1, 0, 0, 1, 1, light);
        vertex(front, matrix, 1, 1, 0, 1, 0, light);
        vertex(front, matrix, 0, 1, 0, 0, 0, light);
        VertexConsumer back = buffers.getBuffer(RenderType.text(texture(Painting.BLANK.pixels())));
        vertex(back, matrix, 0, 1, -0.002F, 0, 0, light);
        vertex(back, matrix, 1, 1, -0.002F, 1, 0, light);
        vertex(back, matrix, 1, 0, -0.002F, 1, 1, light);
        vertex(back, matrix, 0, 0, -0.002F, 0, 1, light);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(matrix, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setLight(light);
    }
}
