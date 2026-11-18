package com.reiko45.furniture.kitchen.appliance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.reiko45.furniture.core.registry.ModRecipes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A recipe for a kitchen appliance: put in exactly these ingredients (one item each, in any order)
 * and after {@code time} ticks the appliance gives {@code result}.
 */
public record ApplianceRecipe(String appliance, List<Ingredient> ingredients, ItemStack result, int time)
        implements Recipe<ApplianceRecipe.Input> {

    public static final MapCodec<ApplianceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("appliance").forGetter(ApplianceRecipe::appliance),
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(ApplianceRecipe::ingredients),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ApplianceRecipe::result),
            Codec.intRange(1, 12000).fieldOf("time").forGetter(ApplianceRecipe::time)
    ).apply(instance, ApplianceRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ApplianceRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ApplianceRecipe::appliance,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ApplianceRecipe::ingredients,
            ItemStack.STREAM_CODEC, ApplianceRecipe::result,
            ByteBufCodecs.VAR_INT, ApplianceRecipe::time,
            ApplianceRecipe::new);

    /** What is inside an appliance. */
    public record Input(String appliance, List<ItemStack> items) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }
    }

    @Override
    public boolean matches(Input input, Level level) {
        if (!appliance.equals(input.appliance())) {
            return false;
        }
        List<ItemStack> contents = input.items().stream().filter(stack -> !stack.isEmpty()).toList();
        return contents.size() == ingredients.size() && assign(contents, new ArrayList<>(ingredients));
    }

    /** Matches every item to a different ingredient (the lists are tiny, so trying all orders is fine). */
    private static boolean assign(List<ItemStack> items, List<Ingredient> left) {
        if (items.isEmpty()) {
            return left.isEmpty();
        }
        ItemStack first = items.get(0);
        for (int i = 0; i < left.size(); i++) {
            if (left.get(i).test(first)) {
                List<Ingredient> rest = new ArrayList<>(left);
                rest.remove(i);
                if (assign(items.subList(1, items.size()), rest)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Whether the item is part of this recipe at all (so the appliance accepts it). */
    public boolean uses(ItemStack stack) {
        return ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
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
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }

    /** Not in the vanilla recipe book; JEI and EMI show these recipes. */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.APPLIANCE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.APPLIANCE.get();
    }

    public static class Serializer implements RecipeSerializer<ApplianceRecipe> {
        @Override
        public MapCodec<ApplianceRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplianceRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
