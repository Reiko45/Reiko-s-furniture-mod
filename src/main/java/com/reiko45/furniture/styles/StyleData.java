package com.reiko45.furniture.styles;

import com.reiko45.furniture.bedroom.BedroomData;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.kids.KidsData;
import com.reiko45.furniture.kids.KidsFurniture;
import com.reiko45.furniture.livingroom.LivingRoomData;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;

/**
 * Models, recipes and names for the style pieces. Each one is crafted from the classic piece plus the
 * style's material: any log (Rustic), an iron nugget (Modern), a gold nugget (Royal) or bamboo (Japanese).
 */
public class StyleData implements RoomData {
    /** The extra ingredient that turns a classic piece into a style piece. */
    public static Ingredient material(Style style) {
        return switch (style) {
            case RUSTIC -> Ingredient.of(ItemTags.LOGS);
            case MODERN -> Ingredient.of(Items.IRON_NUGGET);
            case ROYAL -> Ingredient.of(Items.GOLD_NUGGET);
            case JAPANESE -> Ingredient.of(Items.BAMBOO);
        };
    }

    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (StyleFurniture.Pieces pieces : StyleFurniture.all().values()) {
            BedroomData.beds(models, pieces.bed());
            KidsData.bunkBeds(models, pieces.bunkBed(), pieces.bunkBedTop());
            models.woodFacing(pieces.diningChair(), "dining_chair");
            models.woodTable(pieces.diningTable(), "dining_table");
            models.woodTable(pieces.coffeeTable(), "coffee_table");
            models.woodFacingOpenable(pieces.nightstand(), "nightstand");
            models.woodFacingOpenable(pieces.dresser(), "dresser");
            models.woodTallOpenable(pieces.wardrobe(), "wardrobe");
            for (DyeColor color : DyeColor.values()) {
                LivingRoomData.sofa(models, pieces.sofa(), color);
                models.woolItem(pieces.sofa(), color, "sofa_item");
            }
            models.colorFacing(pieces.armchair(), "armchair");
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (StyleFurniture.Pieces pieces : StyleFurniture.all().values()) {
            Ingredient material = material(pieces.style());
            wood(output, pieces.bed(), BedroomFurniture.BED, material);
            wood(output, pieces.bunkBed(), KidsFurniture.BUNK_BED, material);
            wood(output, pieces.diningChair(), DiningFurniture.DINING_CHAIR, material);
            wood(output, pieces.diningTable(), DiningFurniture.DINING_TABLE, material);
            wood(output, pieces.coffeeTable(), LivingRoomFurniture.COFFEE_TABLE, material);
            wood(output, pieces.nightstand(), BedroomFurniture.NIGHTSTAND, material);
            wood(output, pieces.dresser(), BedroomFurniture.DRESSER, material);
            wood(output, pieces.wardrobe(), BedroomFurniture.WARDROBE, material);
            color(output, pieces.sofa(), LivingRoomFurniture.SOFA, material);
            color(output, pieces.armchair(), LivingRoomFurniture.ARMCHAIR, material);
        }
    }

    private static void wood(RecipeOutput output, WoodSet<?> style, WoodSet<?> classic, Ingredient material) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            convert(output, style.get(wood).get(), classic.get(wood).get(), material);
        }
    }

    private static void color(RecipeOutput output, ColorSet<?> style, ColorSet<?> classic, Ingredient material) {
        for (DyeColor color : DyeColor.values()) {
            convert(output, style.get(color).get(), classic.get(color).get(), material);
        }
    }

    private static void convert(RecipeOutput output, ItemLike result, ItemLike classic, Ingredient material) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, result)
                .requires(classic)
                .requires(material)
                .unlockedBy("has_classic", FurnitureRecipeProvider.hasItem(classic))
                .save(output);
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        for (StyleFurniture.Pieces pieces : StyleFurniture.all().values()) {
            pieces.bunkBedTop().all().forEach(top -> tags.tagFor(BlockTags.MINEABLE_WITH_AXE).add((Block) top.get()));
        }
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("itemGroup.reikos_furniture.styles", "Reiko's Furniture: Styles");
        for (Style style : Style.values()) {
            add.accept(style.translationKey(), style.displayName() + " Style");
            StyleFurniture.Pieces pieces = StyleFurniture.get(style);
            for (FurnitureWood wood : FurnitureWood.values()) {
                add.accept(pieces.bunkBedTop().get(wood).get().getDescriptionId(),
                        wood.displayName() + " " + style.displayName() + " Bunk Bed");
            }
        }
    }
}
