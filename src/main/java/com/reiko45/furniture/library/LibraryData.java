package com.reiko45.furniture.library;

import static com.reiko45.furniture.library.LibraryFurniture.BOOK_STAND;
import static com.reiko45.furniture.library.LibraryFurniture.GLOBE;
import static com.reiko45.furniture.library.LibraryFurniture.LIBRARY_LADDER;
import static com.reiko45.furniture.library.LibraryFurniture.READING_LAMP;
import static com.reiko45.furniture.library.LibraryFurniture.TALL_BOOKCASE;
import static com.reiko45.furniture.library.LibraryFurniture.WRITING_DESK;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models, recipes and tags for the library. P = planks, S = wooden slab, B = book, M = map, F = feather,
 * C = chest, G = green stained glass, T = torch.
 */
public class LibraryData implements RoomData {
    private static final Room ROOM = Room.LIBRARY;
    /** The globe's ball is drawn at these extra angles; the blockstate adds quarter turns. */
    private static final String[] GLOBE_MODELS = {"globe_0", "globe_22", "globe_45", "globe_m22"};

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodTallOpenable(TALL_BOOKCASE, "tall_bookcase");
        models.woodFacing(LIBRARY_LADDER, "library_ladder");
        models.woodFacing(BOOK_STAND, "book_stand");
        models.woodFacingOpenable(WRITING_DESK, "writing_desk");
        models.singleFacingLit(READING_LAMP.get(), ROOM, "reading_lamp");
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block globe = GLOBE.get(wood).get();
            String name = BuiltInRegistries.BLOCK.getKey(globe).getPath();
            ModelFile[] files = new ModelFile[4];
            for (int i = 0; i < 4; i++) {
                String suffix = i == 0 ? "" : GLOBE_MODELS[i].substring("globe".length());
                files[i] = models.woodModel(name + suffix, models.template(ROOM, GLOBE_MODELS[i]), wood);
            }
            // Angle k * 22.5 = quarter turns q * 90 plus the ball's own turn: 0, 22.5, 45 or -22.5 (then one more quarter).
            models.getVariantBuilder(globe).forAllStatesExcept(state -> {
                int k = state.getValue(GlobeBlock.ANGLE);
                int m = k % 4;
                int quarters = k / 4 + (m == 3 ? 1 : 0);
                return ConfiguredModel.builder().modelFile(files[m]).rotationY((quarters % 4) * 90).build();
            }, FurnitureBlock.FACING, FurnitureBlock.WATERLOGGED, GlobeBlock.SPEED);
            models.blockItem(globe, models.modLoc("block/" + name));
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, TALL_BOOKCASE, 1, wood -> Map.of('B', Ingredient.of(Items.BOOK)), "PBP", "PBP", "PBP");
        recipes.woodShaped(output, LIBRARY_LADDER, 3, wood -> Map.of(), "S S", "SSS", "S S");
        recipes.woodShaped(output, GLOBE, 1, wood -> Map.of('M', Ingredient.of(Items.MAP)), "M", "S", "P");
        recipes.woodShaped(output, BOOK_STAND, 1, wood -> Map.of('B', Ingredient.of(Items.BOOK)), "B", "S");
        recipes.woodShaped(output, WRITING_DESK, 1,
                wood -> Map.of('F', Ingredient.of(Items.FEATHER), 'C', Ingredient.of(Items.CHEST)), "  F", "PPP", "PCP");
        recipes.shaped(READING_LAMP.get(), 1, "GGG", " T ", "III")
                .define('G', Items.GREEN_STAINED_GLASS).define('T', Items.TORCH).define('I', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", FurnitureRecipeProvider.hasItem(Items.GOLD_INGOT)).save(output);
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        TALL_BOOKCASE.all().forEach(block -> tags.tagFor(BlockTags.ENCHANTMENT_POWER_PROVIDER).add(block.get()));
        LIBRARY_LADDER.all().forEach(block -> tags.tagFor(BlockTags.CLIMBABLE).add(block.get()));
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("message.reikos_furniture.ladder_cannot_slide", "There's nothing to lean on there");
    }
}
