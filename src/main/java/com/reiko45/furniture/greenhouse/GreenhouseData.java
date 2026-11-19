package com.reiko45.furniture.greenhouse;

import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.GARDEN_SHED_CABINET;
import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.GROWING_POT;
import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.HANGING_PLANTER;
import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.PLANT_STAND;
import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.TERRACOTTA_GROWING_POT;
import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.WATERING_CAN;
import static com.reiko45.furniture.greenhouse.GreenhouseFurniture.WINDOW_FLOWER_BOX;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models, recipes and texts for the greenhouse and garden. P = planks, S = wooden slab, L = log,
 * T = terracotta, D = dirt, C = chest, H = hoe.
 */
public class GreenhouseData implements RoomData {
    private static final Room ROOM = Room.GREENHOUSE;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacing(PLANT_STAND, "plant_stand");
        models.woodFacing(WINDOW_FLOWER_BOX, "window_flower_box");
        models.woodTallOpenable(GARDEN_SHED_CABINET, "garden_shed_cabinet");

        // The growth stage only changes how big the plant is drawn, not the pot.
        ModelFile terracotta = models.existingTemplate(ROOM, "growing_pot");
        models.facingBlock(TERRACOTTA_GROWING_POT.get(), state -> terracotta, GrowingPotBlock.GROWTH);
        models.blockItem(TERRACOTTA_GROWING_POT.get(), models.template(ROOM, "growing_pot"));
        for (DyeColor color : DyeColor.values()) {
            Block pot = GROWING_POT.get(color).get();
            ModelFile glazed = models.models().withExistingParent(name(pot), models.template(ROOM, "growing_pot"))
                    .texture("pot", terracotta(color)).texture("band", concrete(color));
            models.facingBlock(pot, state -> glazed, GrowingPotBlock.GROWTH);
            models.blockItem(pot, models.modLoc("block/" + name(pot)));

            Block planter = HANGING_PLANTER.get(color).get();
            ModelFile hanging = models.models().withExistingParent(name(planter), models.template(ROOM, "hanging_planter"))
                    .texture("pot", terracotta(color));
            models.facingBlock(planter, state -> hanging);
            models.blockItem(planter, models.modLoc("block/" + name(planter)));
        }
        models.itemModels().basicItem(WATERING_CAN.get());
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private static ResourceLocation terracotta(DyeColor color) {
        return ResourceLocation.withDefaultNamespace("block/" + color.getName() + "_terracotta");
    }

    private static ResourceLocation concrete(DyeColor color) {
        return ResourceLocation.withDefaultNamespace("block/" + color.getName() + "_concrete");
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, PLANT_STAND, 1, wood -> Map.of('F', Ingredient.of(Items.FLOWER_POT)), " F ", "SSS", "L L");
        recipes.woodShaped(output, WINDOW_FLOWER_BOX, 2, wood -> Map.of('D', Ingredient.of(Items.DIRT)), "SDS");
        recipes.woodShaped(output, GARDEN_SHED_CABINET, 1,
                wood -> Map.of('C', Ingredient.of(Items.CHEST), 'H', Ingredient.of(ItemTags.HOES)), "PHP", "PCP", "PPP");
        recipes.shaped(TERRACOTTA_GROWING_POT.get(), 1, "TDT", " T ")
                .define('T', Items.TERRACOTTA).define('D', Items.DIRT)
                .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(Items.TERRACOTTA)).save(output);
        for (DyeColor color : DyeColor.values()) {
            Item terracotta = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_terracotta"));
            recipes.shaped(GROWING_POT.get(color).get(), 1, "TDT", " T ")
                    .define('T', terracotta).define('D', Items.DIRT)
                    .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(terracotta)).save(output);
            recipes.shaped(HANGING_PLANTER.get(color).get(), 1, " S ", "TDT")
                    .define('S', Items.STRING).define('T', terracotta).define('D', Items.DIRT)
                    .unlockedBy("has_terracotta", FurnitureRecipeProvider.hasItem(terracotta)).save(output);
        }
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, WATERING_CAN.get())
                .requires(Items.BUCKET).requires(Items.IRON_NUGGET).requires(Items.IRON_NUGGET).requires(Items.GREEN_DYE)
                .unlockedBy("has_bucket", FurnitureRecipeProvider.hasItem(Items.BUCKET))
                .save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("item.reikos_furniture.watering_can", "Watering Can");
        add.accept("item.reikos_furniture.watering_can.water", "Water: %s/%s");
        add.accept("item.reikos_furniture.watering_can.tooltip",
                "Right-click water, a sink or a bathtub to fill it, then plants to water them (3×3)");
        add.accept("message.reikos_furniture.watering_can_empty", "The watering can is empty");
    }
}
