package com.reiko45.furniture.kitchen.appliance;

import com.reiko45.furniture.core.config.FurnitureConfig;
import com.reiko45.furniture.core.registry.ModRecipes;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Ingredients inside a kitchen appliance. As soon as they match a recipe the appliance starts; when
 * it is done the result pops out (plus leftovers such as the empty milk bucket).
 */
public class ApplianceBlockEntity extends BlockEntity {
    public static final int SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private int progress;
    private int total;

    public ApplianceBlockEntity(BlockPos pos, BlockState state) {
        super(KitchenFurniture.APPLIANCE_BLOCK_ENTITY.get(), pos, state);
    }

    private ApplianceType type() {
        return ((ApplianceBlock) getBlockState().getBlock()).type();
    }

    private ApplianceRecipe.Input input() {
        return new ApplianceRecipe.Input(type().id(), List.copyOf(items));
    }

    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    /** Ticks done so far, and of how many (0 when it isn't running). */
    public int progress() {
        return progress;
    }

    public int total() {
        return total;
    }

    public boolean isRunning() {
        return getBlockState().getValue(ApplianceBlock.RUNNING);
    }

    /** Whether any recipe of this appliance uses the item. */
    public boolean accepts(Level level, ItemStack stack) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.APPLIANCE.get()).stream()
                .anyMatch(holder -> holder.value().appliance().equals(type().id()) && holder.value().uses(stack));
    }

    /** Puts one item in. Returns false if it doesn't fit or isn't used by this appliance. */
    public boolean add(Level level, ItemStack stack, @Nullable LivingEntity entity) {
        if (isRunning() || !accepts(level, stack)) {
            return false;
        }
        for (int i = 0; i < SLOTS; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.consumeAndReturn(1, entity));
                setChanged();
                tryStart(level);
                return true;
            }
        }
        return false;
    }

    /** Gives back everything inside (only while it isn't running). */
    public NonNullList<ItemStack> takeAll() {
        NonNullList<ItemStack> taken = NonNullList.create();
        for (int i = 0; i < SLOTS; i++) {
            if (!items.get(i).isEmpty()) {
                taken.add(items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
        }
        setChanged();
        return taken;
    }

    private Optional<ApplianceRecipe> recipe(Level level) {
        ApplianceRecipe.Input input = input();
        return level.getRecipeManager().getRecipeFor(ModRecipes.APPLIANCE.get(), input, level).map(holder -> holder.value());
    }

    private void tryStart(Level level) {
        recipe(level).ifPresent(recipe -> {
            progress = 0;
            total = FurnitureConfig.applianceTicks(recipe.time());
            level.setBlock(worldPosition, getBlockState().setValue(ApplianceBlock.RUNNING, true), Block.UPDATE_ALL);
            playSound(level, type().runningSound());
        });
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApplianceBlockEntity appliance) {
        if (!state.getValue(ApplianceBlock.RUNNING)) {
            return;
        }
        appliance.progress++;
        if (appliance.progress % 50 == 0 && appliance.progress < appliance.total) {
            appliance.playSound(level, appliance.type().runningSound());
        }
        if (appliance.progress < appliance.total) {
            return;
        }
        Optional<ApplianceRecipe> recipe = appliance.recipe(level);
        if (recipe.isPresent()) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.6;
            double z = pos.getZ() + 0.5;
            Containers.dropItemStack(level, x, y, z, recipe.get().assemble(appliance.input(), level.registryAccess()));
            for (ItemStack used : appliance.items) {
                ItemStack leftover = used.getCraftingRemainingItem();
                if (!leftover.isEmpty()) {
                    Containers.dropItemStack(level, x, y, z, leftover);
                }
            }
            for (int i = 0; i < SLOTS; i++) {
                appliance.items.set(i, ItemStack.EMPTY);
            }
            appliance.playSound(level, appliance.type().doneSound());
        }
        appliance.progress = 0;
        level.setBlock(pos, state.setValue(ApplianceBlock.RUNNING, false), Block.UPDATE_ALL);
        appliance.setChanged();
    }

    private void playSound(Level level, @Nullable SoundEvent sound) {
        if (sound != null) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.7F, 1.0F);
        }
    }

    public void dropContents() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, items);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < SLOTS; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        progress = tag.getInt("Progress");
        total = tag.getInt("Total");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, true, registries);
        tag.putInt("Progress", progress);
        tag.putInt("Total", total);
    }
}
