package com.reiko45.furniture.studio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

/**
 * What is painted on a canvas: {@link #SIZE} by {@link #SIZE} pixels, row by row from the top left, each
 * one character: {@code 0}-{@code f} for the 16 dye colors (by {@link DyeColor#getId()}) or {@code .} for bare
 * canvas. A signed painting has a title and an author and can't be painted on any more.
 */
public record Painting(String pixels, String title, String author) {
    public static final int SIZE = 16;
    public static final int MAX_TITLE = 32;
    public static final char BARE = '.';
    /** The color of bare canvas, ARGB. */
    public static final int CANVAS_COLOR = 0xFFF2EBD9;
    private static final String DIGITS = "0123456789abcdef";

    public static final Painting BLANK = new Painting(String.valueOf(BARE).repeat(SIZE * SIZE), "", "");

    public static final Codec<Painting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.validate(Painting::check).fieldOf("pixels").forGetter(Painting::pixels),
            Codec.STRING.optionalFieldOf("title", "").forGetter(Painting::title),
            Codec.STRING.optionalFieldOf("author", "").forGetter(Painting::author)).apply(instance, Painting::new));

    public static final StreamCodec<ByteBuf, Painting> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(SIZE * SIZE), Painting::pixels,
            ByteBufCodecs.stringUtf8(MAX_TITLE * 4), Painting::title,
            ByteBufCodecs.stringUtf8(64), Painting::author,
            Painting::new);

    /** Whether a string is a valid set of pixels. */
    public static boolean valid(String pixels) {
        if (pixels.length() != SIZE * SIZE) {
            return false;
        }
        for (int i = 0; i < pixels.length(); i++) {
            char c = pixels.charAt(i);
            if (c != BARE && DIGITS.indexOf(c) < 0) {
                return false;
            }
        }
        return true;
    }

    private static DataResult<String> check(String pixels) {
        return valid(pixels) ? DataResult.success(pixels) : DataResult.error(() -> "not a painting: " + pixels);
    }

    public boolean signed() {
        return !author.isEmpty();
    }

    public boolean isBlank() {
        return pixels.equals(BLANK.pixels);
    }

    public Painting withPixels(String pixels) {
        return new Painting(pixels, title, author);
    }

    public Painting signedBy(String title, String author) {
        return new Painting(pixels, title, author);
    }

    /** The character for a dye color. */
    public static char of(DyeColor color) {
        return DIGITS.charAt(color.getId());
    }

    /** The ARGB color of one pixel character. */
    public static int color(char pixel) {
        int index = DIGITS.indexOf(pixel);
        return index < 0 ? CANVAS_COLOR : DyeColor.byId(index).getTextureDiffuseColor();
    }
}
