package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.catalog.CatalogCrafting;
import com.reiko45.furniture.dining.DiningFurniture;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class CatalogCraftingGameTests {
    private static int count(Inventory inventory, Item item) {
        return inventory.items.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // a mock server player has an inventory the server can craft into
    public static void craftingFromTheCatalogUsesTheInventory(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        Inventory inventory = player.getInventory();
        inventory.clearContent();
        inventory.add(new ItemStack(Items.OAK_PLANKS, 12));
        inventory.add(new ItemStack(Items.CHEST, 2));
        Item nightstand = BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get().asItem();

        helper.assertValueEqual(CatalogCrafting.craft(player, ReikosFurniture.id("oak_nightstand"), false), 1, "crafted once");
        helper.assertValueEqual(count(inventory, nightstand), 1, "a nightstand");
        helper.assertValueEqual(count(inventory, Items.OAK_PLANKS), 7, "five planks used");
        helper.assertValueEqual(count(inventory, Items.CHEST), 1, "one chest used");

        // "All" stops when the materials run out: one more chest, so one more nightstand.
        helper.assertValueEqual(CatalogCrafting.craft(player, ReikosFurniture.id("oak_nightstand"), true), 1, "as many as possible");
        helper.assertValueEqual(CatalogCrafting.craft(player, ReikosFurniture.id("oak_nightstand"), false), 0, "no chest left");
        helper.assertValueEqual(count(inventory, nightstand), 2, "two nightstands");

        // Furniture workbench recipes work too: planks only.
        inventory.add(new ItemStack(Items.SPRUCE_PLANKS, 20));
        Item chair = DiningFurniture.DINING_CHAIR.get(FurnitureWood.SPRUCE).get().asItem();
        helper.assertTrue(CatalogCrafting.craft(player, ReikosFurniture.id("carpentry/spruce_dining_chair"), false) == 1,
                "workbench recipe from the catalog");
        helper.assertTrue(count(inventory, chair) >= 1, "a chair");
        helper.assertTrue(count(inventory, Items.SPRUCE_PLANKS) < 20, "planks used");

        // Only the mod's own pieces can be crafted this way.
        inventory.add(new ItemStack(Items.OAK_PLANKS, 4));
        helper.assertValueEqual(CatalogCrafting.craft(player, net.minecraft.resources.ResourceLocation.withDefaultNamespace("crafting_table"),
                false), 0, "vanilla recipes are not in the catalog");
        helper.succeed();
    }
}
