package com.reiko45.furniture.nursery;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** The fish in an aquarium, kept as the buckets they came in (so tropical fish keep their pattern). */
public class AquariumBlockEntity extends BlockEntity {
    public static final int MAX_FISH = 3;
    /** Fish buckets the aquarium takes, and the fish each one holds. */
    public static final Map<Item, EntityType<?>> FISH = Map.of(Items.COD_BUCKET, EntityType.COD, Items.SALMON_BUCKET, EntityType.SALMON,
            Items.TROPICAL_FISH_BUCKET, EntityType.TROPICAL_FISH, Items.PUFFERFISH_BUCKET, EntityType.PUFFERFISH);

    private final NonNullList<ItemStack> fish = NonNullList.withSize(MAX_FISH, ItemStack.EMPTY);
    /** Client only: the fish entities drawn in the tank. */
    final Entity[] drawn = new Entity[MAX_FISH];

    public AquariumBlockEntity(BlockPos pos, BlockState state) {
        super(NurseryFurniture.AQUARIUM_BLOCK_ENTITY.get(), pos, state);
    }

    public NonNullList<ItemStack> fish() {
        return fish;
    }

    public int count() {
        return (int) fish.stream().filter(stack -> !stack.isEmpty()).count();
    }

    /** Puts the fish from a bucket into the tank; false if it's full. */
    public boolean add(ItemStack bucket) {
        for (int i = 0; i < MAX_FISH; i++) {
            if (fish.get(i).isEmpty()) {
                fish.set(i, bucket.copyWithCount(1));
                changed();
                return true;
            }
        }
        return false;
    }

    /** Scoops out the last fish put in, as a filled bucket. */
    public ItemStack take() {
        for (int i = MAX_FISH - 1; i >= 0; i--) {
            if (!fish.get(i).isEmpty()) {
                ItemStack taken = fish.get(i);
                fish.set(i, ItemStack.EMPTY);
                changed();
                return taken;
            }
        }
        return ItemStack.EMPTY;
    }

    public void dropFish() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, fish);
        }
    }

    private void changed() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < MAX_FISH; i++) {
            fish.set(i, ItemStack.EMPTY);
            drawn[i] = null;
        }
        ContainerHelper.loadAllItems(tag, fish, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, fish, true, registries);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, fish, true, registries);
        return tag;
    }
}
