package com.reiko45.furniture.studio;

import static com.reiko45.furniture.studio.StudioFurniture.CANVAS;
import static com.reiko45.furniture.studio.StudioFurniture.EASEL;
import static com.reiko45.furniture.studio.StudioFurniture.FLOOR_CUSHION;
import static com.reiko45.furniture.studio.StudioFurniture.PAINT_POTS;
import static com.reiko45.furniture.studio.StudioFurniture.POTTERY_WHEEL;
import static com.reiko45.furniture.studio.StudioFurniture.SEWING_MACHINE;
import static com.reiko45.furniture.studio.StudioFurniture.TERRACOTTA_VASE;
import static com.reiko45.furniture.studio.StudioFurniture.VASE;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models, recipes and texts for the art studio. T = stick, S = wooden slab, P = paper or planks,
 * I = iron ingot, N = iron nugget.
 */
public class StudioData implements RoomData {
    private static final Room ROOM = Room.STUDIO;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacing(EASEL, "easel");

        ModelFile canvas = models.existingTemplate(ROOM, "canvas");
        models.facingBlock(CANVAS.get(), state -> canvas);
        // The item is drawn by its renderer (the painting); the transforms are those of a flat item.
        models.itemModels().getBuilder(name(CANVAS.get())).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", ReikosFurniture.id("block/canvas"))
                .guiLight(net.minecraft.client.renderer.block.model.BlockModel.GuiLight.FRONT)
                .transforms()
                .transform(ItemDisplayContext.GROUND).translation(0, 2, 0).scale(0.5F).end()
                .transform(ItemDisplayContext.HEAD).rotation(0, 180, 0).translation(0, 13, 7).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).translation(0, 3, 1).scale(0.55F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).translation(0, 3, 1).scale(0.55F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90, 25).translation(1.13F, 3.2F, 1.13F).scale(0.68F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, -90, 25).translation(1.13F, 3.2F, 1.13F).scale(0.68F).end()
                .transform(ItemDisplayContext.FIXED).rotation(0, 180, 0).end()
                .end();

        appliance(models, POTTERY_WHEEL.get(), "pottery_wheel");
        appliance(models, SEWING_MACHINE.get(), "sewing_machine");

        ModelFile terracotta = models.existingTemplate(ROOM, "vase");
        models.facingBlock(TERRACOTTA_VASE.get(), state -> terracotta);
        models.blockItem(TERRACOTTA_VASE.get(), models.template(ROOM, "vase"));
        for (DyeColor color : DyeColor.values()) {
            Block vase = VASE.get(color).get();
            ModelFile glazed = models.models().withExistingParent(name(vase), models.template(ROOM, "vase"))
                    .texture("pot", vanilla(color.getName() + "_terracotta"));
            models.facingBlock(vase, state -> glazed);
            models.blockItem(vase, models.modLoc("block/" + name(vase)));

            Block pots = PAINT_POTS.get(color).get();
            ModelFile paint = models.models().withExistingParent(name(pots), models.template(ROOM, "paint_pots"))
                    .texture("paint", vanilla(color.getName() + "_concrete"));
            models.facingBlock(pots, state -> paint);
            models.blockItem(pots, models.modLoc("block/" + name(pots)));
        }
        models.colorFacing(FLOOR_CUSHION, "floor_cushion");
    }

    private static void appliance(FurnitureBlockStateProvider models, Block block, String template) {
        ModelFile off = models.existingTemplate(ROOM, template);
        ModelFile on = models.existingTemplate(ROOM, template + "_on");
        models.facingBlock(block, state -> state.getValue(ApplianceBlock.RUNNING) ? on : off);
        models.blockItem(block, models.template(ROOM, template));
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private static ResourceLocation vanilla(String block) {
        return ResourceLocation.withDefaultNamespace("block/" + block);
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, EASEL, 1, wood -> Map.of('T', Ingredient.of(Items.STICK)), " T ", "TST", "T T");
        recipes.shaped(CANVAS.get(), 2, "TPT", "TPT")
                .define('T', Items.STICK).define('P', Items.PAPER)
                .unlockedBy("has_paper", FurnitureRecipeProvider.hasItem(Items.PAPER)).save(output);
        recipes.shaped(POTTERY_WHEEL.get(), 1, "SIS", "PPP")
                .define('S', Items.SMOOTH_STONE_SLAB).define('I', Items.IRON_INGOT).define('P', ItemTags.PLANKS)
                .unlockedBy("has_clay_ball", FurnitureRecipeProvider.hasItem(Items.CLAY_BALL)).save(output);
        recipes.shaped(SEWING_MACHINE.get(), 1, "III", "TNT")
                .define('I', Items.IRON_INGOT).define('T', Items.STRING).define('N', Items.IRON_NUGGET)
                .unlockedBy("has_string", FurnitureRecipeProvider.hasItem(Items.STRING)).save(output);

        Ingredient clay = Ingredient.of(Items.CLAY_BALL);
        appliance(output, "terracotta_vase", ApplianceType.POTTERY_WHEEL, new ItemStack(TERRACOTTA_VASE.get()), 120, clay, clay, clay);
        for (DyeColor color : DyeColor.values()) {
            Item dye = DyeItem.byColor(color);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, VASE.get(color).get())
                    .requires(TERRACOTTA_VASE.get()).requires(dye)
                    .unlockedBy("has_vase", FurnitureRecipeProvider.hasItem(TERRACOTTA_VASE.get().asItem()))
                    .save(output);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, PAINT_POTS.get(color).get())
                    .requires(Items.IRON_NUGGET).requires(Items.IRON_NUGGET).requires(Items.STICK).requires(dye)
                    .unlockedBy("has_dye", FurnitureRecipeProvider.hasItem(dye))
                    .save(output);
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            appliance(output, color.getName() + "_floor_cushion", ApplianceType.SEWING_MACHINE,
                    new ItemStack(FLOOR_CUSHION.get(color).get(), 2), 80, Ingredient.of(wool), Ingredient.of(Items.STRING));
        }
    }

    private static void appliance(RecipeOutput output, String name, ApplianceType type, ItemStack result, int time,
                                  Ingredient... ingredients) {
        output.accept(ReikosFurniture.id("appliance/" + name), new ApplianceRecipe(type.id(), List.of(ingredients), result, time), null);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("container.reikos_furniture.easel", "Painting");
        add.accept("container.reikos_furniture.easel.sign", "Sign");
        add.accept("container.reikos_furniture.easel.title", "Title");
        add.accept("container.reikos_furniture.easel.fill", "Fill");
        add.accept("container.reikos_furniture.easel.brush", "Brush");
        add.accept("container.reikos_furniture.easel.signing", "Give it a title. A signed painting can't be changed.");
        add.accept("item.reikos_furniture.canvas.by", "by %s");
        add.accept("item.reikos_furniture.canvas.unfinished", "Unfinished painting");
        add.accept("item.reikos_furniture.canvas.tooltip", "Put it on an easel to paint it");
    }
}
