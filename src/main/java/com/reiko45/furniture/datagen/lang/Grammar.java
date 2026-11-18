package com.reiko45.furniture.datagen.lang;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

/**
 * One language: its words for the furniture pieces (with their grammatical gender), woods, colors and
 * styles, how those combine into a name, and every other text of the mod. The wood and color words are
 * taken from Minecraft's own translations (doors, buttons, beds, banners, carpets and wool), so they
 * match the game.
 */
public abstract class Grammar {
    public enum Gender { M, F, N }

    public record Noun(String text, Gender gender) {
    }

    private final Map<String, Noun> nouns = new HashMap<>();
    private final Map<String, String> singles = new HashMap<>();
    private final Map<String, String> texts = new HashMap<>();

    /** The language code, e.g. {@code es_es}. */
    public abstract String locale();

    /** The word for oak as it appears in names, so modded woods can swap it for their own name. */
    public abstract String oakWord();

    /** The name of a piece made of a wood, in a color, in a style (each may be missing). */
    public abstract String name(Noun noun, @Nullable FurnitureWood wood, @Nullable DyeColor color, @Nullable Style style);

    protected void noun(String piece, String text, Gender gender) {
        nouns.put(piece, new Noun(text, gender));
    }

    /** The name of a block that has no wood or color variants, by its id (e.g. {@code coffee_machine}). */
    protected void single(String block, String text) {
        singles.put(block, text);
    }

    /** Any other text, by its translation key. */
    protected void text(String key, String text) {
        texts.put(key, text);
    }

    public Noun noun(String piece) {
        Noun noun = nouns.get(piece);
        if (noun == null) {
            throw new IllegalStateException(locale() + " has no word for the piece " + piece);
        }
        return noun;
    }

    @Nullable
    public String single(String block) {
        return singles.get(block);
    }

    @Nullable
    public String text(String key) {
        return texts.get(key);
    }

    /** Index of a wood among the vanilla woods, for the word arrays below. */
    protected static int index(FurnitureWood wood) {
        FurnitureWood[] woods = FurnitureWood.values();
        for (int i = 0; i < woods.length; i++) {
            if (woods[i] == wood) {
                return i;
            }
        }
        throw new IllegalArgumentException("not a vanilla wood: " + wood.id());
    }

    /** "Rustikales" becomes "rustikales" (German adjectives after the first). */
    protected static String lowerFirst(String text) {
        return text.isEmpty() ? text : Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }
}
