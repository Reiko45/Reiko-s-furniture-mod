package com.reiko45.furniture.outdoor;

import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** A doorbell: works like a button (a short redstone pulse) and rings when pressed. */
public class DoorbellBlock extends ButtonBlock {
    public DoorbellBlock(Properties properties) {
        super(BlockSetType.IRON, 20, properties);
    }

    @Override
    protected SoundEvent getSound(boolean isOn) {
        return isOn ? ModSounds.DOORBELL.get() : SoundEvents.STONE_BUTTON_CLICK_OFF;
    }
}
