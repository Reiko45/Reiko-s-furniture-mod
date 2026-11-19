package com.reiko45.furniture.theater;

import static com.reiko45.furniture.theater.TheaterFurniture.MOVIE_POSTER;
import static com.reiko45.furniture.theater.TheaterFurniture.POPCORN;
import static com.reiko45.furniture.theater.TheaterFurniture.POPCORN_MACHINE;
import static com.reiko45.furniture.theater.TheaterFurniture.PROJECTOR;
import static com.reiko45.furniture.theater.TheaterFurniture.PROJECTOR_SCREEN;
import static com.reiko45.furniture.theater.TheaterFurniture.RECLINER;
import static com.reiko45.furniture.theater.TheaterFurniture.SPEAKER;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceRecipe;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import java.util.EnumMap;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models, recipes and texts for the home theater. W = wool, P = planks, I = iron ingot, N = iron nugget,
 * G = glass, R = redstone or red dye, C = black carpet.
 */
public class TheaterData implements RoomData {
    private static final Room ROOM = Room.THEATER;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        // The screen: one model per part, blank or showing one of the films.
        Map<ProjectorScreenBlock.Part, ModelFile[]> screen = new EnumMap<>(ProjectorScreenBlock.Part.class);
        for (ProjectorScreenBlock.Part part : ProjectorScreenBlock.Part.values()) {
            ModelFile[] films = new ModelFile[ProjectorScreenBlock.FILMS + 1];
            String name = "projector_screen_" + part.getSerializedName();
            films[0] = models.existingTemplate(ROOM, name);
            for (int film = 1; film <= ProjectorScreenBlock.FILMS; film++) {
                films[film] = models.existingTemplate(ROOM, name + "_film_" + film);
            }
            screen.put(part, films);
        }
        models.facingBlock(PROJECTOR_SCREEN.get(), state -> screen.get(state.getValue(ProjectorScreenBlock.PART))
                [state.getValue(ProjectorScreenBlock.LIT) ? state.getValue(ProjectorScreenBlock.FILM) : 0]);
        models.blockItem(PROJECTOR_SCREEN.get(), models.template(ROOM, "projector_screen_item"));

        ModelFile projectorOff = models.existingTemplate(ROOM, "projector");
        ModelFile projectorOn = models.existingTemplate(ROOM, "projector_on");
        models.facingBlock(PROJECTOR.get(), state -> state.getValue(ProjectorBlock.LIT) ? projectorOn : projectorOff,
                ProjectorBlock.FILM);
        models.blockItem(PROJECTOR.get(), models.template(ROOM, "projector"));

        for (DyeColor color : DyeColor.values()) {
            Block recliner = RECLINER.get(color).get();
            String name = BuiltInRegistries.BLOCK.getKey(recliner).getPath();
            ModelFile upright = models.woolModel(name, models.template(ROOM, "recliner"), color);
            ModelFile reclined = models.woolModel(name + "_reclined", models.template(ROOM, "recliner_reclined"), color);
            models.facingBlock(recliner, state -> state.getValue(ReclinerBlock.RECLINED) ? reclined : upright);
            models.blockItem(recliner, models.modLoc("block/" + name));
        }
        models.woodFacing(SPEAKER, "speaker");

        ModelFile machineOff = models.existingTemplate(ROOM, "popcorn_machine");
        ModelFile machineOn = models.existingTemplate(ROOM, "popcorn_machine_on");
        models.facingBlock(POPCORN_MACHINE.get(), state -> state.getValue(ApplianceBlock.RUNNING) ? machineOn : machineOff);
        models.blockItem(POPCORN_MACHINE.get(), models.template(ROOM, "popcorn_machine"));

        ModelFile[] posters = new ModelFile[MoviePosterBlock.DESIGNS];
        for (int design = 0; design < posters.length; design++) {
            posters[design] = models.existingTemplate(ROOM, "movie_poster_" + design);
        }
        models.facingBlock(MOVIE_POSTER.get(), state -> posters[state.getValue(MoviePosterBlock.DESIGN)]);
        models.blockItem(MOVIE_POSTER.get(), models.template(ROOM, "movie_poster_0"));

        models.itemModels().basicItem(POPCORN.get());
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.shaped(PROJECTOR_SCREEN.get(), 1, "NSN", "WWW", "WWW")
                .define('N', Items.IRON_NUGGET).define('S', Items.STICK).define('W', Items.WHITE_WOOL)
                .unlockedBy("has_white_wool", FurnitureRecipeProvider.hasItem(Items.WHITE_WOOL)).save(output);
        recipes.shaped(PROJECTOR.get(), 1, "IIG", "IRI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS).define('R', Items.REDSTONE)
                .unlockedBy("has_redstone", FurnitureRecipeProvider.hasItem(Items.REDSTONE)).save(output);
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(RECLINER.get(color).get(), 1, "W L", "WWW", "P P")
                    .define('W', wool).define('L', Items.LEVER).define('P', ItemTags.PLANKS)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
        recipes.woodShaped(output, SPEAKER, 2,
                wood -> Map.of('C', Ingredient.of(Items.BLACK_CARPET), 'N', Ingredient.of(Items.NOTE_BLOCK)), "PCP", "PNP", "PCP");
        recipes.shaped(POPCORN_MACHINE.get(), 1, "GGG", "G G", "RIR")
                .define('G', Items.GLASS).define('R', Items.RED_DYE).define('I', Items.IRON_INGOT)
                .unlockedBy("has_glass", FurnitureRecipeProvider.hasItem(Items.GLASS)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, MOVIE_POSTER.get())
                .requires(Items.PAPER).requires(Items.PAPER).requires(Items.PAPER).requires(Items.INK_SAC).requires(Items.RED_DYE)
                .unlockedBy("has_paper", FurnitureRecipeProvider.hasItem(Items.PAPER))
                .save(output);
        Ingredient seeds = Ingredient.of(Items.WHEAT_SEEDS);
        output.accept(ReikosFurniture.id("appliance/popcorn"), new ApplianceRecipe(ApplianceType.POPCORN_MACHINE.id(),
                List.of(seeds, seeds, Ingredient.of(Items.PAPER)), new ItemStack(POPCORN.get(), 2), 100), null);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept(POPCORN.get().getDescriptionId(), "Popcorn");
        add.accept("message.reikos_furniture.projector_no_screen", "There is no projector screen in front of the lens");
    }
}
