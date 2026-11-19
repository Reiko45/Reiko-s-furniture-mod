package com.reiko45.furniture.studio;

import com.reiko45.furniture.core.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** The painting on a canvas hung on a wall. It comes from the canvas item and goes back onto it when broken. */
public class CanvasBlockEntity extends BlockEntity {
    private Painting painting = Painting.BLANK;

    public CanvasBlockEntity(BlockPos pos, BlockState state) {
        super(StudioFurniture.CANVAS_BLOCK_ENTITY.get(), pos, state);
    }

    public Painting painting() {
        return painting;
    }

    public void setPainting(Painting painting) {
        this.painting = painting;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        painting = input.getOrDefault(ModDataComponents.PAINTING.get(), Painting.BLANK);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!painting.isBlank() || painting.signed()) {
            components.set(ModDataComponents.PAINTING.get(), painting);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        Painting.CODEC.encodeStart(NbtOps.INSTANCE, painting).ifSuccess(encoded -> tag.put("Painting", encoded));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Tag encoded = tag.get("Painting");
        painting = encoded == null ? Painting.BLANK : Painting.CODEC.parse(NbtOps.INSTANCE, encoded).result().orElse(Painting.BLANK);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Painting");
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
}
