package com.reiko45.furniture.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.MapColor;

/** The stones that stone furniture is carved from, with the block it is made of and its textures. */
public enum StoneMaterial {
    WHITE_MARBLE("white_marble", MapColor.SNOW, "reikos_furniture:block/marble_white", "reikos_furniture:block/marble_white"),
    BLACK_MARBLE("black_marble", MapColor.COLOR_BLACK, "reikos_furniture:block/marble_black", "reikos_furniture:block/marble_black"),
    QUARTZ("quartz", MapColor.QUARTZ, "minecraft:block/quartz_block_side", "minecraft:block/quartz_block_top"),
    BLACKSTONE("blackstone", MapColor.COLOR_BLACK, "minecraft:block/polished_blackstone", "minecraft:block/polished_blackstone"),
    STONE_BRICK("stone_brick", MapColor.STONE, "minecraft:block/stone_bricks", "minecraft:block/smooth_stone");

    private final String id;
    private final MapColor mapColor;
    private final String side;
    private final String top;

    StoneMaterial(String id, MapColor mapColor, String side, String top) {
        this.id = id;
        this.mapColor = mapColor;
        this.side = side;
        this.top = top;
    }

    public String id() {
        return id;
    }

    public MapColor mapColor() {
        return mapColor;
    }

    /** Texture for the sides of a piece. */
    public String side() {
        return side;
    }

    /** Texture for table tops and counter tops. */
    public String top() {
        return top;
    }

    /** The block the furniture is crafted from. */
    public Item block() {
        return switch (this) {
            case WHITE_MARBLE -> Items.CALCITE;
            case BLACK_MARBLE -> Items.POLISHED_DEEPSLATE;
            case QUARTZ -> Items.QUARTZ_BLOCK;
            case BLACKSTONE -> Items.POLISHED_BLACKSTONE;
            case STONE_BRICK -> Items.STONE_BRICKS;
        };
    }
}
