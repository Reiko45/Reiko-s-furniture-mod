package com.reiko45.furniture.nursery;

import static com.reiko45.furniture.nursery.NurseryFurniture.AQUARIUM;
import static com.reiko45.furniture.nursery.NurseryFurniture.BABY_MOBILE;
import static com.reiko45.furniture.nursery.NurseryFurniture.BIRD_CAGE;
import static com.reiko45.furniture.nursery.NurseryFurniture.CHANGING_TABLE;
import static com.reiko45.furniture.nursery.NurseryFurniture.CRIB;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.library.GlobeBlock;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models and recipes for the nursery and hobby room. P = planks, S = wooden slab, W = wool, C = chest, G = glass. */
public class NurseryData implements RoomData {
    private static final Room ROOM = Room.NURSERY;
    private static final String[] MOBILE_MODELS = {"baby_mobile_0", "baby_mobile_22", "baby_mobile_45", "baby_mobile_m22"};

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacing(CRIB, "crib");
        models.woodFacingOpenable(CHANGING_TABLE, "changing_table");
        models.singleFacing(AQUARIUM.get(), ROOM, "aquarium");
        models.singleFacing(BIRD_CAGE.get(), ROOM, "bird_cage");
        for (DyeColor color : DyeColor.values()) {
            Block mobile = BABY_MOBILE.get(color).get();
            String name = BuiltInRegistries.BLOCK.getKey(mobile).getPath();
            ModelFile[] files = new ModelFile[4];
            for (int i = 0; i < 4; i++) {
                String suffix = i == 0 ? "" : MOBILE_MODELS[i].substring("baby_mobile".length());
                files[i] = models.woolModel(name + suffix, models.template(ROOM, MOBILE_MODELS[i]), color);
            }
            // Same turning as the globe: quarter turns plus the arms' own 0, 22.5, 45 or -22.5 degrees.
            models.getVariantBuilder(mobile).forAllStatesExcept(state -> {
                int k = state.getValue(GlobeBlock.ANGLE);
                int m = k % 4;
                int quarters = k / 4 + (m == 3 ? 1 : 0);
                return ConfiguredModel.builder().modelFile(files[m]).rotationY((quarters % 4) * 90).build();
            }, FurnitureBlock.FACING, FurnitureBlock.WATERLOGGED, GlobeBlock.SPEED);
            models.blockItem(mobile, models.modLoc("block/" + name));
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, CRIB, 1, wood -> Map.of('W', Ingredient.of(Items.WHITE_WOOL)), "S S", "SWS", "PPP");
        recipes.woodShaped(output, CHANGING_TABLE, 1,
                wood -> Map.of('W', Ingredient.of(Items.WHITE_WOOL), 'C', Ingredient.of(Items.CHEST)), "WWW", "PCP");
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(BABY_MOBILE.get(color).get(), 1, " N ", "TWT")
                    .define('N', Items.IRON_NUGGET).define('T', Items.STRING).define('W', wool)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
        recipes.shaped(AQUARIUM.get(), 1, "GGG", "GBG", "PPP")
                .define('G', Items.GLASS).define('B', Items.WATER_BUCKET).define('P', net.minecraft.tags.ItemTags.PLANKS)
                .unlockedBy("has_glass", FurnitureRecipeProvider.hasItem(Items.GLASS)).save(output);
        recipes.shaped(BIRD_CAGE.get(), 1, " N ", "N N", "NIN")
                .define('N', Items.GOLD_NUGGET).define('I', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", FurnitureRecipeProvider.hasItem(Items.GOLD_INGOT)).save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("message.reikos_furniture.bird_cage_empty", "Bring a parrot on your shoulder to put it in");
    }
}
