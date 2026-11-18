package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.workbench.CarpentryRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The furniture workbench's recipe type. */
public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ReikosFurniture.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ReikosFurniture.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CarpentryRecipe>> CARPENTRY =
            TYPES.register("carpentry", () -> RecipeType.simple(ReikosFurniture.id("carpentry")));
    public static final DeferredHolder<RecipeSerializer<?>, CarpentryRecipe.Serializer> CARPENTRY_SERIALIZER =
            SERIALIZERS.register("carpentry", CarpentryRecipe.Serializer::new);

    private ModRecipes() {
    }
}
