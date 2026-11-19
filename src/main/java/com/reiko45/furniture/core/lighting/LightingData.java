package com.reiko45.furniture.core.lighting;

import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.library.LibraryFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.office.OfficeFurniture;
import java.util.function.BiConsumer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Model, recipe, the lamps tag and texts for the light switch. */
public class LightingData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        Block block = Lighting.LIGHT_SWITCH.get();
        ModelFile off = models.existingTemplate(Room.DECOR, "light_switch");
        ModelFile on = models.existingTemplate(Room.DECOR, "light_switch_on");
        models.facingBlock(block, state -> state.getValue(LightSwitchBlock.ON) ? on : off);
        models.blockItem(block, models.template(Room.DECOR, "light_switch"));
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, Lighting.LIGHT_SWITCH.get())
                .requires(Items.LEVER)
                .requires(Items.WHITE_TERRACOTTA)
                .unlockedBy("has_lever", FurnitureRecipeProvider.hasItem(Items.LEVER))
                .save(output);
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        var lamps = tags.tagFor(FurnitureTags.LAMPS);
        BedroomFurniture.TABLE_LAMP.all().forEach(block -> lamps.add(block.get()));
        BedroomFurniture.WALL_LAMP.all().forEach(block -> lamps.add(block.get()));
        LivingRoomFurniture.FLOOR_LAMP.all().forEach(block -> lamps.add(block.get()));
        KitchenFurniture.PENDANT_LAMP.all().forEach(block -> lamps.add(block.get()));
        lamps.add(LivingRoomFurniture.CEILING_LIGHT.get(), DiningFurniture.CHANDELIER.get(), OfficeFurniture.DESK_LAMP.get(),
                LibraryFurniture.READING_LAMP.get());
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("item.reikos_furniture.light_switch.tooltip", "Sneak and right-click lamps to link them (%s linked)");
        add.accept("message.reikos_furniture.lamp_linked", "Lamp linked to the switch (%s of %s)");
        add.accept("message.reikos_furniture.lamp_unlinked", "Lamp unlinked from the switch (%s of %s)");
        add.accept("message.reikos_furniture.switch_full", "This switch already controls %s of %s lamps");
    }
}
