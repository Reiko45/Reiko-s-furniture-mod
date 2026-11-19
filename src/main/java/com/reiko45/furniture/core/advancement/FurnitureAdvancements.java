package com.reiko45.furniture.core.advancement;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.registry.ModEntities;
import com.reiko45.furniture.core.tool.Tools;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.gameroom.GameRoomFurniture;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.laundry.LaundryFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.music.MusicFurniture;
import com.reiko45.furniture.pets.PetFurniture;
import com.reiko45.furniture.styles.StyleFurniture;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

/** The "Reiko's Furniture" advancement tab, written by data generation. */
public class FurnitureAdvancements implements AdvancementProvider.AdvancementGenerator {
    /** The loot table the "Woodworker" advancement gives: a furniture catalog. */
    public static final ResourceKey<LootTable> CATALOG_REWARD =
            ResourceKey.create(Registries.LOOT_TABLE, ReikosFurniture.id("advancement/catalog"));

    /** English title and description of every advancement, by id (also used for the translations). */
    public static final Map<String, String[]> NAMES = new LinkedHashMap<>();

    static {
        name("root", "Reiko's Furniture", "Make your first piece of furniture");
        name("woodworker", "Woodworker", "Make a Furniture Workbench. You get a furniture catalog with it");
        name("take_a_seat", "Take a Seat", "Sit down on a chair, sofa or stool");
        name("coffee_break", "Coffee Break", "Have a drink made with a kitchen or bar appliance");
        name("home_sweet_home", "Home Sweet Home", "Have a piece of furniture from every room");
        name("interior_designer", "Interior Designer", "Turn a piece of furniture with a hammer");
        name("heavy_lifting", "Heavy Lifting", "Move storage furniture with a moving dolly, contents and all");
        name("a_matter_of_style", "A Matter of Style", "Make a piece of furniture in a style");
        name("style_icon", "Style Icon", "Have a piece in each of the eight styles");
        name("tickling_the_ivories", "Tickling the Ivories", "Play a note on a piano");
        name("bullseye", "Bullseye!", "Score 50 with a dart");
        name("high_score", "High Score", "Score 9000 or more on an arcade cabinet");
        name("fresh_laundry", "Fresh Laundry", "Wash the dye out of something in a washing machine");
        name("good_boy", "Who's a Good Boy?", "Have your pet eat from a feeding bowl");
        name("valued_customer", "Valued Customer", "Buy furniture from a Furniture Maker");
    }

    private static void name(String id, String title, String description) {
        NAMES.put(id, new String[] {title, description});
    }

    public static String titleKey(String id) {
        return "advancements.reikos_furniture." + id + ".title";
    }

    public static String descriptionKey(String id) {
        return "advancements.reikos_furniture." + id + ".description";
    }

    public static ResourceLocation id(String id) {
        return ReikosFurniture.id(id);
    }

    /** Advancements written so far, so children can name their parent. */
    private final Map<String, AdvancementHolder> saved = new LinkedHashMap<>();

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper files) {
        FurnitureWood oak = FurnitureWood.OAK;
        save(saver, "root", null, BedroomFurniture.NIGHTSTAND.get(oak).get(), AdvancementType.TASK,
                builder -> builder.addCriterion("has_furniture", has(FurnitureTags.FURNITURE)));
        save(saver, "woodworker", "root", Workbench.BLOCK.get(), AdvancementType.TASK, builder -> builder
                .addCriterion("has_workbench", InventoryChangeTrigger.TriggerInstance.hasItems(Workbench.BLOCK.get()))
                .rewards(AdvancementRewards.Builder.loot(CATALOG_REWARD)));
        save(saver, "take_a_seat", "root", DiningFurniture.DINING_CHAIR.get(oak).get(), AdvancementType.TASK, builder -> builder
                .addCriterion("sat_down", StartRidingTrigger.TriggerInstance.playerStartsRiding(EntityPredicate.Builder.entity()
                        .vehicle(EntityPredicate.Builder.entity().of(ModEntities.SEAT.get())))));
        save(saver, "coffee_break", "take_a_seat", KitchenFurniture.COFFEE.get(), AdvancementType.TASK, builder -> builder
                .addCriterion("drank", ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(FurnitureTags.DRINKS))));
        save(saver, "home_sweet_home", "take_a_seat", LivingRoomFurniture.SOFA.get(DyeColor.RED).get(), AdvancementType.CHALLENGE,
                builder -> {
                    for (Room room : Room.values()) {
                        builder.addCriterion(room.id(), has(FurnitureTags.room(room)));
                    }
                    builder.requirements(AdvancementRequirements.Strategy.AND).rewards(AdvancementRewards.Builder.experience(100));
                });
        save(saver, "interior_designer", "woodworker", Tools.HAMMER.get(), AdvancementType.TASK,
                builder -> builder.addCriterion("hammer", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.HAMMER)));
        save(saver, "heavy_lifting", "interior_designer", Tools.MOVING_DOLLY.get(), AdvancementType.TASK,
                builder -> builder.addCriterion("dolly", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.DOLLY)));
        save(saver, "a_matter_of_style", "woodworker", StyleFurniture.get(Style.RUSTIC).bed().get(oak).get(), AdvancementType.TASK,
                builder -> builder.addCriterion("has_style", has(FurnitureTags.STYLES)));
        save(saver, "style_icon", "a_matter_of_style", StyleFurniture.get(Style.ROYAL).bed().get(FurnitureWood.DARK_OAK).get(),
                AdvancementType.GOAL, builder -> {
                    for (Style style : Style.values()) {
                        builder.addCriterion(style.id(), has(FurnitureTags.style(style)));
                    }
                    builder.requirements(AdvancementRequirements.Strategy.AND);
                });
        save(saver, "valued_customer", "woodworker", Items.EMERALD, AdvancementType.TASK, builder -> builder
                .addCriterion("traded", CriteriaTriggers.TRADE.createCriterion(new TradeTrigger.TriggerInstance(
                        Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(FurnitureTags.FURNITURE).build())))));
        save(saver, "tickling_the_ivories", "root", MusicFurniture.PIANO.get(oak).get(), AdvancementType.TASK,
                builder -> builder.addCriterion("piano", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.PIANO)));
        save(saver, "bullseye", "tickling_the_ivories", GameRoomFurniture.DARTBOARD.get(), AdvancementType.TASK,
                builder -> builder.addCriterion("bullseye", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.BULLSEYE)));
        save(saver, "high_score", "bullseye", GameRoomFurniture.ARCADE_CABINET.get(), AdvancementType.GOAL, builder -> builder
                .addCriterion("high_score", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.ARCADE_HIGH_SCORE)));
        save(saver, "fresh_laundry", "root", LaundryFurniture.WASHING_MACHINE.get(), AdvancementType.TASK,
                builder -> builder.addCriterion("wash", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.WASH)));
        save(saver, "good_boy", "root", PetFurniture.FEEDING_BOWL.get(DyeColor.RED).get(), AdvancementType.TASK,
                builder -> builder.addCriterion("pet_fed", FurnitureActionTrigger.TriggerInstance.action(FurnitureActionTrigger.PET_FED)));
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag));
    }

    private void save(Consumer<AdvancementHolder> saver, String id, @Nullable String parent, ItemLike icon,
                      AdvancementType type, Consumer<Advancement.Builder> criteria) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        if (parent != null) {
            builder.parent(saved.get(parent));
        }
        boolean root = parent == null;
        builder.display(icon, Component.translatable(titleKey(id)), Component.translatable(descriptionKey(id)),
                root ? ResourceLocation.withDefaultNamespace("textures/block/stripped_oak_log.png") : null,
                type, !root, !root, false);
        criteria.accept(builder);
        saved.put(id, builder.save(saver, id(id).toString()));
    }
}
