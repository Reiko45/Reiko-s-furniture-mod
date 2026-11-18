package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

/** Writes sounds.json: one .ogg file per sound event, with a subtitle. */
public class FurnitureSoundsProvider extends SoundDefinitionsProvider {
    public FurnitureSoundsProvider(PackOutput output, ExistingFileHelper files) {
        super(output, ReikosFurniture.MODID, files);
    }

    @Override
    public void registerSounds() {
        for (Holder<SoundEvent> holder : ModSounds.SOUNDS.getEntries()) {
            String name = holder.unwrapKey().orElseThrow().location().getPath();
            add(holder.value(), definition()
                    .subtitle("subtitles.reikos_furniture." + name)
                    .with(sound(ReikosFurniture.id(name))));
        }
    }
}
