package com.reiko45.furniture.core.display;

import com.reiko45.furniture.core.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** The items shown on a {@link DisplayFurnitureBlock}, one per slot. Synced to clients so they can be drawn. */
public class DisplayBlockEntity extends BlockEntity {
    private final NonNullList<ItemStack> items;

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISPLAY.get(), pos, state);
        int size = state.getBlock() instanceof DisplayFurnitureBlock display ? display.slots().size() : 1;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean hasSpace() {
        return items.stream().anyMatch(stack -> stack.isEmpty() || stack.getCount() < maxPerSlot());
    }

    private int maxPerSlot() {
        return getBlockState().getBlock() instanceof DisplayFurnitureBlock display ? display.maxPerSlot() : 1;
    }

    /** The slot one more of this item would go in (topping up a matching stack first), or -1 if it doesn't fit. */
    private int slotFor(ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack existing = items.get(i);
            if (!existing.isEmpty() && existing.getCount() < maxPerSlot() && ItemStack.isSameItemSameComponents(existing, stack)) {
                return i;
            }
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /** Whether one more of this item fits. Works on the client too (the items are synced). */
    public boolean canInsert(ItemStack stack) {
        return slotFor(stack) >= 0;
    }

    /** Puts one item from the stack down: on a matching stack if there's room, otherwise on the first free spot. */
    public boolean insert(ItemStack stack, @Nullable LivingEntity entity) {
        int slot = slotFor(stack);
        if (slot < 0) {
            return false;
        }
        if (items.get(slot).isEmpty()) {
            items.set(slot, stack.consumeAndReturn(1, entity));
        } else {
            stack.consume(1, entity);
            items.get(slot).grow(1);
        }
        changed();
        return true;
    }

    /** Takes the last item that was put down. */
    public ItemStack takeLast() {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack taken = items.get(i);
                items.set(i, ItemStack.EMPTY);
                changed();
                return taken;
            }
        }
        return ItemStack.EMPTY;
    }

    /** Removes one item from a slot (used by the feeding bowl when pets eat). */
    public void shrink(int slot) {
        items.get(slot).shrink(1);
        changed();
    }

    public void dropContents() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, items);
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
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, true, registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, items, true, registries);
        return tag;
    }
}
