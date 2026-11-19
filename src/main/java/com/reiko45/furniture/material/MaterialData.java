package com.reiko45.furniture.material;

import static com.reiko45.furniture.material.MaterialFurniture.CONCRETE_BENCH;
import static com.reiko45.furniture.material.MaterialFurniture.CONCRETE_COUNTER;
import static com.reiko45.furniture.material.MaterialFurniture.CONCRETE_PLANTER;
import static com.reiko45.furniture.material.MaterialFurniture.CONCRETE_TABLE;
import static com.reiko45.furniture.material.MaterialFurniture.COPPER_CHAIR;
import static com.reiko45.furniture.material.MaterialFurniture.COPPER_LAMP;
import static com.reiko45.furniture.material.MaterialFurniture.COPPER_TABLE;
import static com.reiko45.furniture.material.MaterialFurniture.STONE_BENCH;
import static com.reiko45.furniture.material.MaterialFurniture.STONE_COFFEE_TABLE;
import static com.reiko45.furniture.material.MaterialFurniture.STONE_COUNTER;
import static com.reiko45.furniture.material.MaterialFurniture.STONE_DINING_TABLE;
import static com.reiko45.furniture.material.MaterialFurniture.STONE_PLANTER;
import static com.reiko45.furniture.material.MaterialFurniture.WROUGHT_IRON_BENCH;
import static com.reiko45.furniture.material.MaterialFurniture.WROUGHT_IRON_GARDEN_CHAIR;
import static com.reiko45.furniture.material.MaterialFurniture.WROUGHT_IRON_GARDEN_TABLE;
import static com.reiko45.furniture.material.MaterialFurniture.WROUGHT_IRON_LAMP_POST;

import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.kitchen.CounterBlock;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models, recipes and tags for stone, concrete, copper and wrought-iron furniture. */
public class MaterialData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (StoneMaterial material : StoneMaterial.values()) {
            stonePieces(models, Map.of("side", material.side(), "top", material.top()),
                    STONE_DINING_TABLE.get(material).get(), STONE_COFFEE_TABLE.get(material).get(), STONE_BENCH.get(material).get(),
                    STONE_COUNTER.get(material).get(), STONE_PLANTER.get(material).get());
        }
        for (DyeColor color : DyeColor.values()) {
            String concrete = "minecraft:block/" + color.getName() + "_concrete";
            stonePieces(models, Map.of("side", concrete, "top", concrete),
                    CONCRETE_TABLE.get(color).get(), null, CONCRETE_BENCH.get(color).get(),
                    CONCRETE_COUNTER.get(color).get(), CONCRETE_PLANTER.get(color).get());
        }
        for (CopperStage stage : CopperStage.values()) {
            Map<String, String> copper = Map.of("copper", stage.texture());
            Block lamp = COPPER_LAMP.get(stage).get();
            models.facingLitBlock(lamp, model(models, name(lamp), Room.METAL, "copper_lamp", copper),
                    model(models, name(lamp) + "_on", Room.METAL, "copper_lamp_on", copper));
            models.blockItem(lamp, models.modLoc("block/" + name(lamp)));
            Block table = COPPER_TABLE.get(stage).get();
            models.tableBlock(table, model(models, name(table) + "_top", Room.METAL, "copper_table_top", copper),
                    model(models, name(table) + "_leg", Room.METAL, "copper_table_leg", copper));
            item(models, table, Room.METAL, "copper_table_item", copper);
            Block chair = COPPER_CHAIR.get(stage).get();
            ModelFile chairModel = model(models, name(chair), Room.METAL, "copper_chair", copper);
            models.facingBlock(chair, state -> chairModel);
            models.blockItem(chair, models.modLoc("block/" + name(chair)));
        }
        models.singleFacing(WROUGHT_IRON_GARDEN_CHAIR.get(), Room.METAL, "wrought_iron_garden_chair");
        models.singleFacing(WROUGHT_IRON_GARDEN_TABLE.get(), Room.METAL, "wrought_iron_garden_table");
        models.singleFacing(WROUGHT_IRON_BENCH.get(), Room.METAL, "wrought_iron_bench");
        ModelFile lower = models.existingTemplate(Room.METAL, "wrought_iron_lamp_post_lower");
        ModelFile upper = models.existingTemplate(Room.METAL, "wrought_iron_lamp_post_upper");
        ModelFile upperOn = models.existingTemplate(Room.METAL, "wrought_iron_lamp_post_upper_on");
        models.facingBlock(WROUGHT_IRON_LAMP_POST.get(), state -> state.getValue(TallBlockHelper.HALF) == DoubleBlockHalf.LOWER ? lower
                : state.getValue(SwitchableFurnitureBlock.LIT) ? upperOn : upper);
        models.blockItem(WROUGHT_IRON_LAMP_POST.get(), models.template(Room.METAL, "wrought_iron_lamp_post_item"));
    }

    /** The stone (or concrete) pieces of one material: tables, bench, counter and planter. */
    private static void stonePieces(FurnitureBlockStateProvider models, Map<String, String> tex, Block table, Block coffeeTable,
                                    Block bench, Block counter, Block planter) {
        models.tableBlock(table, model(models, name(table) + "_top", Room.STONE, "dining_table_top", tex),
                model(models, name(table) + "_leg", Room.STONE, "dining_table_leg", tex));
        item(models, table, Room.STONE, "dining_table_item", tex);
        if (coffeeTable != null) {
            models.tableBlock(coffeeTable, model(models, name(coffeeTable) + "_top", Room.STONE, "coffee_table_top", tex),
                    model(models, name(coffeeTable) + "_leg", Room.STONE, "coffee_table_leg", tex));
            item(models, coffeeTable, Room.STONE, "coffee_table_item", tex);
        }
        ModelFile benchModel = model(models, name(bench), Room.STONE, "bench", tex);
        models.facingBlock(bench, state -> benchModel);
        models.blockItem(bench, models.modLoc("block/" + name(bench)));

        Map<String, ModelFile> files = new HashMap<>();
        for (String variant : List.of("", "_open", "_inner", "_outer_left", "_outer_left_open", "_outer_right", "_outer_right_open")) {
            files.put(variant, model(models, name(counter) + variant, Room.STONE, "counter" + variant, tex));
        }
        models.facingBlock(counter, state -> {
            boolean open = state.getValue(CounterBlock.OPEN);
            return switch (state.getValue(CounterBlock.SHAPE)) {
                case STRAIGHT -> files.get(open ? "_open" : "");
                case INNER -> files.get("_inner");
                case OUTER_LEFT -> files.get(open ? "_outer_left_open" : "_outer_left");
                case OUTER_RIGHT -> files.get(open ? "_outer_right_open" : "_outer_right");
            };
        });
        models.blockItem(counter, models.modLoc("block/" + name(counter)));

        models.simpleBlock(planter, model(models, name(planter), Room.STONE, "planter", tex));
        models.blockItem(planter, models.modLoc("block/" + name(planter)));
    }

    private static BlockModelBuilder model(FurnitureBlockStateProvider models, String name, Room room, String template,
                                           Map<String, String> tex) {
        BlockModelBuilder builder = models.models().withExistingParent(name, models.template(room, template));
        new java.util.TreeMap<>(tex).forEach(builder::texture);
        return builder;
    }

    private static void item(FurnitureBlockStateProvider models, Block block, Room room, String template, Map<String, String> tex) {
        var builder = models.itemModels().withExistingParent(name(block), models.template(room, template));
        new java.util.TreeMap<>(tex).forEach(builder::texture);
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (StoneMaterial material : StoneMaterial.values()) {
            stoneRecipes(recipes, output, material.block(), STONE_DINING_TABLE.get(material).get(), STONE_COFFEE_TABLE.get(material).get(),
                    STONE_BENCH.get(material).get(), STONE_COUNTER.get(material).get(), STONE_PLANTER.get(material).get());
        }
        for (DyeColor color : DyeColor.values()) {
            Item concrete = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_concrete"));
            stoneRecipes(recipes, output, concrete, CONCRETE_TABLE.get(color).get(), null, CONCRETE_BENCH.get(color).get(),
                    CONCRETE_COUNTER.get(color).get(), CONCRETE_PLANTER.get(color).get());
        }

        recipes.shaped(COPPER_LAMP.get(CopperStage.UNAFFECTED).get(), 1, "CCC", " T ", " C ")
                .define('C', Items.COPPER_INGOT).define('T', Items.TORCH)
                .unlockedBy("has_copper_ingot", FurnitureRecipeProvider.hasItem(Items.COPPER_INGOT)).save(output);
        recipes.shaped(COPPER_TABLE.get(CopperStage.UNAFFECTED).get(), 1, "CCC", " C ", " C ")
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", FurnitureRecipeProvider.hasItem(Items.COPPER_INGOT)).save(output);
        recipes.shaped(COPPER_CHAIR.get(CopperStage.UNAFFECTED).get(), 1, "C  ", "CCC", "C C")
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", FurnitureRecipeProvider.hasItem(Items.COPPER_INGOT)).save(output);
        // Waxing with honeycomb in the crafting grid, like vanilla copper blocks.
        for (MaterialSet<CopperStage, ?> set : List.of(COPPER_LAMP, COPPER_TABLE, COPPER_CHAIR)) {
            for (CopperStage stage : CopperStage.values()) {
                if (!stage.waxed()) {
                    Block unwaxed = set.get(stage).get();
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, set.get(stage.withWax(true)).get())
                            .requires(unwaxed).requires(Items.HONEYCOMB)
                            .unlockedBy("has_" + name(unwaxed), FurnitureRecipeProvider.hasItem(unwaxed))
                            .save(output, com.reiko45.furniture.ReikosFurniture.id(name(set.get(stage.withWax(true)).get()) + "_from_honeycomb"));
                }
            }
        }

        recipes.shaped(WROUGHT_IRON_GARDEN_CHAIR.get(), 1, "B  ", "III", "B B")
                .define('B', Items.IRON_BARS).define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_bars", FurnitureRecipeProvider.hasItem(Items.IRON_BARS)).save(output);
        recipes.shaped(WROUGHT_IRON_GARDEN_TABLE.get(), 1, "III", " B ", "BBB")
                .define('B', Items.IRON_BARS).define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_bars", FurnitureRecipeProvider.hasItem(Items.IRON_BARS)).save(output);
        recipes.shaped(WROUGHT_IRON_BENCH.get(), 1, "B B", "III", "B B")
                .define('B', Items.IRON_BARS).define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_bars", FurnitureRecipeProvider.hasItem(Items.IRON_BARS)).save(output);
        recipes.shaped(WROUGHT_IRON_LAMP_POST.get(), 1, "L", "B", "B")
                .define('L', Items.LANTERN).define('B', Items.IRON_BARS)
                .unlockedBy("has_lantern", FurnitureRecipeProvider.hasItem(Items.LANTERN)).save(output);
    }

    /** M = the stone or concrete block, C = chest, D = dirt. */
    private static void stoneRecipes(FurnitureRecipeProvider recipes, RecipeOutput output, Item stone, Block table, Block coffeeTable,
                                     Block bench, Block counter, Block planter) {
        String has = "has_" + BuiltInRegistries.ITEM.getKey(stone).getPath();
        recipes.shaped(table, 1, "MMM", " M ", " M ").define('M', stone).unlockedBy(has, FurnitureRecipeProvider.hasItem(stone)).save(output);
        if (coffeeTable != null) {
            recipes.shaped(coffeeTable, 1, "MMM", "M M").define('M', stone).unlockedBy(has, FurnitureRecipeProvider.hasItem(stone)).save(output);
        }
        recipes.shaped(bench, 1, "M M", "MMM").define('M', stone).unlockedBy(has, FurnitureRecipeProvider.hasItem(stone)).save(output);
        recipes.shaped(counter, 1, "MMM", "MCM", "MMM").define('M', stone).define('C', Items.CHEST)
                .unlockedBy(has, FurnitureRecipeProvider.hasItem(stone)).save(output);
        recipes.shaped(planter, 2, "M M", "MDM", "MMM").define('M', stone).define('D', Items.DIRT)
                .unlockedBy(has, FurnitureRecipeProvider.hasItem(stone)).save(output);
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        COPPER_LAMP.all().forEach(block -> tags.tagFor(FurnitureTags.LAMPS).add((Block) block.get()));
        tags.tagFor(FurnitureTags.LAMPS).add(WROUGHT_IRON_LAMP_POST.get());
    }

    /** The steps copper furniture takes as it ages, and its waxed versions, for NeoForge's data maps. */
    public static Map<Block, Block> oxidizing() {
        Map<Block, Block> next = new LinkedHashMap<>();
        for (MaterialSet<CopperStage, ?> set : List.of(COPPER_LAMP, COPPER_TABLE, COPPER_CHAIR)) {
            for (CopperStage stage : CopperStage.values()) {
                if (stage.next() != null) {
                    next.put(set.get(stage).get(), set.get(stage.next()).get());
                }
            }
        }
        return next;
    }

    public static Map<Block, Block> waxing() {
        Map<Block, Block> waxed = new LinkedHashMap<>();
        for (MaterialSet<CopperStage, ?> set : List.of(COPPER_LAMP, COPPER_TABLE, COPPER_CHAIR)) {
            for (CopperStage stage : CopperStage.values()) {
                if (!stage.waxed()) {
                    waxed.put(set.get(stage).get(), set.get(stage.withWax(true)).get());
                }
            }
        }
        return waxed;
    }
}
