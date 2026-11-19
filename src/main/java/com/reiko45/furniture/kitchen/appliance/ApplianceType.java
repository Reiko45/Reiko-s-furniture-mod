package com.reiko45.furniture.kitchen.appliance;

import com.reiko45.furniture.core.registry.ModSounds;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

/** The small appliances (kitchen, bar, theater and studio). Each has its own recipes ({@link ApplianceRecipe#appliance()} is the id). */
public enum ApplianceType {
    COFFEE_MACHINE("coffee_machine", () -> ModSounds.COFFEE_BREWING.get(), null),
    TOASTER("toaster", null, () -> ModSounds.TOASTER_POP.get()),
    BLENDER("blender", () -> ModSounds.BLENDER.get(), null),
    ESPRESSO_MACHINE("espresso_machine", () -> ModSounds.COFFEE_BREWING.get(), null),
    POPCORN_MACHINE("popcorn_machine", () -> ModSounds.POPCORN_POPPING.get(), null),
    POTTERY_WHEEL("pottery_wheel", () -> ModSounds.POTTERY_WHEEL.get(), null),
    SEWING_MACHINE("sewing_machine", () -> ModSounds.SEWING_MACHINE.get(), null);

    private final String id;
    @Nullable
    private final Supplier<SoundEvent> runningSound;
    @Nullable
    private final Supplier<SoundEvent> doneSound;

    ApplianceType(String id, @Nullable Supplier<SoundEvent> runningSound, @Nullable Supplier<SoundEvent> doneSound) {
        this.id = id;
        this.runningSound = runningSound;
        this.doneSound = doneSound;
    }

    public String id() {
        return id;
    }

    @Nullable
    public SoundEvent runningSound() {
        return runningSound == null ? null : runningSound.get();
    }

    @Nullable
    public SoundEvent doneSound() {
        return doneSound == null ? null : doneSound.get();
    }
}
