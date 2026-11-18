package com.reiko45.furniture.nursery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** The parrot in a bird cage, saved like a parrot on a player's shoulder. */
public class BirdCageBlockEntity extends BlockEntity {
    private CompoundTag bird = new CompoundTag();
    /** Client only: the parrot drawn on the perch. */
    @Nullable
    Entity drawn;

    public BirdCageBlockEntity(BlockPos pos, BlockState state) {
        super(NurseryFurniture.BIRD_CAGE_BLOCK_ENTITY.get(), pos, state);
    }

    public CompoundTag bird() {
        return bird;
    }

    public boolean hasBird() {
        return !bird.isEmpty();
    }

    public void setBird(CompoundTag bird) {
        this.bird = bird.copy();
        drawn = null;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        bird = tag.getCompound("Bird");
        drawn = null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Bird", bird);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("Bird", bird);
        return tag;
    }
}
