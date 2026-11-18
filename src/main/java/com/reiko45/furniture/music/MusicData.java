package com.reiko45.furniture.music;

import static com.reiko45.furniture.music.MusicFurniture.DRUM_KIT;
import static com.reiko45.furniture.music.MusicFurniture.PIANO;
import static com.reiko45.furniture.music.MusicFurniture.PIANO_BENCH;
import static com.reiko45.furniture.music.MusicFurniture.RECORD_PLAYER;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JukeboxBlock;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models and recipes for the music room. P = planks, S = wooden slab, N = note block, J = jukebox, G = gold nugget. */
public class MusicData implements RoomData {
    private static final Room ROOM = Room.MUSIC;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.woodFacing(PIANO, "piano");
        models.woodFacing(PIANO_BENCH, "piano_bench");
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block block = RECORD_PLAYER.get(wood).get();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ModelFile empty = models.woodModel(name, models.template(ROOM, "record_player"), wood);
            ModelFile playing = models.woodModel(name + "_playing", models.template(ROOM, "record_player_playing"), wood);
            models.facingBlock(block, state -> state.getValue(JukeboxBlock.HAS_RECORD) ? playing : empty);
            models.blockItem(block, models.modLoc("block/" + name));
        }
        models.singleFacing(DRUM_KIT.get(), ROOM, "drum_kit");
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, PIANO, 1, wood -> Map.of('N', Ingredient.of(Items.NOTE_BLOCK)), "PPP", "PNP", "P P");
        recipes.woodShaped(output, PIANO_BENCH, 1, wood -> Map.of(), "PPP", "S S");
        recipes.woodShaped(output, RECORD_PLAYER, 1, wood -> Map.of('J', Ingredient.of(Items.JUKEBOX)), "PPP", "PJP");
        recipes.shaped(DRUM_KIT.get(), 1, "GNG", "NNN")
                .define('G', Items.GOLD_NUGGET).define('N', Items.NOTE_BLOCK)
                .unlockedBy("has_note_block", FurnitureRecipeProvider.hasItem(Items.NOTE_BLOCK)).save(output);
    }
}
