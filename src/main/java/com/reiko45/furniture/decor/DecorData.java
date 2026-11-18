package com.reiko45.furniture.decor;

import static com.reiko45.furniture.decor.DecorFurniture.BASEBOARD;
import static com.reiko45.furniture.decor.DecorFurniture.CROWN_MOLDING;
import static com.reiko45.furniture.decor.DecorFurniture.PARQUET;
import static com.reiko45.furniture.decor.DecorFurniture.PARQUET_SLAB;
import static com.reiko45.furniture.decor.DecorFurniture.TILES;
import static com.reiko45.furniture.decor.DecorFurniture.WALLPAPER;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.WallTrimBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models, recipes and tags for the decor section. P = planks, S = wooden slab, D = dye, W = paper. */
public class DecorData implements RoomData {
    private static final Room ROOM = Room.DECOR;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (DyeColor color : DyeColor.values()) {
            Block block = WALLPAPER.get(color).get();
            cube(models, block, ReikosFurniture.id("block/wallpaper/" + color.getName()));
        }
        TILES.forEach((pattern, block) -> cube(models, block.get(), ReikosFurniture.id("block/tiles/" + pattern.id())));

        models.woodSimple(PARQUET, "parquet");
        for (FurnitureWood wood : FurnitureWood.values()) {
            SlabBlock slab = PARQUET_SLAB.get(wood).get();
            String name = name(slab);
            ModelFile bottom = models.woodModel(name, models.template(ROOM, "parquet_slab"), wood);
            ModelFile top = models.woodModel(name + "_top", models.template(ROOM, "parquet_slab_top"), wood);
            ModelFile full = new ModelFile.UncheckedModelFile(ReikosFurniture.id("block/" + name(PARQUET.get(wood).get())));
            models.slabBlock(slab, bottom, top, full);
            models.blockItem(slab, ReikosFurniture.id("block/" + name));
        }
        trims(models, BASEBOARD, "baseboard");
        trims(models, CROWN_MOLDING, "crown_molding");
    }

    private static void cube(FurnitureBlockStateProvider models, Block block, ResourceLocation texture) {
        String name = name(block);
        models.simpleBlock(block, models.models().cubeAll(name, texture));
        models.blockItem(block, ReikosFurniture.id("block/" + name));
    }

    /** A wall trim has a model for each corner shape. */
    private static void trims(FurnitureBlockStateProvider models, WoodSet<WallTrimBlock> set, String template) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            WallTrimBlock block = set.get(wood).get();
            String name = name(block);
            Map<WallTrimBlock.Shape, ModelFile> files = new EnumMap<>(WallTrimBlock.Shape.class);
            for (WallTrimBlock.Shape shape : WallTrimBlock.Shape.values()) {
                String suffix = shape == WallTrimBlock.Shape.STRAIGHT ? "" : "_" + shape.getSerializedName();
                files.put(shape, models.woodModel(name + suffix, models.template(ROOM, template + "_" + shape.getSerializedName()), wood));
            }
            models.facingBlock(block, state -> files.get(state.getValue(WallTrimBlock.SHAPE)));
            models.blockItem(block, ReikosFurniture.id("block/" + name));
        }
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            Item dye = DyeItem.byColor(color);
            recipes.shaped(WALLPAPER.get(color).get(), 8, "WPW", "PDP", "WPW")
                    .define('W', Items.PAPER).define('P', ItemTags.PLANKS).define('D', dye)
                    .unlockedBy("has_paper", FurnitureRecipeProvider.hasItem(Items.PAPER)).save(output);
        }
        tiles(recipes, output, DecorFurniture.Tiles.WHITE, Items.WHITE_CONCRETE, Items.WHITE_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.BLACK, Items.BLACK_CONCRETE, Items.BLACK_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.CHECKERED, Items.WHITE_CONCRETE, Items.BLACK_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.BLUE, Items.BLUE_CONCRETE, Items.BLUE_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.GREEN, Items.GREEN_CONCRETE, Items.GREEN_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.TERRACOTTA, Items.TERRACOTTA, Items.TERRACOTTA);
        tiles(recipes, output, DecorFurniture.Tiles.MARBLE, Items.CALCITE, Items.CALCITE);
        tiles(recipes, output, DecorFurniture.Tiles.HEXAGON, Items.WHITE_CONCRETE, Items.LIGHT_GRAY_CONCRETE);

        recipes.woodShaped(output, PARQUET, 2, wood -> Map.of(), "SS", "SS");
        recipes.woodShaped(output, PARQUET_SLAB, 6, wood -> Map.of('Q', Ingredient.of(PARQUET.get(wood).get())), "QQQ");
        recipes.woodShaped(output, BASEBOARD, 6, wood -> Map.of(), "SSS");
        recipes.woodShaped(output, CROWN_MOLDING, 6, wood -> Map.of(), "S", "S", "S");
    }

    /** Four tiles from a 2x2 of two materials (the same one twice for single-color tiles). */
    private static void tiles(FurnitureRecipeProvider recipes, RecipeOutput output, DecorFurniture.Tiles pattern, Item a, Item b) {
        recipes.shaped(TILES.get(pattern).get(), 4, "AB", "BA")
                .define('A', a).define('B', b)
                .unlockedBy("has_material", FurnitureRecipeProvider.hasItem(a)).save(output);
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        PARQUET_SLAB.all().forEach(slab -> {
            tags.tagFor(BlockTags.SLABS).add(slab.get());
            tags.tagFor(BlockTags.WOODEN_SLABS).add(slab.get());
        });
    }
}
