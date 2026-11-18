package com.reiko45.furniture.laundry;

import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Runs a 5-second cycle after the door closes (or after a hopper puts something in), then washes
 * (or dries) everything inside.
 */
public class LaundryMachineBlockEntity extends FurnitureStorageBlockEntity {
    public static final int CYCLE_TICKS = 100;
    private int timer;

    public LaundryMachineBlockEntity(BlockPos pos, BlockState state) {
        super(LaundryFurniture.LAUNDRY_MACHINE_BLOCK_ENTITY.get(), pos, state);
    }

    private LaundryMachineBlock.Kind kind() {
        return ((LaundryMachineBlock) getBlockState().getBlock()).kind();
    }

    /** Takes the dye out: leather (and horse/wolf armor) color, shulker boxes, the top banner pattern, wool and carpet. */
    public static ItemStack wash(ItemStack stack) {
        if (stack.has(DataComponents.DYED_COLOR)) {
            ItemStack copy = stack.copy();
            copy.remove(DataComponents.DYED_COLOR);
            return copy;
        }
        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof ShulkerBoxBlock box && box.getColor() != null) {
            return stack.transmuteCopy(Items.SHULKER_BOX, stack.getCount());
        }
        BannerPatternLayers patterns = stack.get(DataComponents.BANNER_PATTERNS);
        if (patterns != null && !patterns.layers().isEmpty()) {
            ItemStack copy = stack.copy();
            copy.set(DataComponents.BANNER_PATTERNS, patterns.removeLast());
            return copy;
        }
        if (stack.is(ItemTags.WOOL) && !stack.is(Items.WHITE_WOOL)) {
            return stack.transmuteCopy(Items.WHITE_WOOL, stack.getCount());
        }
        if (stack.is(ItemTags.WOOL_CARPETS) && !stack.is(Items.WHITE_CARPET)) {
            return stack.transmuteCopy(Items.WHITE_CARPET, stack.getCount());
        }
        return stack;
    }

    /** Dries wet sponges. */
    public static ItemStack dry(ItemStack stack) {
        return stack.is(Items.WET_SPONGE) ? stack.transmuteCopy(Items.SPONGE, stack.getCount()) : stack;
    }

    private boolean anythingToDo() {
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty() && kind().process(stack) != stack) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onClosed() {
        startIfNeeded();
    }

    /** Starts a cycle if anything inside can be washed (or dried). */
    private void startIfNeeded() {
        if (level != null && !level.isClientSide && anythingToDo()) {
            timer = CYCLE_TICKS;
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(LaundryMachineBlock.RUNNING, true), Block.UPDATE_ALL);
            level.playSound(null, worldPosition, kind().sound(), SoundSource.BLOCKS, 0.7F, 1.0F);
            setChanged();
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LaundryMachineBlockEntity machine) {
        if (!state.getValue(LaundryMachineBlock.RUNNING)) {
            // Hoppers can fill it too: start by itself once nobody has the door open.
            if (level.getGameTime() % 20 == 0 && machine.openerCount() == 0) {
                machine.startIfNeeded();
            }
            return;
        }
        machine.timer--;
        if (machine.timer > 0 && machine.timer % 60 == 0) {
            level.playSound(null, pos, machine.kind().sound(), SoundSource.BLOCKS, 0.7F, 1.0F);
        }
        if (machine.timer > 0) {
            return;
        }
        for (int i = 0; i < machine.getContainerSize(); i++) {
            ItemStack stack = machine.getItem(i);
            if (!stack.isEmpty()) {
                machine.getItems().set(i, machine.kind().process(stack));
            }
        }
        level.setBlock(pos, state.setValue(LaundryMachineBlock.RUNNING, false), Block.UPDATE_ALL);
        level.playSound(null, pos, ModSounds.MICROWAVE_DING.get(), SoundSource.BLOCKS, 0.6F, 0.8F);
        machine.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Timer", timer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        timer = tag.getInt("Timer");
    }
}
