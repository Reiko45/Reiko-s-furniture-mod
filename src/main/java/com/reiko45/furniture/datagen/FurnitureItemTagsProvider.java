package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.kitchen.appliance.DrinkItem;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Item tags: all furniture, furniture per room and per style, and the drinks (see {@link FurnitureTags}). */
public class FurnitureItemTagsProvider extends ItemTagsProvider {
    public FurnitureItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                     CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper files) {
        super(output, registries, blockTags, ReikosFurniture.MODID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // Create every tag, even ones that end up empty, so the advancements can always load.
        tag(FurnitureTags.FURNITURE);
        tag(FurnitureTags.STYLES);
        tag(FurnitureTags.DRINKS);
        for (Holder<Item> holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.value();
            if (item instanceof FurnitureBlockItem furniture && furniture.room() != null) {
                tag(FurnitureTags.FURNITURE).add(item);
                tag(FurnitureTags.room(furniture.room())).add(item);
                if (furniture.style() != null) {
                    tag(FurnitureTags.STYLES).add(item);
                    tag(FurnitureTags.style(furniture.style())).add(item);
                }
            }
            if (item instanceof DrinkItem) {
                tag(FurnitureTags.DRINKS).add(item);
            }
        }
    }
}
