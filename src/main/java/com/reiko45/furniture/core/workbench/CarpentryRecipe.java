package com.reiko45.furniture.core.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.reiko45.furniture.core.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * A furniture workbench recipe: {@code count} of {@code ingredient} (usually planks) makes {@code result}.
 * Recipes are listed for any matching ingredient; the result is only available once there are enough.
 */
public record CarpentryRecipe(Ingredient ingredient, int count, ItemStack result) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CarpentryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CarpentryRecipe::ingredient),
            Codec.intRange(1, 64).optionalFieldOf("count", 1).forGetter(CarpentryRecipe::count),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CarpentryRecipe::result)
    ).apply(instance, CarpentryRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CarpentryRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CarpentryRecipe::ingredient,
            ByteBufCodecs.VAR_INT, CarpentryRecipe::count,
            ItemStack.STREAM_CODEC, CarpentryRecipe::result,
            CarpentryRecipe::new);

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    /** Whether the input stack holds enough of the ingredient to make the result. */
    public boolean canAfford(ItemStack input) {
        return ingredient.test(input) && input.getCount() >= count;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, ingredient);
    }

    /** Not shown in the vanilla recipe book; the workbench screen lists these recipes itself. */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CARPENTRY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CARPENTRY.get();
    }

    public static class Serializer implements RecipeSerializer<CarpentryRecipe> {
        @Override
        public MapCodec<CarpentryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CarpentryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
