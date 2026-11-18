package com.reiko45.furniture.office;

import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Safe inventory with an owner. Without an owner (placed by a command or structure) anyone can open it. */
public class SafeBlockEntity extends FurnitureStorageBlockEntity {
    @Nullable
    private UUID owner;
    private String ownerName = "";

    public SafeBlockEntity(BlockPos pos, BlockState state) {
        super(OfficeFurniture.SAFE_BLOCK_ENTITY.get(), pos, state);
    }

    public void setOwner(Player player) {
        this.owner = player.getUUID();
        this.ownerName = player.getGameProfile().getName();
        setChanged();
    }

    public boolean canAccess(Player player) {
        return owner == null || owner.equals(player.getUUID()) || player.isCreative();
    }

    public String ownerName() {
        return ownerName;
    }

    /** Hoppers and other automation can't take things out of a safe. */
    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (owner != null) {
            tag.putUUID("Owner", owner);
            tag.putString("OwnerName", ownerName);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        ownerName = tag.getString("OwnerName");
    }
}
