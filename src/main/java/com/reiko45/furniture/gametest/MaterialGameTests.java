package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.lighting.Lighting;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.material.CopperStage;
import com.reiko45.furniture.material.MaterialFurniture;
import com.reiko45.furniture.material.StoneMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class MaterialGameTests {
    private static void useOn(GameTestHelper helper, ServerPlayer player, BlockPos relative, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos pos = helper.absolutePos(relative);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // a mock server player uses the honeycomb and the axe
    public static void copperAgesAndCanBeWaxedAndScraped(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, MaterialFurniture.COPPER_CHAIR.get(CopperStage.UNAFFECTED).get());
        // Random ticks age it (each one has a small chance, like copper blocks).
        for (int i = 0; i < 3000 && helper.getBlockState(pos).is(MaterialFurniture.COPPER_CHAIR.get(CopperStage.UNAFFECTED).get()); i++) {
            helper.getBlockState(pos).randomTick(helper.getLevel(), helper.absolutePos(pos), helper.getLevel().random);
        }
        helper.assertBlockPresent(MaterialFurniture.COPPER_CHAIR.get(CopperStage.EXPOSED).get(), pos);

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        useOn(helper, player, pos, new ItemStack(Items.HONEYCOMB));
        helper.assertBlockPresent(MaterialFurniture.COPPER_CHAIR.get(CopperStage.WAXED_EXPOSED).get(), pos);
        helper.assertFalse(helper.getBlockState(pos).isRandomlyTicking(), "waxed copper doesn't age");

        useOn(helper, player, pos, new ItemStack(Items.IRON_AXE));
        helper.assertBlockPresent(MaterialFurniture.COPPER_CHAIR.get(CopperStage.EXPOSED).get(), pos);
        useOn(helper, player, pos, new ItemStack(Items.IRON_AXE));
        helper.assertBlockPresent(MaterialFurniture.COPPER_CHAIR.get(CopperStage.UNAFFECTED).get(), pos);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void copperLampsDimAsTheyAge(GameTestHelper helper) {
        int last = 16;
        for (CopperStage stage : new CopperStage[] {CopperStage.UNAFFECTED, CopperStage.EXPOSED, CopperStage.WEATHERED, CopperStage.OXIDIZED}) {
            var lit = MaterialFurniture.COPPER_LAMP.get(stage).get().defaultBlockState().setValue(com.reiko45.furniture.core.block.SwitchableFurnitureBlock.LIT, true);
            int light = lit.getLightEmission();
            helper.assertTrue(light > 0 && light < last, stage + " gives less light than the stage before");
            helper.assertTrue(Lighting.isLamp(lit), stage + " is a lamp");
            last = light;
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stoneCountersFormCornersAndPlantersGrowPlants(GameTestHelper helper) {
        Block counter = MaterialFurniture.STONE_COUNTER.get(StoneMaterial.WHITE_MARBLE).get();
        BlockPos inner = new BlockPos(1, 1, 2);
        CoreGameTests.placeFacing(helper, inner, counter, Direction.NORTH);
        CoreGameTests.placeFacing(helper, inner.north(), counter, Direction.EAST);
        helper.assertBlockProperty(inner, CounterBlock.SHAPE, CounterBlock.Shape.INNER);

        BlockPos planter = new BlockPos(4, 1, 2);
        helper.setBlock(planter, MaterialFurniture.CONCRETE_PLANTER.get(DyeColor.ORANGE).get());
        helper.assertValueEqual(helper.getBlockState(planter).canSustainPlant(helper.getLevel(), helper.absolutePos(planter), Direction.UP,
                Blocks.POPPY.defaultBlockState()), TriState.TRUE, "flowers grow in it");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stoneAndMetalNeedAPickaxe(GameTestHelper helper) {
        for (Block block : new Block[] {MaterialFurniture.STONE_DINING_TABLE.get(StoneMaterial.BLACKSTONE).get(),
                MaterialFurniture.CONCRETE_BENCH.get(DyeColor.CYAN).get(), MaterialFurniture.COPPER_TABLE.get(CopperStage.OXIDIZED).get(),
                MaterialFurniture.WROUGHT_IRON_LAMP_POST.get()}) {
            helper.assertTrue(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE), block + " is mined with a pickaxe");
        }
        helper.assertTrue(MaterialFurniture.WROUGHT_IRON_LAMP_POST.get().defaultBlockState().is(FurnitureTags.LAMPS), "the lamp post is a lamp");
        helper.succeed();
    }
}
