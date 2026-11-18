package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.advancement.FurnitureAdvancements;
import com.reiko45.furniture.core.catalog.Catalog;
import com.reiko45.furniture.core.catalog.CatalogEntries;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.styles.StyleFurniture;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class AdvancementGameTests {
    private static AdvancementHolder advancement(GameTestHelper helper, String id) {
        AdvancementHolder holder = helper.getLevel().getServer().getAdvancements().get(FurnitureAdvancements.id(id));
        if (holder == null) {
            throw new GameTestAssertException("advancement " + id + " did not load");
        }
        return holder;
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void advancementsLoad(GameTestHelper helper) {
        for (String id : FurnitureAdvancements.NAMES.keySet()) {
            AdvancementHolder holder = advancement(helper, id);
            helper.assertTrue(holder.value().display().isPresent(), id + " is shown in the tab");
            helper.assertTrue(id.equals("root") == holder.value().parent().isEmpty(), id + " parent");
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // the mock server player is the only way to get a player with advancements
    public static void furnitureActionsGrantAdvancements(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        AdvancementHolder hammer = advancement(helper, "interior_designer");
        AdvancementHolder piano = advancement(helper, "tickling_the_ivories");
        helper.assertFalse(player.getAdvancements().getOrStartProgress(hammer).isDone(), "not done before");

        FurnitureActionTrigger.award(player, FurnitureActionTrigger.HAMMER);
        helper.assertTrue(player.getAdvancements().getOrStartProgress(hammer).isDone(), "the hammer advancement is done");
        helper.assertFalse(player.getAdvancements().getOrStartProgress(piano).isDone(), "other actions don't count");

        FurnitureActionTrigger.award(player, FurnitureActionTrigger.PIANO);
        helper.assertTrue(player.getAdvancements().getOrStartProgress(piano).isDone(), "the piano advancement is done");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void itemTagsSortFurniture(GameTestHelper helper) {
        ItemStack nightstand = new ItemStack(BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get());
        ItemStack rustic = new ItemStack(StyleFurniture.get(Style.RUSTIC).bed().get(FurnitureWood.OAK).get());
        helper.assertTrue(nightstand.is(FurnitureTags.FURNITURE), "nightstand is furniture");
        helper.assertTrue(nightstand.is(FurnitureTags.room(Room.BEDROOM)), "nightstand is bedroom furniture");
        helper.assertFalse(nightstand.is(FurnitureTags.STYLES), "the classic nightstand has no style");
        helper.assertTrue(rustic.is(FurnitureTags.style(Style.RUSTIC)), "rustic bed is rustic");
        helper.assertTrue(rustic.is(FurnitureTags.room(Room.BEDROOM)), "rustic bed is bedroom furniture");
        for (Room room : Room.values()) {
            helper.assertTrue(BuiltInRegistries.ITEM.getTag(FurnitureTags.room(room)).map(tag -> tag.size() > 0).orElse(false),
                    room.id() + " has furniture for Home Sweet Home");
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void catalogIsCraftedAndListsEverything(GameTestHelper helper) {
        CraftingInput input = CraftingInput.of(2, 1, List.of(new ItemStack(Items.BOOK), new ItemStack(Items.SPRUCE_PLANKS)));
        ItemStack crafted = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel())
                .map(recipe -> recipe.value().assemble(input, helper.getLevel().registryAccess())).orElse(ItemStack.EMPTY);
        helper.assertTrue(crafted.is(Catalog.CATALOG.get()), "book and planks make a catalog, got " + crafted);

        List<CatalogEntries.Section> sections = CatalogEntries.sections();
        helper.assertValueEqual(sections.size(), Room.values().length + Style.values().length + 1, "sections");
        Set<Item> listed = new HashSet<>();
        for (CatalogEntries.Section section : sections) {
            helper.assertFalse(section.entries().isEmpty(), section.title().getString() + " has pieces");
            section.entries().forEach(entry -> entry.variants().forEach(stack -> listed.add(stack.getItem())));
        }
        ModItems.ITEMS.getEntries().forEach(holder -> helper.assertTrue(listed.contains(holder.value()),
                holder.getId() + " is in the catalog"));
        // Wood sets are one entry with all eleven woods.
        CatalogEntries.Entry nightstands = sections.get(Room.BEDROOM.ordinal()).entries().stream()
                .filter(entry -> entry.first().getItem() instanceof FurnitureBlockItem
                        && entry.variants().stream().anyMatch(stack -> stack.is(BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get().asItem())))
                .findFirst().orElseThrow(() -> new GameTestAssertException("no nightstand entry"));
        helper.assertTrue(nightstands.variants().size() >= FurnitureWood.values().length, "nightstands in every wood");
        helper.succeed();
    }
}
