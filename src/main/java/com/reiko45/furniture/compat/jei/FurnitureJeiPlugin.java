package com.reiko45.furniture.compat.jei;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.compat.RecipeViewers;
import com.reiko45.furniture.core.registry.ModRecipes;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

/** Shows furniture workbench and kitchen appliance recipes in JEI. Only loaded when JEI is installed. */
@JeiPlugin
public class FurnitureJeiPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<CarpentryRecipe>> CARPENTRY = RecipeType.createRecipeHolderType(ReikosFurniture.id("carpentry"));
    public static final RecipeType<RecipeHolder<ApplianceRecipe>> APPLIANCE = RecipeType.createRecipeHolderType(ReikosFurniture.id("appliance"));

    @Override
    public ResourceLocation getPluginUid() {
        return ReikosFurniture.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CarpentryCategory(gui), new ApplianceCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipes = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(CARPENTRY, recipes.getAllRecipesFor(ModRecipes.CARPENTRY.get()));
        registration.addRecipes(APPLIANCE, recipes.getAllRecipesFor(ModRecipes.APPLIANCE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Workbench.BLOCK.get(), CARPENTRY);
        for (ItemStack appliance : RecipeViewers.appliances()) {
            registration.addRecipeCatalyst(appliance, APPLIANCE);
        }
    }
}
