package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.modded.WoodResources;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.music.MusicFurniture;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for furniture in other mods' woods. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class ModdedWoodGameTests {
    private static final WoodResources.Swap FIR = new WoodResources.Swap("somemod_fir", "Fir", "somemod:fir_planks", "somemod:fir_log",
            "somemod:fir_slab", "somemod:block/fir_planks", "somemod:block/fir_log", "somemod:block/stripped_fir_log");

    @GameTest(template = CoreGameTests.EMPTY)
    public static void oakFilesBecomeModdedWoodFiles(GameTestHelper helper) {
        String model = """
                {"parent": "reikos_furniture:block/template/bedroom/nightstand",
                 "textures": {"log": "minecraft:block/oak_log", "planks": "minecraft:block/oak_planks",
                              "stripped_log": "minecraft:block/stripped_oak_log", "frame": "minecraft:block/stripped_dark_oak_log"}}""";
        String swapped = WoodResources.translate(model, FIR);
        helper.assertTrue(swapped.contains("\"somemod:block/fir_planks\"") && swapped.contains("\"somemod:block/fir_log\"")
                && swapped.contains("\"somemod:block/stripped_fir_log\""), "textures swapped: " + swapped);
        helper.assertTrue(swapped.contains("stripped_dark_oak_log") && swapped.contains("template/bedroom/nightstand"),
                "other textures and templates kept: " + swapped);

        String recipe = """
                {"key": {"B": {"item": "reikos_furniture:oak_bed"}, "P": {"item": "minecraft:oak_planks"},
                         "S": {"item": "minecraft:oak_slab"}, "L": {"item": "minecraft:oak_log"}},
                 "result": {"id": "reikos_furniture:oak_bunk_bed"}}""";
        String swappedRecipe = WoodResources.translate(recipe, FIR);
        for (String expected : List.of("reikos_furniture:somemod_fir_bed", "somemod:fir_planks", "somemod:fir_slab", "somemod:fir_log",
                "reikos_furniture:somemod_fir_bunk_bed")) {
            helper.assertTrue(swappedRecipe.contains("\"" + expected + "\""), "recipe should use " + expected + ": " + swappedRecipe);
        }
        helper.assertFalse(swappedRecipe.contains("oak"), "no oak left: " + swappedRecipe);

        helper.assertValueEqual(WoodResources.rename("oak_wardrobe_lower_open.json", FIR), "somemod_fir_wardrobe_lower_open.json", "file");
        helper.assertValueEqual(WoodResources.rename("Oak Nightstand", "Fir"), "Fir Nightstand", "name");
        helper.assertValueEqual(WoodResources.translate("{\"name\": \"reikos_furniture:blocks/oak_desk\"}", FIR),
                "{\"name\": \"reikos_furniture:blocks/somemod_fir_desk\"}", "loot table id");
        helper.succeed();
    }

    /** Only does something when a mod with its own wood (like Biomes O' Plenty) is installed. */
    @GameTest(template = CoreGameTests.EMPTY)
    public static void moddedWoodFurnitureWorks(GameTestHelper helper) {
        if (FurnitureWood.modded().isEmpty()) {
            helper.succeed();
            return;
        }
        for (FurnitureWood wood : FurnitureWood.modded()) {
            Block nightstand = BedroomFurniture.NIGHTSTAND.get(wood).get();
            String name = BuiltInRegistries.BLOCK.getKey(nightstand).getPath();
            helper.assertValueEqual(name, wood.id() + "_nightstand", "block name");
            helper.assertTrue(nightstand.asItem() != net.minecraft.world.item.Items.AIR, name + " has an item");
            helper.assertTrue(nightstand.defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), name + " is mined with an axe");

            LootTable loot = helper.getLevel().getServer().reloadableRegistries().getLootTable(nightstand.getLootTable());
            helper.assertTrue(loot != LootTable.EMPTY, name + " has a loot table");
            RecipeHolder<?> recipe = helper.getLevel().getRecipeManager().byKey(ReikosFurniture.id(name))
                    .orElseThrow(() -> new net.minecraft.gametest.framework.GameTestAssertException(name + " has no recipe"));
            helper.assertTrue(recipe.value().getResultItem(helper.getLevel().registryAccess()).is(nightstand.asItem()), "recipe result");
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(ReikosFurniture.id("carpentry/" + name)).isPresent(),
                    name + " has a workbench recipe");
        }
        FurnitureWood wood = FurnitureWood.modded().get(0);
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BedroomFurniture.NIGHTSTAND.get(wood).get());
        helper.assertTrue(helper.getBlockEntity(pos) instanceof FurnitureStorageBlockEntity, "nightstand storage");
        helper.setBlock(pos.east(), LivingRoomFurniture.WALL_SHELF.get(wood).get());
        helper.assertTrue(helper.getBlockEntity(pos.east()) instanceof DisplayBlockEntity, "shelf display");
        helper.setBlock(pos.east(2), MusicFurniture.RECORD_PLAYER.get(wood).get());
        helper.assertTrue(helper.getBlockEntity(pos.east(2)) instanceof JukeboxBlockEntity, "record player jukebox");
        helper.assertTrue(FurnitureRegistry.moddedItems().size() >= FurnitureWood.modded().size(), "items in the creative tab");
        helper.succeed();
    }
}
