package com.reiko45.furniture.core.catalog;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Crafting a piece straight from the catalog, with the materials in the player's inventory: like the
 * recipe book, but without a crafting table. Works for crafting recipes and furniture workbench
 * (planks) recipes of the mod's own items.
 */
public final class CatalogCrafting {
    private CatalogCrafting() {
    }

    /** Whether the player has the materials for one of the recipe's results (checked on either side). */
    public static boolean canCraft(Player player, Recipe<?> recipe) {
        if (recipe instanceof CarpentryRecipe carpentry) {
            return count(player.getInventory(), carpentry.ingredient()) >= carpentry.count();
        }
        if (recipe instanceof CraftingRecipe crafting && !crafting.isSpecial()) {
            StackedContents contents = new StackedContents();
            player.getInventory().fillStackedContents(contents);
            return contents.canCraft(crafting, null);
        }
        return false;
    }

    /**
     * Crafts the recipe once, or as often as the materials allow up to one stack of the result.
     * Returns how many times it was crafted.
     */
    public static int craft(ServerPlayer player, ResourceLocation id, boolean all) {
        RecipeHolder<?> holder = player.server.getRecipeManager().byKey(id).orElse(null);
        if (holder == null) {
            return 0;
        }
        Recipe<?> recipe = holder.value();
        ItemStack result = recipe.getResultItem(player.registryAccess());
        if (result.isEmpty() || !BuiltInRegistries.ITEM.getKey(result.getItem()).getNamespace().equals(ReikosFurniture.MODID)) {
            return 0; // only the mod's own pieces, as listed in the catalog
        }
        int times = all ? Math.max(1, result.getMaxStackSize() / result.getCount()) : 1;
        int crafted = 0;
        while (crafted < times && canCraft(player, recipe)) {
            List<ItemStack> used = new ArrayList<>();
            if (recipe instanceof CarpentryRecipe carpentry) {
                take(player, carpentry.ingredient(), carpentry.count(), used);
            } else {
                // The most particular ingredients first, so "any wool" doesn't use up the white wool another slot needs.
                List<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients().stream().filter(i -> !i.isEmpty()).toList());
                ingredients.sort(java.util.Comparator.comparingInt(i -> i.getItems().length));
                for (Ingredient ingredient : ingredients) {
                    take(player, ingredient, 1, used);
                }
            }
            ItemStack made = result.copy();
            made.onCraftedBy(player.level(), player, made.getCount());
            EventHooks.firePlayerCraftingEvent(player, made, new SimpleContainer(used.toArray(ItemStack[]::new)));
            player.triggerRecipeCrafted(holder, used);
            player.getInventory().placeItemBackInInventory(made);
            crafted++;
        }
        if (crafted > 0) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.PLAYERS, 0.6F, 1.0F);
        }
        return crafted;
    }

    private static int count(Inventory inventory, Ingredient ingredient) {
        int total = 0;
        for (ItemStack stack : inventory.items) {
            if (ingredient.test(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    /** Takes {@code amount} items matching the ingredient, giving back crafting remainders (like empty buckets). */
    private static void take(Player player, Ingredient ingredient, int amount, List<ItemStack> used) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.items.size() && amount > 0; slot++) {
            ItemStack stack = inventory.items.get(slot);
            while (amount > 0 && !stack.isEmpty() && ingredient.test(stack)) {
                ItemStack one = stack.copyWithCount(1);
                ItemStack remainder = stack.getCraftingRemainingItem();
                stack.shrink(1);
                used.add(one);
                amount--;
                if (!remainder.isEmpty()) {
                    inventory.placeItemBackInInventory(remainder);
                }
            }
        }
    }
}
