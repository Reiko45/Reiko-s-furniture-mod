package com.reiko45.furniture.decor;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.WallTrimBlock;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Decor section: building blocks for walls and floors (wallpaper, tiles, parquet), wall trims, doors and windows. */
public final class DecorFurniture {
    private static final Room ROOM = Room.DECOR;

    /** Patterned wallpaper on a full block. */
    public static final ColorSet<Block> WALLPAPER = FurnitureRegistry.colorSet("wallpaper", ROOM,
            color -> new Block(BlockBehaviour.Properties.of().mapColor(color.getMapColor()).strength(1.0F).sound(SoundType.WOOD)
                    .ignitedByLava()));

    /** The tile patterns. */
    public enum Tiles {
        WHITE(MapColor.SNOW), BLACK(MapColor.COLOR_BLACK), CHECKERED(MapColor.COLOR_GRAY), BLUE(MapColor.COLOR_BLUE),
        GREEN(MapColor.COLOR_GREEN), TERRACOTTA(MapColor.TERRACOTTA_ORANGE), MARBLE(MapColor.QUARTZ), HEXAGON(MapColor.SNOW);

        private final MapColor mapColor;

        Tiles(MapColor mapColor) {
            this.mapColor = mapColor;
        }

        public String id() {
            return name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    /** Ceramic tiles for kitchens and bathrooms: {@code <pattern>_tiles}. */
    public static final Map<Tiles, DeferredBlock<Block>> TILES;

    static {
        Map<Tiles, DeferredBlock<Block>> tiles = new EnumMap<>(Tiles.class);
        for (Tiles pattern : Tiles.values()) {
            tiles.put(pattern, FurnitureRegistry.register(pattern.id() + "_tiles", ROOM,
                    () -> new Block(BlockBehaviour.Properties.of().mapColor(pattern.mapColor).strength(1.5F, 6.0F)
                            .sound(SoundType.STONE).requiresCorrectToolForDrops())));
        }
        TILES = Collections.unmodifiableMap(tiles);
    }

    /** Parquet floor: the wood's planks laid in a basket weave. */
    public static final WoodSet<Block> PARQUET = FurnitureRegistry.woodSet("parquet", ROOM,
            wood -> new Block(BlockBehaviour.Properties.ofFullCopy(wood.planks())));

    public static final WoodSet<SlabBlock> PARQUET_SLAB = FurnitureRegistry.woodSet("parquet_slab", ROOM,
            wood -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(wood.slab())));

    /** Strip along the foot of a wall; turns corners. */
    public static final WoodSet<WallTrimBlock> BASEBOARD = FurnitureRegistry.woodSet("baseboard", ROOM,
            wood -> new WallTrimBlock(wood.properties(), false));

    /** Strip along the top of a wall; turns corners. */
    public static final WoodSet<WallTrimBlock> CROWN_MOLDING = FurnitureRegistry.woodSet("crown_molding", ROOM,
            wood -> new WallTrimBlock(wood.properties(), true));

    /** Wood-and-glass door with a handle; works like a vanilla door (hand, redstone, villagers). */
    public static final WoodSet<DoorBlock> FRENCH_DOOR = FurnitureRegistry.woodSet("french_door", ROOM,
            wood -> new DoorBlock(blockSetType(wood), wood.properties()));

    /** Two blocks tall; slides into the wall next to it. */
    public static final WoodSet<SlidingDoorBlock> SLIDING_DOOR = FurnitureRegistry.woodSet("sliding_door", ROOM,
            wood -> new SlidingDoorBlock(wood.properties()));

    /** Joins with windows next to it; tilts open. */
    public static final WoodSet<FramedWindowBlock> WINDOW = FurnitureRegistry.woodSet("window", ROOM,
            wood -> new FramedWindowBlock(wood.properties()));

    private DecorFurniture() {
    }

    /** The vanilla wood's door sounds; other mods' woods sound like oak. */
    private static BlockSetType blockSetType(FurnitureWood wood) {
        return switch (wood.id()) {
            case "spruce" -> BlockSetType.SPRUCE;
            case "birch" -> BlockSetType.BIRCH;
            case "jungle" -> BlockSetType.JUNGLE;
            case "acacia" -> BlockSetType.ACACIA;
            case "dark_oak" -> BlockSetType.DARK_OAK;
            case "mangrove" -> BlockSetType.MANGROVE;
            case "cherry" -> BlockSetType.CHERRY;
            case "bamboo" -> BlockSetType.BAMBOO;
            case "crimson" -> BlockSetType.CRIMSON;
            case "warped" -> BlockSetType.WARPED;
            default -> BlockSetType.OAK;
        };
    }

    public static void init() {
    }
}
