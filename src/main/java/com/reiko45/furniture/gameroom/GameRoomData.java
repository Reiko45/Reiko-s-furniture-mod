package com.reiko45.furniture.gameroom;

import static com.reiko45.furniture.gameroom.GameRoomFurniture.ARCADE_CABINET;
import static com.reiko45.furniture.gameroom.GameRoomFurniture.DARTBOARD;
import static com.reiko45.furniture.gameroom.GameRoomFurniture.GAMING_CHAIR;
import static com.reiko45.furniture.gameroom.GameRoomFurniture.POOL_TABLE;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Models and recipes for the game room. P = planks, L = log, C = green carpet, W = wool, N = iron nugget,
 * I = iron ingot, G = glass pane, R = redstone, A = arrow.
 */
public class GameRoomData implements RoomData {
    private static final Room ROOM = Room.GAME_ROOM;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            poolTable(models, POOL_TABLE.get(wood).get(), wood);
        }
        models.colorFacing(GAMING_CHAIR, "gaming_chair");
        models.singleFacing(DARTBOARD.get(), ROOM, "dartboard");

        ModelFile lower = models.existingTemplate(ROOM, "arcade_cabinet_lower");
        ModelFile upper = models.existingTemplate(ROOM, "arcade_cabinet_upper");
        ModelFile upperOn = models.existingTemplate(ROOM, "arcade_cabinet_upper_on");
        models.facingBlock(ARCADE_CABINET.get(), state -> {
            if (state.getValue(TallBlockHelper.HALF) == DoubleBlockHalf.LOWER) {
                return lower;
            }
            return state.getValue(SwitchableFurnitureBlock.LIT) ? upperOn : upper;
        });
        models.blockItem(ARCADE_CABINET.get(), models.template(ROOM, "arcade_cabinet_item"));
    }

    /** Like other tables (legs on open corners), plus a cushion rail on each side that doesn't join another pool table. */
    private static void poolTable(FurnitureBlockStateProvider models, Block block, FurnitureWood wood) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile top = models.woodModel(name + "_top", models.template(ROOM, "pool_table_top"), wood);
        ModelFile leg = models.woodModel(name + "_leg", models.template(ROOM, "pool_table_leg"), wood);
        ModelFile rail = models.woodModel(name + "_rail", models.template(ROOM, "pool_table_rail"), wood);
        models.tableBlock(block, top, leg);
        var builder = models.getMultipartBuilder(block);
        for (Direction side : Direction.Plane.HORIZONTAL) {
            builder.part().modelFile(rail).rotationY(FurnitureBlockStateProvider.yRot(side)).addModel()
                    .condition(ConnectingTableBlock.property(side), false).end();
        }
        models.woodItem(block, ROOM, "pool_table_item", wood);
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, POOL_TABLE, 1, wood -> Map.of('C', Ingredient.of(Items.GREEN_CARPET)), "CCC", "PPP", "L L");
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(GAMING_CHAIR.get(color).get(), 1, "W W", "WWW", "NIN")
                    .define('W', wool).define('N', Items.IRON_NUGGET).define('I', Items.IRON_INGOT)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
        recipes.shaped(ARCADE_CABINET.get(), 1, "IGI", "IRI", "III")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE).define('R', Items.REDSTONE)
                .unlockedBy("has_redstone", FurnitureRecipeProvider.hasItem(Items.REDSTONE)).save(output);
        recipes.shaped(DARTBOARD.get(), 1, "PPP", "PAP", "PPP")
                .define('P', ItemTags.PLANKS).define('A', Items.ARROW)
                .unlockedBy("has_arrow", FurnitureRecipeProvider.hasItem(Items.ARROW)).save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("message.reikos_furniture.arcade_score", "New high score: %s!");
        add.accept("message.reikos_furniture.dart_score", "You scored %s");
    }
}
