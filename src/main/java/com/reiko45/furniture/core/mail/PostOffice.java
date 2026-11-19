package com.reiko45.furniture.core.mail;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Everyone's mail, saved with the world. Each player who owns a mailbox has an inbox of 27 slots;
 * mail waits there while they are offline or far away, and any of their mailboxes shows it.
 */
public class PostOffice extends SavedData {
    public static final int INBOX_SIZE = 27;
    private static final String NAME = "reikos_furniture_post_office";

    private final Map<UUID, NonNullList<ItemStack>> inboxes = new LinkedHashMap<>();
    private final Set<UUID> owners = new LinkedHashSet<>();

    public static PostOffice get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(PostOffice::new, PostOffice::load), NAME);
    }

    public void addOwner(UUID player) {
        if (owners.add(player)) {
            setDirty();
        }
    }

    /** Whether the player has (or had) a mailbox, so mail can be sent to them. */
    public boolean isOwner(UUID player) {
        return owners.contains(player);
    }

    private NonNullList<ItemStack> inbox(UUID player) {
        return inboxes.computeIfAbsent(player, p -> NonNullList.withSize(INBOX_SIZE, ItemStack.EMPTY));
    }

    public boolean hasMail(UUID player) {
        NonNullList<ItemStack> inbox = inboxes.get(player);
        return inbox != null && inbox.stream().anyMatch(stack -> !stack.isEmpty());
    }

    /** The player's inbox as a container; taking things out of it is saved. */
    public Container inboxContainer(UUID player) {
        return new Inbox(inbox(player));
    }

    /**
     * Moves everything it can from {@code outgoing} into the recipient's inbox, filling matching stacks
     * first. Whatever doesn't fit stays in {@code outgoing}. Returns true if everything was delivered.
     */
    public boolean deliver(UUID recipient, Container outgoing) {
        NonNullList<ItemStack> inbox = inbox(recipient);
        boolean all = true;
        for (int i = 0; i < outgoing.getContainerSize(); i++) {
            ItemStack stack = outgoing.getItem(i);
            for (int slot = 0; slot < inbox.size() && !stack.isEmpty(); slot++) {
                ItemStack there = inbox.get(slot);
                if (!there.isEmpty() && ItemStack.isSameItemSameComponents(there, stack) && there.getCount() < there.getMaxStackSize()) {
                    int moved = Math.min(stack.getCount(), there.getMaxStackSize() - there.getCount());
                    there.grow(moved);
                    stack.shrink(moved);
                }
            }
            for (int slot = 0; slot < inbox.size() && !stack.isEmpty(); slot++) {
                if (inbox.get(slot).isEmpty()) {
                    inbox.set(slot, stack.copy());
                    stack.setCount(0);
                }
            }
            outgoing.setItem(i, stack.isEmpty() ? ItemStack.EMPTY : stack);
            all &= stack.isEmpty();
        }
        outgoing.setChanged();
        setDirty();
        return all;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        inboxes.forEach((player, items) -> {
            CompoundTag entry = new CompoundTag();
            entry.put("Player", NbtUtils.createUUID(player));
            ContainerHelper.saveAllItems(entry, items, registries);
            list.add(entry);
        });
        tag.put("Inboxes", list);
        ListTag ownerList = new ListTag();
        owners.forEach(owner -> ownerList.add(NbtUtils.createUUID(owner)));
        tag.put("Owners", ownerList);
        return tag;
    }

    public static PostOffice load(CompoundTag tag, HolderLookup.Provider registries) {
        PostOffice office = new PostOffice();
        for (Tag element : tag.getList("Inboxes", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) element;
            NonNullList<ItemStack> items = NonNullList.withSize(INBOX_SIZE, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(entry, items, registries);
            office.inboxes.put(NbtUtils.loadUUID(entry.get("Player")), items);
        }
        for (Tag owner : tag.getList("Owners", Tag.TAG_INT_ARRAY)) {
            office.owners.add(NbtUtils.loadUUID(owner));
        }
        return office;
    }

    /** A player's inbox as a container. Only the owner may take things out; nothing can be put in by hand. */
    private class Inbox implements Container {
        private final NonNullList<ItemStack> items;

        Inbox(NonNullList<ItemStack> items) {
            this.items = items;
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            return items.stream().allMatch(ItemStack::isEmpty);
        }

        @Override
        public ItemStack getItem(int slot) {
            return items.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack taken = ContainerHelper.removeItem(items, slot, amount);
            setChanged();
            return taken;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack taken = ContainerHelper.takeItem(items, slot);
            setChanged();
            return taken;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            items.set(slot, stack);
            setChanged();
        }

        @Override
        public void setChanged() {
            setDirty();
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            items.clear();
            setChanged();
        }
    }
}
