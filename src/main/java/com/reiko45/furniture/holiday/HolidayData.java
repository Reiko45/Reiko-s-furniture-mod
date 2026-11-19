package com.reiko45.furniture.holiday;

import static com.reiko45.furniture.holiday.HolidayFurniture.CANDY_BOWL;
import static com.reiko45.furniture.holiday.HolidayFurniture.CHRISTMAS_TREE;
import static com.reiko45.furniture.holiday.HolidayFurniture.COLORFUL_STRING_LIGHTS;
import static com.reiko45.furniture.holiday.HolidayFurniture.FAKE_COBWEB;
import static com.reiko45.furniture.holiday.HolidayFurniture.GRAVESTONE;
import static com.reiko45.furniture.holiday.HolidayFurniture.HANGING_BATS;
import static com.reiko45.furniture.holiday.HolidayFurniture.PRESENT;
import static com.reiko45.furniture.holiday.HolidayFurniture.SNOW_GLOBE;
import static com.reiko45.furniture.holiday.HolidayFurniture.STOCKING;
import static com.reiko45.furniture.holiday.HolidayFurniture.WARM_STRING_LIGHTS;
import static com.reiko45.furniture.holiday.HolidayFurniture.WITCH_CAULDRON;
import static com.reiko45.furniture.holiday.HolidayFurniture.WREATH;

import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.function.BiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models, recipes, tags and texts for the holiday decorations. */
public class HolidayData implements RoomData {
    private static final Room ROOM = Room.HOLIDAY;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        ModelFile lower = models.existingTemplate(ROOM, "christmas_tree_lower");
        ModelFile upper = models.existingTemplate(ROOM, "christmas_tree_upper");
        ModelFile lowerOn = models.existingTemplate(ROOM, "christmas_tree_lower_on");
        ModelFile upperOn = models.existingTemplate(ROOM, "christmas_tree_upper_on");
        models.facingBlock(CHRISTMAS_TREE.get(), state -> {
            boolean on = state.getValue(SwitchableFurnitureBlock.LIT);
            return state.getValue(TallBlockHelper.HALF) == DoubleBlockHalf.UPPER ? (on ? upperOn : upper) : (on ? lowerOn : lower);
        });
        models.blockItem(CHRISTMAS_TREE.get(), models.template(ROOM, "christmas_tree_item"));

        for (DyeColor color : DyeColor.values()) {
            Block present = PRESENT.get(color).get();
            ModelFile closed = models.woolModel(name(present), models.template(ROOM, "present"), color);
            ModelFile open = models.woolModel(name(present) + "_open", models.template(ROOM, "present_open"), color);
            models.facingBlock(present, state -> state.getValue(FurnitureStorageBlock.OPEN) ? open : closed);
            models.blockItem(present, models.modLoc("block/" + name(present)));
        }
        models.colorFacing(STOCKING, "stocking");

        stringLights(models, WARM_STRING_LIGHTS.get(), "reikos_furniture:block/holiday/bulb_warm",
                "reikos_furniture:block/holiday/bulb_warm", "reikos_furniture:block/holiday/bulb_warm");
        stringLights(models, COLORFUL_STRING_LIGHTS.get(), "minecraft:block/red_concrete", "minecraft:block/lime_concrete",
                "minecraft:block/light_blue_concrete");

        models.singleFacing(WREATH.get(), ROOM, "wreath");
        ModelFile globe = models.existingTemplate(ROOM, "snow_globe");
        models.facingBlock(SNOW_GLOBE.get(), state -> globe, SnowGlobeBlock.SHAKEN);
        models.blockItem(SNOW_GLOBE.get(), models.template(ROOM, "snow_globe"));
        models.singleFacing(GRAVESTONE.get(), ROOM, "gravestone");
        models.singleFacing(HANGING_BATS.get(), ROOM, "hanging_bats");
        models.singleFacing(CANDY_BOWL.get(), ROOM, "candy_bowl");
        models.singleFacing(WITCH_CAULDRON.get(), ROOM, "witch_cauldron");

        ResourceLocation cobweb = ResourceLocation.withDefaultNamespace("block/cobweb");
        models.simpleBlock(FAKE_COBWEB.get(), models.models().cross(name(FAKE_COBWEB.get()), cobweb).renderType("cutout"));
        models.itemModels().withExistingParent(name(FAKE_COBWEB.get()), "item/generated").texture("layer0", cobweb);
    }

    /** A strand on a wall or a ceiling, lit or not, with a hook on each end that isn't joined to another strand. */
    private static void stringLights(FurnitureBlockStateProvider models, Block block, String bulb1, String bulb2, String bulb3) {
        String name = name(block);
        var builder = models.getMultipartBuilder(block);
        for (AttachFace face : new AttachFace[] {AttachFace.WALL, AttachFace.CEILING}) {
            String kind = face == AttachFace.WALL ? "wall" : "ceiling";
            ModelFile off = models.existingTemplate(ROOM, "string_lights_" + kind);
            ModelFile on = models.models().withExistingParent(name + "_" + kind + "_on", models.template(ROOM, "string_lights_" + kind + "_on"))
                    .texture("b1", bulb1).texture("b2", bulb2).texture("b3", bulb3);
            ModelFile hookWest = models.existingTemplate(ROOM, "string_lights_" + kind + "_hook_west");
            ModelFile hookEast = models.existingTemplate(ROOM, "string_lights_" + kind + "_hook_east");
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                int y = FurnitureBlockStateProvider.yRot(facing);
                for (boolean lit : new boolean[] {false, true}) {
                    builder.part().modelFile(lit ? on : off).rotationY(y).addModel()
                            .condition(StringLightsBlock.FACE, face).condition(StringLightsBlock.FACING, facing)
                            .condition(StringLightsBlock.LIT, lit).end();
                }
                builder.part().modelFile(hookWest).rotationY(y).addModel()
                        .condition(StringLightsBlock.FACE, face).condition(StringLightsBlock.FACING, facing)
                        .condition(StringLightsBlock.WEST, false).end();
                builder.part().modelFile(hookEast).rotationY(y).addModel()
                        .condition(StringLightsBlock.FACE, face).condition(StringLightsBlock.FACING, facing)
                        .condition(StringLightsBlock.EAST, false).end();
            }
        }
        models.blockItem(block, models.modLoc("block/" + name + "_wall_on"));
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.shaped(CHRISTMAS_TREE.get(), 1, " G ", "LDL", "LSL")
                .define('G', Items.GOLD_NUGGET).define('L', ItemTags.LEAVES).define('D', Items.GLOWSTONE_DUST).define('S', ItemTags.LOGS)
                .unlockedBy("has_glowstone_dust", FurnitureRecipeProvider.hasItem(Items.GLOWSTONE_DUST)).save(output);
        recipes.shaped(WARM_STRING_LIGHTS.get(), 4, "TTT", "GGG")
                .define('T', Items.STRING).define('G', Items.GLOWSTONE_DUST)
                .unlockedBy("has_glowstone_dust", FurnitureRecipeProvider.hasItem(Items.GLOWSTONE_DUST)).save(output);
        recipes.shaped(COLORFUL_STRING_LIGHTS.get(), 4, "TTT", "RGB")
                .define('T', Items.STRING).define('G', Items.GLOWSTONE_DUST).define('R', Items.RED_DYE).define('B', Items.BLUE_DYE)
                .unlockedBy("has_glowstone_dust", FurnitureRecipeProvider.hasItem(Items.GLOWSTONE_DUST)).save(output);
        recipes.shaped(WREATH.get(), 1, "LLL", "L L", "LRL")
                .define('L', ItemTags.LEAVES).define('R', Items.RED_DYE)
                .unlockedBy("has_leaves", FurnitureRecipeProvider.hasItem(Items.OAK_LEAVES)).save(output);
        recipes.shaped(SNOW_GLOBE.get(), 1, " G ", "GSG", " P ")
                .define('G', Items.GLASS).define('S', Items.SNOWBALL).define('P', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_snowball", FurnitureRecipeProvider.hasItem(Items.SNOWBALL)).save(output);
        recipes.shaped(GRAVESTONE.get(), 1, " S ", "SSS", "SSS")
                .define('S', Items.STONE)
                .unlockedBy("has_stone", FurnitureRecipeProvider.hasItem(Items.STONE)).save(output);
        recipes.shaped(FAKE_COBWEB.get(), 1, "T T", " T ", "T T")
                .define('T', Items.STRING)
                .unlockedBy("has_string", FurnitureRecipeProvider.hasItem(Items.STRING)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, HANGING_BATS.get())
                .requires(Items.BLACK_WOOL).requires(Items.STRING).requires(Items.LEATHER)
                .unlockedBy("has_leather", FurnitureRecipeProvider.hasItem(Items.LEATHER)).save(output);
        recipes.shaped(CANDY_BOWL.get(), 1, "T T", " T ")
                .define('T', Items.ORANGE_TERRACOTTA)
                .unlockedBy("has_orange_terracotta", FurnitureRecipeProvider.hasItem(Items.ORANGE_TERRACOTTA)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, WITCH_CAULDRON.get())
                .requires(Items.CAULDRON).requires(Items.SLIME_BALL).requires(Items.GLOWSTONE_DUST)
                .unlockedBy("has_cauldron", FurnitureRecipeProvider.hasItem(Items.CAULDRON)).save(output);
        for (DyeColor color : DyeColor.values()) {
            Item dye = DyeItem.byColor(color);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, PRESENT.get(color).get())
                    .requires(Items.CHEST).requires(Items.PAPER).requires(Items.PAPER).requires(dye).requires(Items.STRING)
                    .unlockedBy("has_paper", FurnitureRecipeProvider.hasItem(Items.PAPER)).save(output);
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(STOCKING.get(color).get(), 1, "C ", "W ", "WW")
                    .define('C', Items.WHITE_CARPET).define('W', wool)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        tags.tagFor(FurnitureTags.LAMPS).add(CHRISTMAS_TREE.get(), WARM_STRING_LIGHTS.get(), COLORFUL_STRING_LIGHTS.get());
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("block.reikos_furniture.present.full", "Something is inside...");
    }
}
