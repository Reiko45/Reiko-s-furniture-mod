package com.reiko45.furniture.outdoor;

import java.util.Optional;
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
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Up to four pieces of food on the grill. While the grill is lit they cook using campfire recipes,
 * twice as fast as on a campfire, and pop off the grill when done.
 */
public class GrillBlockEntity extends BlockEntity {
    public static final int SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final int[] progress = new int[SLOTS];
    private final int[] total = new int[SLOTS];

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(OutdoorFurniture.GRILL_BLOCK_ENTITY.get(), pos, state);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public static Optional<RecipeHolder<CampfireCookingRecipe>> recipeFor(Level level, ItemStack stack) {
        return level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(stack), level);
    }

    /** Puts one item on a free spot of the grill. Returns false if the grill is full. */
    public boolean placeFood(Level level, @Nullable LivingEntity entity, ItemStack stack, int cookTime) {
        for (int i = 0; i < SLOTS; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.consumeAndReturn(1, entity));
                progress[i] = 0;
                total[i] = Math.max(1, cookTime / 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(entity, getBlockState()));
                changed();
                return true;
            }
        }
        return false;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity grill) {
        if (!state.getValue(GrillBlock.LIT)) {
            return;
        }
        boolean changed = false;
        for (int i = 0; i < SLOTS; i++) {
            ItemStack stack = grill.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            changed = true;
            if (++grill.progress[i] >= grill.total[i]) {
                SingleRecipeInput input = new SingleRecipeInput(stack);
                ItemStack result = recipeFor(level, stack)
                        .map(recipe -> recipe.value().assemble(input, level.registryAccess()))
                        .orElse(stack);
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, result);
                grill.items.set(i, ItemStack.EMPTY);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
                grill.changed();
            }
        }
        if (changed) {
            setChanged(level, pos, state);
        }
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
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
        copy(tag.getIntArray("CookingTimes"), progress);
        copy(tag.getIntArray("CookingTotalTimes"), total);
    }

    private static void copy(int[] from, int[] to) {
        System.arraycopy(from, 0, to, 0, Math.min(from.length, to.length));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, true, registries);
        tag.putIntArray("CookingTimes", progress);
        tag.putIntArray("CookingTotalTimes", total);
    }

    /** Sends the food on the grill to clients so it can be drawn. */
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
