package com.reiko45.furniture.core.stain;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.repaint.Repaint;
import com.reiko45.furniture.kids.BunkBedBlock;
import com.reiko45.furniture.kids.BunkTopBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

/**
 * Wood stain: sneak and right-click wooden furniture with a dye to tint its wood, and with a sponge to
 * take the stain off. Stains are kept per chunk (a chunk data attachment), so any wooden piece can be
 * stained without a block entity; NeoForge sends them to the players that see the chunk. Broken pieces
 * keep their stain on the dropped item, and placing a stained item stains the new piece.
 */
public final class Stains {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ReikosFurniture.MODID);

    /** Set by the client: told about every change, so the stained blocks are drawn again. */
    public static BiConsumer<StainMap, StainMap> clientListener = (before, after) -> {
    };

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<StainMap>> STAINS = ATTACHMENT_TYPES.register("stains",
            () -> AttachmentType.builder(() -> StainMap.EMPTY)
                    .serialize(StainMap.CODEC, map -> !map.isEmpty())
                    .sync(new AttachmentSyncHandler<StainMap>() {
                        @Override
                        public void write(RegistryFriendlyByteBuf buf, StainMap stains, boolean initialSync) {
                            StainMap.STREAM_CODEC.encode(buf, stains);
                        }

                        @Override
                        public StainMap read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable StainMap previous) {
                            StainMap stains = StainMap.STREAM_CODEC.decode(buf);
                            clientListener.accept(previous == null ? StainMap.EMPTY : previous, stains);
                            return stains;
                        }
                    })
                    .build());

    private Stains() {
    }

    @Nullable
    public static DyeColor get(Level level, BlockPos pos) {
        return level.getChunkAt(pos).getExistingData(STAINS).map(stains -> stains.get(pos)).orElse(null);
    }

    /** Stains one block (or takes the stain off with {@code null}). Server side; players are sent the change. */
    public static void set(Level level, BlockPos pos, @Nullable DyeColor color) {
        LevelChunk chunk = level.getChunkAt(pos);
        StainMap current = chunk.getData(STAINS);
        StainMap next = current.with(pos, color);
        if (!next.equals(current)) {
            chunk.setData(STAINS, next);
            chunk.setUnsaved(true);
        }
    }

    /** Stains every block of the piece at {@code pos}: both halves, both bed parts and top bunks. */
    public static void setPiece(Level level, BlockPos pos, @Nullable DyeColor color) {
        for (BlockPos part : pieceParts(level, pos)) {
            set(level, part, color);
        }
    }

    /** The stain of the piece at {@code pos}, from whichever of its blocks has one. */
    @Nullable
    public static DyeColor getPiece(Level level, BlockPos pos) {
        for (BlockPos part : pieceParts(level, pos)) {
            DyeColor color = get(level, part);
            if (color != null) {
                return color;
            }
        }
        return null;
    }

    private static List<BlockPos> pieceParts(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        List<BlockPos> parts = new ArrayList<>(Repaint.parts(level, pos, state));
        if (state.getBlock() instanceof BunkBedBlock) {
            for (BlockPos part : List.copyOf(parts)) {
                if (level.getBlockState(part.above()).getBlock() instanceof BunkTopBlock) {
                    parts.add(part.above());
                }
            }
        }
        return parts;
    }

    /** The tint for a stain: the dye color washed out over the wood grain. */
    public static int tint(@Nullable DyeColor color) {
        if (color == null) {
            return 0xFFFFFF;
        }
        int dye = color.getTextureDiffuseColor();
        float strength = 0.45F;
        int r = Math.round(255 + (FastColor.ARGB32.red(dye) - 255) * strength);
        int g = Math.round(255 + (FastColor.ARGB32.green(dye) - 255) * strength);
        int b = Math.round(255 + (FastColor.ARGB32.blue(dye) - 255) * strength);
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Stains of pieces players are breaking right now. A player's block is removed before its drops are
     * made, so the stain is noted here first and put on the dropped item. Cleared every tick.
     */
    private static final Map<GlobalPos, DyeColor> BREAKING = new HashMap<>();

    /** Game bus: note the stain of a piece a player breaks, and take it off the blocks. */
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide || !Repaint.isWooden(event.getState().getBlock())) {
            return;
        }
        DyeColor color = getPiece(level, event.getPos());
        for (BlockPos part : Repaint.partPositions(event.getPos(), event.getState())) {
            if (color != null && !event.getPlayer().isCreative()) {
                BREAKING.put(GlobalPos.of(level.dimension(), part), color);
            }
            set(level, part, null);
        }
    }

    /** Game bus: a broken stained piece drops its item with the stain on it. */
    public static void onBlockDrops(BlockDropsEvent event) {
        Level level = event.getLevel();
        BlockState state = event.getState();
        if (!Repaint.isWooden(state.getBlock())) {
            return;
        }
        List<BlockPos> parts = Repaint.partPositions(event.getPos(), state);
        DyeColor color = null;
        for (BlockPos part : parts) {
            DyeColor here = get(level, part);
            DyeColor noted = BREAKING.remove(GlobalPos.of(level.dimension(), part));
            color = color != null ? color : here != null ? here : noted;
            set(level, part, null);
        }
        if (color != null) {
            for (ItemEntity drop : event.getDrops()) {
                if (drop.getItem().getItem() instanceof BlockItem item && item.getBlock() == state.getBlock()) {
                    drop.getItem().set(ModDataComponents.STAIN.get(), color);
                }
            }
        }
    }

    /**
     * Game bus: when a block changes into something that isn't a wooden piece (set by a command, blown
     * up, washed away...), its stain goes too, so a piece placed there later starts unstained.
     */
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide && !Repaint.isWooden(event.getState().getBlock())
                && level.getChunkAt(event.getPos()).getExistingData(STAINS).map(stains -> stains.get(event.getPos()) != null).orElse(false)) {
            set(level, event.getPos(), null);
        }
    }

    /** Game bus: forget noted stains whose pieces dropped nothing. */
    public static void onServerTick(ServerTickEvent.Post event) {
        BREAKING.clear();
    }
}
