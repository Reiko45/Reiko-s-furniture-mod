package com.reiko45.furniture.compat.emi;

import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Furniture workbench: planks (with the cost as the amount) to one piece. */
public class CarpentryEmiRecipe extends BasicEmiRecipe {
    public CarpentryEmiRecipe(RecipeHolder<CarpentryRecipe> holder) {
        super(FurnitureEmiPlugin.CARPENTRY, holder.id(), 82, 26);
        inputs.add(EmiIngredient.of(holder.value().ingredient(), holder.value().count()));
        outputs.add(EmiStack.of(holder.value().result()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 4);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 5);
        widgets.addSlot(outputs.get(0), 56, 0).large(true).recipeContext(this);
    }
}
