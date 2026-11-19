package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import com.reiko45.furniture.core.lighting.Lighting;
import com.reiko45.furniture.core.registry.ModCreativeTab;
import com.reiko45.furniture.dining.DiningFurniture;
import com.reiko45.furniture.kids.BunkBedBlock;
import com.reiko45.furniture.kids.BunkTopBlock;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.livingroom.SofaBlock;
import com.reiko45.furniture.styles.StyleFurniture;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class StyleGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // the mock server player is still the way to get a player that can sleep
    public static void styleBedsCanBeSleptInAndDyed(GameTestHelper helper) {
        int x = 1;
        for (Style style : Style.values()) {
            FurnitureBedBlock bed = StyleFurniture.get(style).bed().get(FurnitureWood.SPRUCE).get();
            BlockPos foot = new BlockPos(x, 1, 3);
            BlockState state = bed.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
            helper.setBlock(foot, state);
            helper.setBlock(foot.north(), state.setValue(BedBlock.PART, BedPart.HEAD));
            helper.assertTrue(helper.getBlockState(foot).isBed(helper.getLevel(), helper.absolutePos(foot), null),
                    style.id() + " bed should be a bed");

            Player dyer = helper.makeMockPlayer(GameType.SURVIVAL);
            dyer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CYAN_DYE));
            helper.useBlock(foot, dyer);
            helper.assertBlockProperty(foot.north(), FurnitureBedBlock.COLOR, DyeColor.CYAN);

            ServerPlayer sleeper = helper.makeMockServerPlayerInLevel();
            sleeper.startSleeping(helper.absolutePos(foot.north()));
            helper.assertTrue(sleeper.isSleeping(), style.id() + " bed: the player should be asleep");
            helper.assertBlockProperty(foot.north(), BedBlock.OCCUPIED, true);
            sleeper.stopSleeping();
            x += 2;
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void styleBunkBedPlacesTopBunk(GameTestHelper helper) {
        BunkBedBlock bunk = StyleFurniture.get(Style.JAPANESE).bunkBed().get(FurnitureWood.CHERRY).get();
        BlockPos foot = new BlockPos(2, 1, 3);
        BlockState state = bunk.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
        helper.setBlock(foot, state);
        bunk.setPlacedBy(helper.getLevel(), helper.absolutePos(foot), state, null, ItemStack.EMPTY);
        BunkTopBlock top = StyleFurniture.get(Style.JAPANESE).bunkBedTop().get(FurnitureWood.CHERRY).get();
        helper.assertBlockPresent(top, foot.above());
        helper.assertBlockPresent(top, foot.north().above());
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void styleSofasOnlyJoinTheirOwnStyle(GameTestHelper helper) {
        Block modernRed = StyleFurniture.get(Style.MODERN).sofa().get(DyeColor.RED).get();
        Block modernBlue = StyleFurniture.get(Style.MODERN).sofa().get(DyeColor.BLUE).get();
        Block classic = LivingRoomFurniture.SOFA.get(DyeColor.RED).get();
        BlockPos left = new BlockPos(1, 1, 1);
        CoreGameTests.placeFacing(helper, left, modernRed, Direction.NORTH);
        CoreGameTests.placeFacing(helper, left.east(), modernBlue, Direction.NORTH);
        helper.assertBlockProperty(left, SofaBlock.PART, SofaBlock.Part.LEFT);
        helper.assertBlockProperty(left.east(), SofaBlock.PART, SofaBlock.Part.RIGHT);

        // A classic sofa next to it stays on its own, and the modern sofa keeps its armrest.
        CoreGameTests.placeFacing(helper, left.east(2), classic, Direction.NORTH);
        helper.assertBlockProperty(left.east(), SofaBlock.PART, SofaBlock.Part.RIGHT);
        helper.assertBlockProperty(left.east(2), SofaBlock.PART, SofaBlock.Part.SINGLE);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stylePiecesAreCraftedFromClassicOnes(GameTestHelper helper) {
        check(helper, StyleFurniture.get(Style.RUSTIC).bed().get(FurnitureWood.OAK).get(),
                new ItemStack(BedroomFurniture.BED.get(FurnitureWood.OAK).get()), new ItemStack(Items.BIRCH_LOG));
        check(helper, StyleFurniture.get(Style.MODERN).dresser().get(FurnitureWood.ACACIA).get(),
                new ItemStack(BedroomFurniture.DRESSER.get(FurnitureWood.ACACIA).get()), new ItemStack(Items.IRON_NUGGET));
        check(helper, StyleFurniture.get(Style.ROYAL).armchair().get(DyeColor.PURPLE).get(),
                new ItemStack(LivingRoomFurniture.ARMCHAIR.get(DyeColor.PURPLE).get()), new ItemStack(Items.GOLD_NUGGET));
        check(helper, StyleFurniture.get(Style.JAPANESE).wardrobe().get(FurnitureWood.BAMBOO).get(),
                new ItemStack(BedroomFurniture.WARDROBE.get(FurnitureWood.BAMBOO).get()), new ItemStack(Items.BAMBOO));
        check(helper, StyleFurniture.get(Style.MEDIEVAL).bed().get(FurnitureWood.DARK_OAK).get(),
                new ItemStack(BedroomFurniture.BED.get(FurnitureWood.DARK_OAK).get()), new ItemStack(Items.IRON_BARS));
        check(helper, StyleFurniture.get(Style.VICTORIAN).nightstand().get(FurnitureWood.CHERRY).get(),
                new ItemStack(BedroomFurniture.NIGHTSTAND.get(FurnitureWood.CHERRY).get()), new ItemStack(Items.PINK_CANDLE));
        check(helper, StyleFurniture.get(Style.FUTURISTIC).sofa().get(DyeColor.CYAN).get(),
                new ItemStack(LivingRoomFurniture.SOFA.get(DyeColor.CYAN).get()), new ItemStack(Items.GLOWSTONE_DUST));
        check(helper, StyleFurniture.get(Style.BEACH).diningChair().get(FurnitureWood.BIRCH).get(),
                new ItemStack(DiningFurniture.DINING_CHAIR.get(FurnitureWood.BIRCH).get()), new ItemStack(Items.RED_SAND));
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void styleCountersFormCorners(GameTestHelper helper) {
        Block counter = StyleFurniture.get(Style.VICTORIAN).counter().get(FurnitureWood.SPRUCE).get();
        BlockPos inner = new BlockPos(1, 1, 2);
        CoreGameTests.placeFacing(helper, inner, counter, Direction.NORTH);
        CoreGameTests.placeFacing(helper, inner.north(), counter, Direction.EAST);
        helper.assertBlockProperty(inner, CounterBlock.SHAPE, CounterBlock.Shape.INNER);
        BlockPos outer = new BlockPos(4, 1, 2);
        CoreGameTests.placeFacing(helper, outer, StyleFurniture.get(Style.FUTURISTIC).counter().get(FurnitureWood.BIRCH).get(), Direction.NORTH);
        CoreGameTests.placeFacing(helper, outer.south(), StyleFurniture.get(Style.FUTURISTIC).counter().get(FurnitureWood.BIRCH).get(), Direction.WEST);
        helper.assertBlockProperty(outer, CounterBlock.SHAPE, CounterBlock.Shape.OUTER_LEFT);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void styleBookshelvesAndLampsWork(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        for (Style style : Style.values()) {
            StyleFurniture.Pieces pieces = StyleFurniture.get(style);
            helper.setBlock(pos, pieces.bookshelf().get(FurnitureWood.OAK).get());
            helper.assertValueEqual(helper.getBlockState(pos).getEnchantPowerBonus(helper.getLevel(), helper.absolutePos(pos)), 1.0F,
                    style.id() + " bookshelf powers enchanting");
            helper.assertTrue(Lighting.isLamp(pieces.tableLamp().get(FurnitureWood.OAK).get().defaultBlockState()),
                    style.id() + " table lamp is a lamp");
            helper.assertTrue(Lighting.isLamp(pieces.floorLamp().get(FurnitureWood.OAK).get().defaultBlockState()),
                    style.id() + " floor lamp is a lamp");
        }
        check(helper, StyleFurniture.get(Style.MEDIEVAL).floorLamp().get(FurnitureWood.OAK).get(),
                new ItemStack(LivingRoomFurniture.FLOOR_LAMP.get(FurnitureWood.OAK).get()), new ItemStack(Items.IRON_BARS));
        check(helper, StyleFurniture.get(Style.BEACH).upperCabinet().get(FurnitureWood.JUNGLE).get(),
                new ItemStack(KitchenFurniture.UPPER_CABINET.get(FurnitureWood.JUNGLE).get()), new ItemStack(Items.SAND));
        helper.succeed();
    }

    /** The classic piece and the material make the style piece in a crafting grid. */
    private static void check(GameTestHelper helper, Block result, ItemStack classic, ItemStack material) {
        CraftingInput input = CraftingInput.of(2, 1, List.of(material, classic));
        ItemStack crafted = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel())
                .map(recipe -> recipe.value().assemble(input, helper.getLevel().registryAccess()))
                .orElse(ItemStack.EMPTY);
        helper.assertTrue(crafted.is(result.asItem()), "expected " + BuiltInRegistries.BLOCK.getKey(result) + " but got " + crafted);
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void styleStorageHoldsItems(GameTestHelper helper) {
        FurnitureStorageBlock dresser = StyleFurniture.get(Style.JAPANESE).dresser().get(FurnitureWood.DARK_OAK).get();
        BlockPos pos = new BlockPos(1, 1, 1);
        CoreGameTests.placeFacing(helper, pos, dresser, Direction.NORTH);
        FurnitureStorageBlockEntity entity = helper.getBlockEntity(pos);
        helper.assertValueEqual(entity.getContainerSize(), 27, "the step tansu has three rows");
        entity.setItem(0, new ItemStack(Items.PAPER, 5));
        entity.startOpen(helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, FurnitureStorageBlock.OPEN, true);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stylePiecesHaveTheirOwnTab(GameTestHelper helper) {
        CreativeModeTabs.tryRebuildTabContents(helper.getLevel().enabledFeatures(), false, helper.getLevel().registryAccess());
        long styled = ModCreativeTab.STYLES.get().getDisplayItems().stream()
                .filter(stack -> stack.getItem() instanceof FurnitureBlockItem item && item.style() != null).count();
        helper.assertTrue(styled >= Style.values().length * 120, "the styles tab should list every style piece, has " + styled);
        helper.assertTrue(ModCreativeTab.STYLES.get().getDisplayItems().size() == styled, "only style pieces in the styles tab");
        helper.assertTrue(ModCreativeTab.FURNITURE.get().getDisplayItems().stream()
                        .noneMatch(stack -> stack.getItem() instanceof FurnitureBlockItem item && item.style() != null),
                "no style pieces in the main tab");
        helper.assertValueEqual(ModCreativeTab.STYLES.get().getDisplayItems().iterator().next().getItem(),
                StyleFurniture.get(Style.RUSTIC).bed().get(FurnitureWood.OAK).get().asItem(), "the tab starts with the rustic pieces");
        helper.succeed();
    }
}
