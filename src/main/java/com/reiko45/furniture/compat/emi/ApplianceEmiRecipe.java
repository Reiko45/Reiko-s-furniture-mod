package com.reiko45.furniture.compat.emi;

import com.reiko45.furniture.compat.RecipeViewers;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Coffee machine, toaster and blender: the ingredients (and cup) go in, the food or drink comes out. */
public class ApplianceEmiRecipe extends BasicEmiRecipe {
    private final ApplianceRecipe recipe;

    public ApplianceEmiRecipe(RecipeHolder<ApplianceRecipe> holder) {
        super(FurnitureEmiPlugin.APPLIANCE, holder.id(), 116, 44);
        this.recipe = holder.value();
        recipe.ingredients().forEach(ingredient -> inputs.add(EmiIngredient.of(ingredient)));
        catalysts.add(EmiStack.of(RecipeViewers.appliance(recipe)));
        outputs.add(EmiStack.of(recipe.result()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), 18 * i, 4);
        }
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 5);
        widgets.addSlot(outputs.get(0), 90, 0).large(true).recipeContext(this);
        widgets.addSlot(catalysts.get(0), 0, 26).catalyst(true);
        widgets.addText(RecipeViewers.time(recipe), 22, 31, 0xFF404040, false);
    }
}
