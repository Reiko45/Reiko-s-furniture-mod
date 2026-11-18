package com.reiko45.furniture.compat.emi;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.compat.RecipeViewers;
import com.reiko45.furniture.core.registry.ModRecipes;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Shows furniture workbench and kitchen appliance recipes in EMI. Only loaded when EMI is installed. */
@EmiEntrypoint
public class FurnitureEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory CARPENTRY = new EmiRecipeCategory(ReikosFurniture.id("carpentry"), EmiStack.of(Workbench.BLOCK.get()));
    public static final EmiRecipeCategory APPLIANCE = new EmiRecipeCategory(ReikosFurniture.id("appliance"),
            EmiStack.of(KitchenFurniture.COFFEE_MACHINE.get()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CARPENTRY);
        registry.addCategory(APPLIANCE);
        registry.addWorkstation(CARPENTRY, EmiStack.of(Workbench.BLOCK.get()));
        for (ItemStack appliance : RecipeViewers.appliances()) {
            registry.addWorkstation(APPLIANCE, EmiStack.of(appliance));
        }
        for (RecipeHolder<CarpentryRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipes.CARPENTRY.get())) {
            registry.addRecipe(new CarpentryEmiRecipe(holder));
        }
        for (RecipeHolder<ApplianceRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipes.APPLIANCE.get())) {
            registry.addRecipe(new ApplianceEmiRecipe(holder));
        }
    }
}
