package com.reiko45.furniture.pets;

import static com.reiko45.furniture.pets.PetFurniture.CAT_TREE;
import static com.reiko45.furniture.pets.PetFurniture.FEEDING_BOWL;
import static com.reiko45.furniture.pets.PetFurniture.PET_BED;

import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models, recipes and tags for the pet corner. W = wool, L = log, C = carpet, T = terracotta. */
public class PetData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.colorFacing(PET_BED, "pet_bed");
        models.woodTall(CAT_TREE, "cat_tree");
        // Feeding bowls are glazed in concrete colors rather than fabric.
        for (DyeColor color : DyeColor.values()) {
            Block block = FEEDING_BOWL.get(color).get();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ModelFile model = models.models().withExistingParent(name, models.template(PET_BED.room(), "feeding_bowl"))
                    .texture("wool", ResourceLocation.withDefaultNamespace("block/" + color.getName() + "_concrete"));
            models.facingBlock(block, state -> model);
            models.blockItem(block, models.modLoc("block/" + name));
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            Item terracotta = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_terracotta"));
            recipes.shaped(PET_BED.get(color).get(), 1, "W W", "WWW")
                    .define('W', wool)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
            recipes.shaped(FEEDING_BOWL.get(color).get(), 1, "T T", " T ")
                    .define('T', terracotta)
                    .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(terracotta)).save(output);
        }
        recipes.woodShaped(output, CAT_TREE, 1, wood -> Map.of('C', Ingredient.of(ItemTags.WOOL_CARPETS)), "CLC", " L ", "CLC");
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        // Cats sit on beds (and on the top of cat trees) through the vanilla "sit on block" goal.
        PET_BED.all().forEach(block -> tags.tagFor(BlockTags.BEDS).add(block.get()));
        CAT_TREE.all().forEach(block -> tags.tagFor(BlockTags.BEDS).add(block.get()));
    }
}
