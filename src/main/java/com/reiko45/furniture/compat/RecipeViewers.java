package com.reiko45.furniture.compat;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Shared helpers for the JEI and EMI plugins (which are only loaded when those mods are installed). */
public final class RecipeViewers {
    public static final String CARPENTRY_TITLE = "recipe_category.reikos_furniture.carpentry";
    public static final String APPLIANCE_TITLE = "recipe_category.reikos_furniture.appliance";

    private RecipeViewers() {
    }

    /** The planks a workbench recipe takes, with the right count on each stack. */
    public static List<ItemStack> carpentryInputs(CarpentryRecipe recipe) {
        return Arrays.stream(recipe.ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.count())).toList();
    }

    /** The appliance block that makes a recipe (its id is the block's name). */
    public static ItemStack appliance(ApplianceRecipe recipe) {
        return new ItemStack(BuiltInRegistries.ITEM.get(ReikosFurniture.id(recipe.appliance())));
    }

    /** The appliance blocks, which are the catalysts / workstations of the appliance category. */
    public static List<ItemStack> appliances() {
        return List.of(new ItemStack(KitchenFurniture.COFFEE_MACHINE.get()), new ItemStack(KitchenFurniture.TOASTER.get()),
                new ItemStack(KitchenFurniture.BLENDER.get()));
    }

    /** "Takes 5 s" */
    public static Component time(ApplianceRecipe recipe) {
        return Component.translatable("recipe.reikos_furniture.appliance_time", String.format("%.1f", recipe.time() / 20.0F).replace(".0", ""));
    }
}
