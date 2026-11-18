package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.decor.Paintings;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Lets the vanilla painting item place the mod's paintings. */
public class FurniturePaintingTagsProvider extends TagsProvider<PaintingVariant> {
    public FurniturePaintingTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
        super(output, Registries.PAINTING_VARIANT, lookup, ReikosFurniture.MODID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var placeable = tag(PaintingVariantTags.PLACEABLE);
        Paintings.ALL.forEach(art -> placeable.add(art.key()));
    }
}
