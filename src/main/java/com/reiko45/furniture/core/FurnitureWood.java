package com.reiko45.furniture.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A wood type furniture is made in. The eleven vanilla woods are constants (and are what data
 * generation uses); woods from other mods are found at startup by {@code ModdedWoods} and added to
 * {@link #modded()}.
 */
public final class FurnitureWood {
    private static final List<FurnitureWood> VANILLA = new ArrayList<>();
    private static final List<FurnitureWood> MODDED = new ArrayList<>();

    public static final FurnitureWood OAK = vanilla("oak", Blocks.OAK_PLANKS, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_SLAB, true);
    public static final FurnitureWood SPRUCE = vanilla("spruce", Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_SLAB, true);
    public static final FurnitureWood BIRCH = vanilla("birch", Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_SLAB, true);
    public static final FurnitureWood JUNGLE = vanilla("jungle", Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_SLAB, true);
    public static final FurnitureWood ACACIA = vanilla("acacia", Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_SLAB, true);
    public static final FurnitureWood DARK_OAK = vanilla("dark_oak", Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_SLAB, true);
    public static final FurnitureWood MANGROVE = vanilla("mangrove", Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_SLAB, true);
    public static final FurnitureWood CHERRY = vanilla("cherry", Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_SLAB, true);
    public static final FurnitureWood BAMBOO = vanilla("bamboo", Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_SLAB, true);
    public static final FurnitureWood CRIMSON = vanilla("crimson", Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_SLAB, false);
    public static final FurnitureWood WARPED = vanilla("warped", Blocks.WARPED_PLANKS, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_SLAB, false);

    private final String id;
    private final String displayName;
    private final Block planks;
    private final Block log;
    private final Block strippedLog;
    private final Block slab;
    private final boolean flammable;
    private final boolean modded;

    private FurnitureWood(String id, String displayName, Block planks, Block log, Block strippedLog, Block slab,
                          boolean flammable, boolean modded) {
        this.id = id;
        this.displayName = displayName;
        this.planks = planks;
        this.log = log;
        this.strippedLog = strippedLog;
        this.slab = slab;
        this.flammable = flammable;
        this.modded = modded;
    }

    private static FurnitureWood vanilla(String id, Block planks, Block log, Block strippedLog, Block slab, boolean flammable) {
        FurnitureWood wood = new FurnitureWood(id, FurnitureRegistry.titleCase(id), planks, log, strippedLog, slab, flammable, false);
        VANILLA.add(wood);
        return wood;
    }

    /**
     * A wood from another mod. {@code id} is used in block names ({@code <id>_<piece>}), so it includes
     * the mod's namespace, e.g. {@code biomesoplenty_fir}.
     */
    public static FurnitureWood modded(String id, String displayName, Block planks, Block log, Block strippedLog, Block slab,
                                       boolean flammable) {
        FurnitureWood wood = new FurnitureWood(id, displayName, planks, log, strippedLog, slab, flammable, true);
        MODDED.add(wood);
        return wood;
    }

    /** The vanilla woods, in their usual order. Data generation and recipes in the jar only cover these. */
    public static FurnitureWood[] values() {
        return VANILLA.toArray(new FurnitureWood[0]);
    }

    /** Woods found in other mods at startup (empty until blocks are registered). */
    public static List<FurnitureWood> modded() {
        return Collections.unmodifiableList(MODDED);
    }

    public String id() {
        return id;
    }

    public boolean isModded() {
        return modded;
    }

    public Block planks() {
        return planks;
    }

    public Block log() {
        return log;
    }

    public Block strippedLog() {
        return strippedLog;
    }

    public Block slab() {
        return slab;
    }

    /** Nether woods (crimson, warped) do not burn. */
    public boolean flammable() {
        return flammable;
    }

    /** Block properties (sound, hardness, map color) copied from the planks, without occlusion. */
    public BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.ofFullCopy(planks).noOcclusion();
    }

    public ResourceLocation planksTexture() {
        return texture(planks);
    }

    public ResourceLocation logTexture() {
        return texture(log);
    }

    public ResourceLocation strippedLogTexture() {
        return texture(strippedLog);
    }

    private static ResourceLocation texture(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "block/" + key.getPath());
    }

    /** "dark_oak" becomes "Dark Oak". */
    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return id;
    }
}
