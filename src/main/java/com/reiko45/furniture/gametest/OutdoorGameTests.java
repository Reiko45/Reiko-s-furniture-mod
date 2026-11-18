package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.outdoor.OutdoorFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.common.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class OutdoorGameTests {
    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 400)
    public static void grillCooksFood(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, OutdoorFurniture.GRILL.get().defaultBlockState().setValue(SwitchableFurnitureBlock.LIT, true));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BEEF, 2));
        helper.useBlock(pos, player);
        helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "one beef should be on the grill");
        helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.COOKED_BEEF, pos, 2.0));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void cropsCanGrowInPlanterBox(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, OutdoorFurniture.PLANTER_BOX.get(FurnitureWood.OAK).get());
        var soil = helper.getBlockState(pos);
        helper.assertTrue(soil.canSustainPlant(helper.getLevel(), helper.absolutePos(pos), Direction.UP,
                Blocks.WHEAT.defaultBlockState()) == TriState.TRUE, "wheat should be plantable");
        helper.assertTrue(Blocks.WHEAT.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(pos.above())),
                "wheat should survive on the planter box");
        helper.assertTrue(Blocks.POPPY.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(pos.above())),
                "flowers should survive on the planter box");
        helper.succeed();
    }
}
