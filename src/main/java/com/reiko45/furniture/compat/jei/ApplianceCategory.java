package com.reiko45.furniture.compat.jei;

import com.reiko45.furniture.compat.RecipeViewers;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Coffee machine, toaster and blender: the ingredients (and cup) go in, the food or drink comes out. */
public class ApplianceCategory extends AbstractRecipeCategory<RecipeHolder<ApplianceRecipe>> {
    public ApplianceCategory(IGuiHelper gui) {
        super(FurnitureJeiPlugin.APPLIANCE, Component.translatable(RecipeViewers.APPLIANCE_TITLE),
                gui.createDrawableItemLike(KitchenFurniture.COFFEE_MACHINE.get()), 116, 44);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ApplianceRecipe> holder, IFocusGroup focuses) {
        ApplianceRecipe recipe = holder.value();
        List<Ingredient> ingredients = recipe.ingredients();
        for (int i = 0; i < ingredients.size(); i++) {
            builder.addInputSlot(1 + 18 * i, 5).setStandardSlotBackground().addIngredients(ingredients.get(i));
        }
        builder.addOutputSlot(95, 5).setOutputSlotBackground().addItemStack(recipe.result());
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 27).addItemStack(RecipeViewers.appliance(recipe));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<ApplianceRecipe> holder, IFocusGroup focuses) {
        builder.addRecipeArrowWidget().setPosition(60, 5);
        builder.addText(RecipeViewers.time(holder.value()), 90, 10).setPosition(22, 31).setColor(0xFF404040);
    }
}
