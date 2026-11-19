package com.reiko45.furniture.core;

import org.jetbrains.annotations.Nullable;

/**
 * A furniture style. Each style has its own look for beds, bunk beds, chairs, tables, storage, sofas
 * and armchairs. The pieces use the same blocks as the classic ones, with their own shapes and models;
 * ids are {@code <wood>_<style>_<piece>} and {@code <color>_<style>_<piece>}.
 */
public enum Style {
    RUSTIC("rustic", "Rustic"),
    MODERN("modern", "Modern"),
    ROYAL("royal", "Royal"),
    JAPANESE("japanese", "Japanese"),
    MEDIEVAL("medieval", "Medieval"),
    VICTORIAN("victorian", "Victorian"),
    FUTURISTIC("futuristic", "Futuristic"),
    BEACH("beach", "Beach House");

    private final String id;
    private final String displayName;

    Style(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    /** {@code bed} becomes {@code rustic_bed}. */
    public String piece(String piece) {
        return id + "_" + piece;
    }

    public String translationKey() {
        return "style.reikos_furniture." + id;
    }

    /** The style a piece name ({@code rustic_bed}) belongs to, or null for a classic piece. */
    @Nullable
    public static Style of(String piece) {
        for (Style style : values()) {
            if (piece.startsWith(style.id + "_")) {
                return style;
            }
        }
        return null;
    }
}
