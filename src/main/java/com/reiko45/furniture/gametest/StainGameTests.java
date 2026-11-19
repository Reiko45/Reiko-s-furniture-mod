package com.reiko45.furniture.gametest;

import com.mojang.serialization.JsonOps;
import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.core.repaint.Repaint;
import com.reiko45.furniture.core.stain.StainMap;
import com.reiko45.furniture.core.stain.Stains;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class StainGameTests {
    private static Player sneaking(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        return player;
    }

    private static DyeColor stain(GameTestHelper helper, BlockPos pos) {
        return Stains.get(helper.getLevel(), helper.absolutePos(pos));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void dyeStainsAndSpongeCleans(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BedroomFurniture.NIGHTSTAND.get(FurnitureWood.OAK).get());
        ItemStack dye = new ItemStack(Items.RED_DYE, 2);
        helper.assertTrue(Repaint.use(helper.getLevel(), helper.absolutePos(pos), sneaking(helper), dye), "dye stains wood");
        helper.assertValueEqual(stain(helper, pos), DyeColor.RED, "stain");
        helper.assertValueEqual(dye.getCount(), 1, "one dye used");

        Repaint.use(helper.getLevel(), helper.absolutePos(pos), sneaking(helper), new ItemStack(Items.SPONGE));
        helper.assertTrue(stain(helper, pos) == null, "the sponge takes the stain off");
        // Fabric pieces change color with dye instead of being stained.
        BlockPos sofa = pos.east(2);
        helper.setBlock(sofa, LivingRoomFurniture.SOFA.get(DyeColor.WHITE).get());
        Repaint.use(helper.getLevel(), helper.absolutePos(sofa), sneaking(helper), new ItemStack(Items.RED_DYE));
        helper.assertTrue(stain(helper, sofa) == null, "fabric is not stained");
        helper.assertBlockPresent(LivingRoomFurniture.SOFA.get(DyeColor.RED).get(), sofa);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void tallPiecesAreStainedWhole(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState wardrobe = BedroomFurniture.WARDROBE.get(FurnitureWood.BIRCH).get().defaultBlockState();
        helper.setBlock(pos, wardrobe);
        helper.setBlock(pos.above(), wardrobe.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        Repaint.use(helper.getLevel(), helper.absolutePos(pos.above()), sneaking(helper), new ItemStack(Items.BLUE_DYE));
        helper.assertValueEqual(stain(helper, pos), DyeColor.BLUE, "lower half");
        helper.assertValueEqual(stain(helper, pos.above()), DyeColor.BLUE, "upper half");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stainedPiecesDropAndPlaceStained(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Block desk = BedroomFurniture.DESK.get(FurnitureWood.SPRUCE).get();
        helper.setBlock(pos, desk);
        Stains.setPiece(helper.getLevel(), helper.absolutePos(pos), DyeColor.GREEN);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        helper.assertTrue(stain(helper, pos) == null, "the stain goes with the piece");
        ItemEntity drop = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new net.minecraft.world.phys.AABB(helper.absolutePos(pos)).inflate(2))
                .stream().filter(item -> item.getItem().is(desk.asItem())).findFirst().orElse(null);
        helper.assertTrue(drop != null, "the desk dropped");
        helper.assertValueEqual(drop.getItem().get(ModDataComponents.STAIN.get()), DyeColor.GREEN, "the dropped desk is stained");

        // Placing it again stains the new desk.
        BlockPos floor = new BlockPos(3, 0, 3);
        helper.setBlock(floor, Blocks.STONE);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, drop.getItem().copy());
        helper.useBlock(floor, player, new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(floor)).add(0, 0.5, 0), Direction.UP,
                helper.absolutePos(floor), false));
        helper.assertBlockPresent(desk, floor.above());
        helper.assertValueEqual(stain(helper, floor.above()), DyeColor.GREEN, "placed stained");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void replacedPiecesLoseTheirStain(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, BedroomFurniture.DESK.get(FurnitureWood.OAK).get());
        Stains.setPiece(helper.getLevel(), helper.absolutePos(pos), DyeColor.PURPLE);
        helper.setBlock(pos, Blocks.STONE);
        helper.assertTrue(stain(helper, pos) == null, "replaced by stone");
        helper.setBlock(pos, BedroomFurniture.DESK.get(FurnitureWood.OAK).get());
        helper.assertTrue(stain(helper, pos) == null, "a new desk there starts unstained");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stainsAreSavedInOrder(GameTestHelper helper) {
        StainMap map = StainMap.EMPTY.with(new BlockPos(5, 64, 2), DyeColor.RED).with(new BlockPos(1, 64, 2), DyeColor.BLUE);
        var json = StainMap.CODEC.encodeStart(JsonOps.INSTANCE, map).getOrThrow();
        helper.assertValueEqual(StainMap.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(), map, "codec round trip");
        helper.assertTrue(json.getAsJsonArray().get(0).getAsJsonObject().get("pos").getAsLong() < json.getAsJsonArray().get(1)
                .getAsJsonObject().get("pos").getAsLong(), "saved in position order");
        helper.assertTrue(map.with(new BlockPos(5, 64, 2), null).with(new BlockPos(1, 64, 2), null).isEmpty(), "removing");
        helper.succeed();
    }
}
