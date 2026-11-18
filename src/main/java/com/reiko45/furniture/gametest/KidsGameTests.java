package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.kids.BunkBedBlock;
import com.reiko45.furniture.kids.BunkTopBlock;
import com.reiko45.furniture.kids.KidsFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class KidsGameTests {
    /** Places a bunk bed the way the item does: the foot, then setPlacedBy adds the head and both top bunks. */
    private static BlockPos placeBunkBed(GameTestHelper helper, BunkBedBlock bunk) {
        BlockPos foot = new BlockPos(2, 1, 3);
        BlockState state = bunk.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
        helper.setBlock(foot, state);
        bunk.setPlacedBy(helper.getLevel(), helper.absolutePos(foot), state, null, ItemStack.EMPTY);
        return foot;
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void bunkBedPlacesAndRemovesTopBunk(GameTestHelper helper) {
        BunkBedBlock bunk = KidsFurniture.BUNK_BED.get(FurnitureWood.BIRCH).get();
        BlockPos foot = placeBunkBed(helper, bunk);
        BlockPos head = foot.north();
        helper.assertBlockProperty(head, BedBlock.PART, BedPart.HEAD);
        helper.assertTrue(helper.getBlockState(foot.above()).getBlock() instanceof BunkTopBlock, "top bunk over the foot");
        helper.assertTrue(helper.getBlockState(head.above()).getBlock() instanceof BunkTopBlock, "top bunk over the head");

        // Dyeing the bed recolors the top bunk too.
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LIME_DYE));
        helper.useBlock(foot, player);
        helper.assertBlockProperty(head.above(), FurnitureBedBlock.COLOR, DyeColor.LIME);

        // Breaking a top bunk removes the whole bunk bed and drops it once.
        helper.destroyBlock(head.above());
        helper.assertBlockNotPresent(bunk, foot);
        helper.assertBlockNotPresent(bunk, head);
        helper.assertTrue(helper.getBlockState(foot.above()).isAir(), "other top bunk should be gone");
        helper.assertItemEntityCountIs(bunk.asItem(), foot, 3.0, 1);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void rockingHorseCanBeRidden(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, KidsFurniture.ROCKING_HORSE.get(FurnitureWood.CHERRY).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(pos, player);
        helper.assertTrue(player.isPassenger(), "player should sit on the rocking horse");
        helper.succeed();
    }
}
