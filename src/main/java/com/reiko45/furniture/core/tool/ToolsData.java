package com.reiko45.furniture.core.tool;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

/** Item models, recipes and names for the furniture tools. */
public class ToolsData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.itemModels().withExistingParent(Tools.HAMMER.getId().getPath(), "item/handheld")
                .texture("layer0", ReikosFurniture.id("item/hammer"));
        var loaded = models.itemModels().basicItem(ReikosFurniture.id("moving_dolly_loaded"));
        models.itemModels().basicItem(Tools.MOVING_DOLLY.get())
                .override().predicate(ReikosFurniture.id("loaded"), 1).model(loaded).end();
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.shaped(Tools.HAMMER.get(), 1, "IPI", " S ", " S ")
                .define('I', Items.IRON_INGOT).define('P', ItemTags.PLANKS).define('S', Items.STICK)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        recipes.shaped(Tools.MOVING_DOLLY.get(), 1, "S  ", "S  ", "INI")
                .define('S', Items.STICK).define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("item.reikos_furniture.hammer", "Furniture Hammer");
        add.accept("item.reikos_furniture.hammer.tooltip", "Right-click furniture to turn it (sneak: the other way)");
        add.accept("item.reikos_furniture.moving_dolly", "Moving Dolly");
        add.accept("item.reikos_furniture.moving_dolly.tooltip", "Right-click storage furniture to move it with its contents");
        add.accept("item.reikos_furniture.moving_dolly.carrying", "Carrying: %s");
        add.accept("message.reikos_furniture.hammer_cannot_turn", "This piece can't be turned with the hammer");
        add.accept("message.reikos_furniture.dolly_not_storage", "The dolly only carries storage furniture");
        add.accept("message.reikos_furniture.dolly_not_yours", "This safe isn't yours");
        add.accept("message.reikos_furniture.dolly_not_empty", "Empty it first (the server doesn't allow moving full storage)");
        add.accept("message.reikos_furniture.dolly_no_room", "There's no room for it here");
    }
}
