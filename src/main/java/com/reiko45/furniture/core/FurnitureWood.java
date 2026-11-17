package com.reiko45.furniture.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** The vanilla wood types every wooden furniture piece is made in. */
public enum FurnitureWood {
    OAK("oak", Blocks.OAK_PLANKS, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_SLAB, true),
    SPRUCE("spruce", Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_SLAB, true),
    BIRCH("birch", Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_SLAB, true),
    JUNGLE("jungle", Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_SLAB, true),
    ACACIA("acacia", Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_SLAB, true),
    DARK_OAK("dark_oak", Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_SLAB, true),
    MANGROVE("mangrove", Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_SLAB, true),
    CHERRY("cherry", Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_SLAB, true),
    BAMBOO("bamboo", Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_SLAB, true),
    CRIMSON("crimson", Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_SLAB, false),
    WARPED("warped", Blocks.WARPED_PLANKS, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_SLAB, false);

    private final String id;
    private final Block planks;
    private final Block log;
    private final Block strippedLog;
    private final Block slab;
    private final boolean flammable;

    FurnitureWood(String id, Block planks, Block log, Block strippedLog, Block slab, boolean flammable) {
        this.id = id;
        this.planks = planks;
        this.log = log;
        this.strippedLog = strippedLog;
        this.slab = slab;
        this.flammable = flammable;
    }

    public String id() {
        return id;
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
        return FurnitureRegistry.titleCase(id);
    }
}
