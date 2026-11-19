package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.repaint.Repaint;
import com.reiko45.furniture.kids.BunkBedBlock;
import com.reiko45.furniture.kids.KidsFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import com.reiko45.furniture.livingroom.SofaBlock;
import com.reiko45.furniture.styles.StyleFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class RepaintGameTests {
    private static Player sneaking(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        return player;
    }

    private static boolean use(GameTestHelper helper, BlockPos pos, Player player, ItemStack stack) {
        return Repaint.use(helper.getLevel(), helper.absolutePos(pos), player, stack);
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void planksChangeTheWoodAndKeepTheContents(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        CoreGameTests.placeFacing(helper, pos, BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get(), Direction.EAST);
        FurnitureStorageBlockEntity storage = helper.getBlockEntity(pos);
        storage.setItem(3, new ItemStack(Items.DIAMOND, 7));
        ItemStack planks = new ItemStack(Items.BIRCH_PLANKS, 2);

        helper.assertTrue(use(helper, pos, sneaking(helper), planks), "planks are a material for wooden pieces");
        helper.assertBlockPresent(BedroomFurniture.NIGHTSTAND.get(FurnitureWood.BIRCH).get(), pos);
        helper.assertBlockProperty(pos, FurnitureStorageBlock.FACING, Direction.EAST);
        FurnitureStorageBlockEntity moved = helper.getBlockEntity(pos);
        helper.assertTrue(moved.getItem(3).is(Items.DIAMOND) && moved.getItem(3).getCount() == 7, "contents kept");
        helper.assertValueEqual(planks.getCount(), 1, "one plank used");
        helper.assertItemEntityCountIs(Items.DIAMOND, pos, 3.0, 0);

        // Without sneaking nothing happens (the click opens the nightstand as usual).
        Player standing = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertFalse(use(helper, pos, standing, new ItemStack(Items.OAK_PLANKS)), "only when sneaking");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tallPiecesChangeBothHalves(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState wardrobe = BedroomFurniture.WARDROBE.get(FurnitureWood.SPRUCE).get().defaultBlockState();
        helper.setBlock(pos, wardrobe);
        helper.setBlock(pos.above(), wardrobe.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        use(helper, pos.above(), sneaking(helper), new ItemStack(Items.CHERRY_PLANKS));
        helper.assertBlockPresent(BedroomFurniture.WARDROBE.get(FurnitureWood.CHERRY).get(), pos);
        helper.assertBlockPresent(BedroomFurniture.WARDROBE.get(FurnitureWood.CHERRY).get(), pos.above());
        helper.assertBlockProperty(pos.above(), TallBlockHelper.HALF, DoubleBlockHalf.UPPER);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void dyeAndWoolRecolorFabric(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        CoreGameTests.placeFacing(helper, pos, LivingRoomFurniture.SOFA.get(DyeColor.RED).get(), Direction.NORTH);
        use(helper, pos, sneaking(helper), new ItemStack(Items.BLUE_DYE));
        helper.assertBlockPresent(LivingRoomFurniture.SOFA.get(DyeColor.BLUE).get(), pos);
        use(helper, pos, sneaking(helper), new ItemStack(Items.LIME_WOOL));
        helper.assertBlockPresent(LivingRoomFurniture.SOFA.get(DyeColor.LIME).get(), pos);
        helper.assertBlockProperty(pos, SofaBlock.FACING, Direction.NORTH);
        // Dye doesn't recolor wooden pieces this way.
        BlockPos table = pos.east(2);
        helper.setBlock(table, BedroomFurniture.DESK.get(FurnitureWood.OAK).get());
        helper.assertFalse(use(helper, table, sneaking(helper), new ItemStack(Items.BLUE_DYE)), "dye is not a wood material");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void materialsTurnPiecesIntoStyles(GameTestHelper helper) {
        BunkBedBlock bunk = KidsFurniture.BUNK_BED.get(FurnitureWood.OAK).get();
        BlockPos foot = new BlockPos(2, 1, 3);
        BlockState state = bunk.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
        helper.setBlock(foot, state);
        bunk.setPlacedBy(helper.getLevel(), helper.absolutePos(foot), state, null, ItemStack.EMPTY);

        use(helper, foot, sneaking(helper), new ItemStack(Items.GOLD_NUGGET));
        StyleFurniture.Pieces royal = StyleFurniture.get(Style.ROYAL);
        helper.assertBlockPresent(royal.bunkBed().get(FurnitureWood.OAK).get(), foot);
        helper.assertBlockPresent(royal.bunkBed().get(FurnitureWood.OAK).get(), foot.north());
        helper.assertBlockPresent(royal.bunkBedTop().get(FurnitureWood.OAK).get(), foot.above());
        helper.assertBlockPresent(royal.bunkBedTop().get(FurnitureWood.OAK).get(), foot.north().above());

        // A style piece can change style again, and its fabric pieces keep their color.
        BlockPos sofa = new BlockPos(5, 1, 1);
        CoreGameTests.placeFacing(helper, sofa, StyleFurniture.get(Style.MODERN).sofa().get(DyeColor.CYAN).get(), Direction.SOUTH);
        use(helper, sofa, sneaking(helper), new ItemStack(Items.BAMBOO));
        helper.assertBlockPresent(StyleFurniture.get(Style.JAPANESE).sofa().get(DyeColor.CYAN).get(), sofa);
        helper.succeed();
    }
}
