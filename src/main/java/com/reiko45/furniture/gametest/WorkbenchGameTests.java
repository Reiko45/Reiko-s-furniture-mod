package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.workbench.WorkbenchMenu;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class WorkbenchGameTests {
    private static int indexOf(WorkbenchMenu menu, Item result) {
        for (int i = 0; i < menu.getRecipes().size(); i++) {
            if (menu.getRecipes().get(i).value().result().is(result)) {
                return i;
            }
        }
        return -1;
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void workbenchTurnsPlanksIntoFurniture(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        WorkbenchMenu menu = new WorkbenchMenu(0, player.getInventory());
        menu.getSlot(WorkbenchMenu.INPUT_SLOT).set(new ItemStack(Items.OAK_PLANKS, 20));

        Item nightstand = BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get().asItem();
        int index = indexOf(menu, nightstand);
        helper.assertTrue(index >= 0, "oak planks should list an oak nightstand");
        helper.assertTrue(indexOf(menu, BedroomFurniture.NIGHTSTAND.get(FurnitureWood.SPRUCE).get().asItem()) < 0,
                "oak planks should not list spruce furniture");

        menu.clickMenuButton(player, index);
        helper.assertTrue(menu.getSlot(WorkbenchMenu.RESULT_SLOT).getItem().is(nightstand), "result should be the nightstand");

        ItemStack taken = menu.getSlot(WorkbenchMenu.RESULT_SLOT).safeTake(1, 1, player);
        helper.assertTrue(taken.is(nightstand), "should take a nightstand");
        helper.assertValueEqual(menu.getSlot(WorkbenchMenu.INPUT_SLOT).getItem().getCount(), 20 - 13, "planks left");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void workbenchNeedsEnoughPlanks(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        WorkbenchMenu menu = new WorkbenchMenu(0, player.getInventory());
        menu.getSlot(WorkbenchMenu.INPUT_SLOT).set(new ItemStack(Items.BIRCH_PLANKS, 2));

        int index = indexOf(menu, BedroomFurniture.WARDROBE.get(FurnitureWood.BIRCH).get().asItem());
        helper.assertTrue(index >= 0, "the wardrobe should still be listed");
        menu.clickMenuButton(player, index);
        helper.assertTrue(menu.getSlot(WorkbenchMenu.RESULT_SLOT).getItem().isEmpty(), "two planks are not enough for a wardrobe");
        helper.succeed();
    }
}
