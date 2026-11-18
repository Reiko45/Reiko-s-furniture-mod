package com.reiko45.furniture.office;

import static com.reiko45.furniture.office.OfficeFurniture.COMPUTER;
import static com.reiko45.furniture.office.OfficeFurniture.DESK_LAMP;
import static com.reiko45.furniture.office.OfficeFurniture.FILING_CABINET;
import static com.reiko45.furniture.office.OfficeFurniture.OFFICE_CHAIR;
import static com.reiko45.furniture.office.OfficeFurniture.SAFE;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Models and recipes for the office. P = planks, C = chest, W = wool, N = iron nugget, I = iron ingot,
 * G = glass pane, R = redstone, T = torch.
 */
public class OfficeData implements RoomData {
    private static final Room ROOM = Room.OFFICE;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        models.colorFacing(OFFICE_CHAIR, "office_chair");
        models.woodFacingOpenable(FILING_CABINET, "filing_cabinet");
        models.facingLitBlock(COMPUTER.get(), models.existingTemplate(ROOM, "computer"), models.existingTemplate(ROOM, "computer_on"));
        models.blockItem(COMPUTER.get(), models.template(ROOM, "computer"));
        models.facingLitBlock(DESK_LAMP.get(), models.existingTemplate(ROOM, "desk_lamp"), models.existingTemplate(ROOM, "desk_lamp_on"));
        models.blockItem(DESK_LAMP.get(), models.template(ROOM, "desk_lamp"));
        models.singleFacingOpenable(SAFE.get(), ROOM, "safe");
    }

    @Override
    public void translations(java.util.function.BiConsumer<String, String> add) {
        add.accept("message.reikos_furniture.safe_locked", "This safe belongs to %s");
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
            recipes.shaped(OFFICE_CHAIR.get(color).get(), 1, "W  ", "WWW", "NIN")
                    .define('W', wool).define('N', Items.IRON_NUGGET).define('I', Items.IRON_INGOT)
                    .unlockedBy("has_wool", FurnitureRecipeProvider.hasItem(wool)).save(output);
        }
        recipes.woodShaped(output, FILING_CABINET, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "PCP", "PCP", "PPP");
        recipes.shaped(COMPUTER.get(), 1, "GGG", "GRG", "III")
                .define('G', Items.GLASS_PANE).define('R', Items.REDSTONE).define('I', Items.IRON_INGOT)
                .unlockedBy("has_redstone", FurnitureRecipeProvider.hasItem(Items.REDSTONE)).save(output);
        recipes.shaped(SAFE.get(), 1, "III", "ICI", "IRI")
                .define('I', Items.IRON_INGOT).define('C', Items.CHEST).define('R', Items.REDSTONE)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
        recipes.shaped(DESK_LAMP.get(), 1, "IIT", " I ", "III")
                .define('I', Items.IRON_INGOT).define('T', Items.TORCH)
                .unlockedBy("has_iron_ingot", FurnitureRecipeProvider.hasItem(Items.IRON_INGOT)).save(output);
    }
}
