package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.config.FurnitureConfig;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.registry.ModSounds;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

/** English names. "oak_nightstand" becomes "Oak Nightstand", "light_gray_sofa" becomes "Light Gray Sofa". */
public class FurnitureLanguageProvider extends LanguageProvider {
    /** Words that should not just be capitalized. */
    private static final Map<String, String> SPECIAL_WORDS = Map.of("tv", "TV");

    public FurnitureLanguageProvider(PackOutput output) {
        super(output, ReikosFurniture.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.reikos_furniture", "Reiko's Furniture");
        for (Room room : Room.values()) {
            add(room.translationKey(), room.displayName());
        }

        Set<Block> named = new HashSet<>();
        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            if (!set.hasItem()) {
                continue; // helper blocks (the top bunk) are named by their room
            }
            for (FurnitureWood wood : FurnitureWood.values()) {
                Block block = set.get(wood).get();
                add(block, wood.displayName() + " " + words(set.piece()));
                named.add(block);
            }
        }
        for (ColorSet<?> set : FurnitureRegistry.colorSets()) {
            for (DyeColor color : DyeColor.values()) {
                Block block = set.get(color).get();
                add(block, FurnitureRegistry.titleCase(color.getName()) + " " + words(set.piece()));
                named.add(block);
            }
        }
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            Block block = holder.get();
            if (!named.contains(block)) {
                add(block, words(BuiltInRegistries.BLOCK.getKey(block).getPath()));
            }
        }

        ModSounds.SUBTITLES.forEach((name, text) -> add("subtitles.reikos_furniture." + name, text));
        add("container.reikos_furniture.furniture_workbench", "Furniture Workbench");
        add("container.reikos_furniture.furniture_workbench.cost", "Needs %s planks");
        add("entity.minecraft.villager.reikos_furniture.furniture_maker", "Furniture Maker");
        FurnitureConfig.NAMES.forEach((name, text) -> add(FurnitureConfig.key(name), text));
        add("reikos_furniture.configuration.title", "Reiko's Furniture Settings");
        // Recipe viewer categories (JEI uses the first key, EMI the second).
        add("recipe_category.reikos_furniture.carpentry", "Furniture Workbench");
        add("recipe_category.reikos_furniture.appliance", "Kitchen Appliances");
        add("emi.category.reikos_furniture.carpentry", "Furniture Workbench");
        add("emi.category.reikos_furniture.appliance", "Kitchen Appliances");
        add("recipe.reikos_furniture.appliance_time", "Takes %s s");
        add("entity.minecraft.villager.furniture_maker", "Furniture Maker");

        DataGenerators.ROOMS.forEach(room -> room.translations(this::add));
    }

    private static String words(String id) {
        StringBuilder sb = new StringBuilder();
        for (String part : id.split("_")) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(SPECIAL_WORDS.getOrDefault(part, FurnitureRegistry.titleCase(part)));
        }
        return sb.toString();
    }
}
