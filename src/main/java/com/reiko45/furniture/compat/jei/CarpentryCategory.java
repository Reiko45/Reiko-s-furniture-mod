package com.reiko45.furniture.compat.jei;

import com.reiko45.furniture.compat.RecipeViewers;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import com.reiko45.furniture.core.workbench.Workbench;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Furniture workbench: planks (with the cost as the stack size) to one piece. */
public class CarpentryCategory extends AbstractRecipeCategory<RecipeHolder<CarpentryRecipe>> {
    public CarpentryCategory(IGuiHelper gui) {
        super(FurnitureJeiPlugin.CARPENTRY, Component.translatable(RecipeViewers.CARPENTRY_TITLE),
                gui.createDrawableItemLike(Workbench.BLOCK.get()), 82, 26);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CarpentryRecipe> holder, IFocusGroup focuses) {
        CarpentryRecipe recipe = holder.value();
        builder.addInputSlot(1, 5).setStandardSlotBackground().addItemStacks(RecipeViewers.carpentryInputs(recipe));
        builder.addOutputSlot(61, 5).setOutputSlotBackground().addItemStack(recipe.result());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<CarpentryRecipe> holder, IFocusGroup focuses) {
        builder.addRecipeArrowWidget().setPosition(26, 5);
    }
}
