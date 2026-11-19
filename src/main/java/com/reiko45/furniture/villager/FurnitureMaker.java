package com.reiko45.furniture.villager;

import com.google.common.collect.ImmutableSet;
import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.lighting.Lighting;
import com.reiko45.furniture.core.mail.Mail;
import com.reiko45.furniture.core.registry.ModSounds;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.gameroom.GameRoomFurniture;
import com.reiko45.furniture.holiday.HolidayFurniture;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.music.MusicFurniture;
import com.reiko45.furniture.studio.StudioFurniture;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The furniture maker villager. An unemployed villager near a furniture workbench takes the job. They
 * buy planks, logs and wool, and sell furniture: small pieces at first, sofas, beds and appliances later.
 */
public final class FurnitureMaker {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, ReikosFurniture.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, ReikosFurniture.MODID);

    /** The furniture workbench is the job site (any rotation). */
    public static final DeferredHolder<PoiType, PoiType> WORKBENCH_POI = POI_TYPES.register("furniture_workbench",
            () -> new PoiType(ImmutableSet.copyOf(Workbench.BLOCK.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> FURNITURE_MAKER = PROFESSIONS.register("furniture_maker",
            () -> new VillagerProfession("furniture_maker", poi -> poi.is(WORKBENCH_POI.getKey()), poi -> poi.is(WORKBENCH_POI.getKey()),
                    ImmutableSet.of(), ImmutableSet.of(), ModSounds.WORKBENCH_SAW.get()));

    private static final float PRICE_MULTIPLIER = 0.05F;

    private FurnitureMaker() {
    }

    /** Adds the furniture maker's trades. Registered on the game event bus. */
    public static void addTrades(VillagerTradesEvent event) {
        if (event.getType() != FURNITURE_MAKER.get()) {
            return;
        }
        var trades = event.getTrades();
        // Novice
        trades.get(1).addAll(List.of(
                buy(random -> randomWood(random).planks(), 20, 16, 2),
                sell(anyWood(DiningFurniture.DINING_CHAIR), 2, 1, 12, 1),
                sell(anyWood(BedroomFurniture.TABLE_LAMP), 1, 2, 12, 1),
                sell(random -> Lighting.LIGHT_SWITCH.get(), 1, 2, 12, 1)));
        // Apprentice
        trades.get(2).addAll(List.of(
                buy(random -> randomWood(random).log(), 6, 16, 10),
                sell(anyWood(LivingRoomFurniture.COFFEE_TABLE), 1, 3, 12, 5),
                sell(anyColor(LivingRoomFurniture.RUG), 2, 2, 12, 5),
                sell(anyWood(Mail.MAILBOX), 1, 3, 12, 5)));
        // Journeyman
        trades.get(3).addAll(List.of(
                buy(random -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(randomColor(random).getName() + "_wool")), 12, 16, 20),
                sell(anyWood(LivingRoomFurniture.FLOOR_LAMP), 1, 4, 12, 10),
                sell(anyWood(LivingRoomFurniture.BOOKSHELF), 1, 5, 12, 10),
                sell(anyColor(StudioFurniture.VASE), 1, 2, 12, 10)));
        // Expert
        trades.get(4).addAll(List.of(
                sell(anyColor(LivingRoomFurniture.SOFA), 1, 6, 12, 15),
                sell(anyWood(BedroomFurniture.BED), 1, 8, 12, 15),
                sell(anyWood(MusicFurniture.RECORD_PLAYER), 1, 7, 12, 15),
                sell(anyColor(HolidayFurniture.PRESENT), 1, 3, 12, 15)));
        // Master
        trades.get(5).addAll(List.of(
                sell(random -> LivingRoomFurniture.TV.get(), 1, 12, 12, 30),
                sell(random -> KitchenFurniture.FRIDGE.get(), 1, 10, 12, 30),
                sell(random -> GameRoomFurniture.ARCADE_CABINET.get(), 1, 15, 12, 30),
                sell(anyWood(MusicFurniture.PIANO), 1, 12, 12, 30)));
    }

    private static FurnitureWood randomWood(RandomSource random) {
        FurnitureWood[] woods = FurnitureWood.values();
        return woods[random.nextInt(woods.length)];
    }

    private static DyeColor randomColor(RandomSource random) {
        DyeColor[] colors = DyeColor.values();
        return colors[random.nextInt(colors.length)];
    }

    private static Function<RandomSource, ItemLike> anyWood(WoodSet<?> set) {
        return random -> set.get(randomWood(random)).get();
    }

    private static Function<RandomSource, ItemLike> anyColor(ColorSet<?> set) {
        return random -> set.get(randomColor(random)).get();
    }

    /** The villager buys {@code count} of an item for one emerald. */
    private static VillagerTrades.ItemListing buy(Function<RandomSource, ItemLike> item, int count, int maxUses, int xp) {
        return (trader, random) -> new MerchantOffer(new ItemCost(item.apply(random), count), new ItemStack(Items.EMERALD),
                maxUses, xp, PRICE_MULTIPLIER);
    }

    /** The villager sells {@code count} of an item for some emeralds. */
    private static VillagerTrades.ItemListing sell(Function<RandomSource, ItemLike> item, int count, int emeralds, int maxUses, int xp) {
        return (trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), new ItemStack(item.apply(random), count),
                maxUses, xp, PRICE_MULTIPLIER);
    }
}
