package com.reiko45.furniture.datagen;

import com.reiko45.furniture.bathroom.BathroomData;
import com.reiko45.furniture.bedroom.BedroomData;
import com.reiko45.furniture.dining.DiningData;
import com.reiko45.furniture.gameroom.GameRoomData;
import com.reiko45.furniture.kids.KidsData;
import com.reiko45.furniture.kitchen.KitchenData;
import com.reiko45.furniture.laundry.LaundryData;
import com.reiko45.furniture.livingroom.LivingRoomData;
import com.reiko45.furniture.music.MusicData;
import com.reiko45.furniture.office.OfficeData;
import com.reiko45.furniture.outdoor.OutdoorData;
import com.reiko45.furniture.pets.PetData;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** Runs with {@code ./gradlew runData}; output goes to {@code src/generated/resources}. */
public final class DataGenerators {
    /** Data for each room section, in creative tab order. */
    static final List<RoomData> ROOMS = List.of(
            new BedroomData(),
            new KitchenData(),
            new DiningData(),
            new LivingRoomData(),
            new BathroomData(),
            new OfficeData(),
            new OutdoorData(),
            new KidsData(),
            new LaundryData(),
            new MusicData(),
            new GameRoomData(),
            new PetData());

    private DataGenerators() {
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper files = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new FurnitureBlockStateProvider(output, files));
        generator.addProvider(event.includeClient(), new FurnitureLanguageProvider(output));
        generator.addProvider(event.includeClient(), new FurnitureSoundsProvider(output, files));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(FurnitureBlockLoot::new, LootContextParamSets.BLOCK)),
                lookup));
        generator.addProvider(event.includeServer(), new FurnitureRecipeProvider(output, lookup));
        generator.addProvider(event.includeServer(), new FurnitureBlockTagsProvider(output, lookup, files));
        generator.addProvider(event.includeServer(), new FurniturePoiTagsProvider(output, lookup, files));
    }
}
