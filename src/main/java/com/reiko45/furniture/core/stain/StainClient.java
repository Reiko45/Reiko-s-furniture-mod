package com.reiko45.furniture.core.stain;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Client side of wood stain. The block color handler runs on chunk-building threads, so the stains it
 * reads are copied into a concurrent map whenever a chunk's stains arrive, and the changed blocks are
 * drawn again. Stained faces use tint index 1 (the wood textures in every furniture template).
 */
public final class StainClient {
    private static final Map<Long, DyeColor> STAINS = new ConcurrentHashMap<>();

    private StainClient() {
    }

    /** Called from the chunk attachment's sync handler when new stains arrive for a chunk. */
    public static void onChanged(StainMap before, StainMap after) {
        before.stains().keySet().forEach(STAINS::remove);
        STAINS.putAll(after.stains());
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.levelRenderer == null) {
            return;
        }
        List<Long> changed = new ArrayList<>(before.stains().keySet());
        changed.addAll(after.stains().keySet());
        for (long packed : changed) {
            BlockPos pos = BlockPos.of(packed);
            minecraft.levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> tintIndex == 1 && pos != null ? Stains.tint(STAINS.get(pos.asLong())) : 0xFFFFFF,
                woodenBlocks());
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        List<Item> items = new ArrayList<>();
        for (Block block : woodenBlocks()) {
            if (block.asItem() != net.minecraft.world.item.Items.AIR) {
                items.add(block.asItem());
            }
        }
        event.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF000000 | Stains.tint(stack.get(ModDataComponents.STAIN.get())) : -1,
                items.toArray(Item[]::new));
    }

    /** Every wooden piece, in vanilla woods and other mods' woods. */
    private static Block[] woodenBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            set.all().forEach(block -> blocks.add(block.get()));
        }
        return blocks.toArray(Block[]::new);
    }

    /** Game bus: forget the stains of chunks the client unloads. */
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            event.getChunk().getExistingData(Stains.STAINS).ifPresent(stains -> stains.stains().keySet().forEach(STAINS::remove));
        }
    }

    /** Game bus: forget everything when leaving a world. */
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            STAINS.clear();
        }
    }
}
