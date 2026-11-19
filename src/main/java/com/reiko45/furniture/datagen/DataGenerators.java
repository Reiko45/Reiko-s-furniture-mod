package com.reiko45.furniture.datagen;

import com.reiko45.furniture.bar.BarData;
import com.reiko45.furniture.bathroom.BathroomData;
import com.reiko45.furniture.bedroom.BedroomData;
import com.reiko45.furniture.core.tool.ToolsData;
import com.reiko45.furniture.decor.DecorData;
import com.reiko45.furniture.dining.DiningData;
import com.reiko45.furniture.gameroom.GameRoomData;
import com.reiko45.furniture.garage.GarageData;
import com.reiko45.furniture.library.LibraryData;
import com.reiko45.furniture.kids.KidsData;
import com.reiko45.furniture.kitchen.KitchenData;
import com.reiko45.furniture.laundry.LaundryData;
import com.reiko45.furniture.livingroom.LivingRoomData;
import com.reiko45.furniture.music.MusicData;
import com.reiko45.furniture.nursery.NurseryData;
import com.reiko45.furniture.greenhouse.GreenhouseData;
import com.reiko45.furniture.theater.TheaterData;
import com.reiko45.furniture.studio.StudioData;
import com.reiko45.furniture.holiday.HolidayData;
import com.reiko45.furniture.styles.StyleData;
import com.reiko45.furniture.datagen.lang.French;
import com.reiko45.furniture.datagen.lang.German;
import com.reiko45.furniture.datagen.lang.Grammar;
import com.reiko45.furniture.datagen.lang.Portuguese;
import com.reiko45.furniture.datagen.lang.Spanish;
import com.reiko45.furniture.datagen.lang.TranslatedLanguageProvider;
import com.reiko45.furniture.core.catalog.CatalogData;
import com.reiko45.furniture.core.lighting.LightingData;
import com.reiko45.furniture.core.mail.MailData;
import com.reiko45.furniture.core.advancement.FurnitureAdvancements;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import com.reiko45.furniture.office.OfficeData;
import com.reiko45.furniture.outdoor.OutdoorData;
import com.reiko45.furniture.pets.PetData;
import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.decor.Paintings;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
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
            new PetData(),
            new DecorData(),
            new GarageData(),
            new LibraryData(),
            new BarData(),
            new NurseryData(),
            new GreenhouseData(),
            new TheaterData(),
            new StudioData(),
            new HolidayData(),
            new StyleData(),
            new CatalogData(),
            new LightingData(),
            new MailData(),
            new ToolsData());

    private DataGenerators() {
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper files = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new FurnitureBlockStateProvider(output, files));
        generator.addProvider(event.includeClient(), new FurnitureLanguageProvider(output));
        for (Grammar language : List.of(new Spanish(), new French(), new German(), new Portuguese())) {
            generator.addProvider(event.includeClient(), new TranslatedLanguageProvider(output, language));
        }
        generator.addProvider(event.includeClient(), new FurnitureSoundsProvider(output, files));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(FurnitureBlockLoot::new, LootContextParamSets.BLOCK)),
                lookup));
        generator.addProvider(event.includeServer(), new FurnitureRecipeProvider(output, lookup));
        FurnitureBlockTagsProvider blockTags = generator.addProvider(event.includeServer(),
                new FurnitureBlockTagsProvider(output, lookup, files));
        generator.addProvider(event.includeServer(), new FurnitureItemTagsProvider(output, lookup, blockTags.contentsGetter(), files));
        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookup, files, List.of(new FurnitureAdvancements())));
        generator.addProvider(event.includeServer(), new FurniturePoiTagsProvider(output, lookup, files));
        // Painting variants are data (a datapack registry), with a tag so paintings place them.
        DatapackBuiltinEntriesProvider paintings = new DatapackBuiltinEntriesProvider(output, lookup,
                new RegistrySetBuilder().add(Registries.PAINTING_VARIANT, Paintings::bootstrap), Set.of(ReikosFurniture.MODID));
        generator.addProvider(event.includeServer(), paintings);
        generator.addProvider(event.includeServer(), new FurniturePaintingTagsProvider(output, paintings.getRegistryProvider(), files));
    }
}
