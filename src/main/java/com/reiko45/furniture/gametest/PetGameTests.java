package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.pets.PetFurniture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/** Tests for the pet corner. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class PetGameTests {
    private static void floor(GameTestHelper helper) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
            }
        }
    }

    private static Wolf tamedWolf(GameTestHelper helper, BlockPos pos) {
        Wolf wolf = helper.spawn(EntityType.WOLF, pos);
        wolf.setTame(true, true);
        return wolf;
    }

    /** A wolf tamed by a player standing nearby (a tamed wolf whose owner isn't around just sits). */
    private static Wolf ownedWolf(GameTestHelper helper, BlockPos pos, ServerPlayer owner) {
        owner.moveTo(helper.absoluteVec(Vec3.atBottomCenterOf(pos)));
        Wolf wolf = helper.spawn(EntityType.WOLF, pos);
        wolf.tame(owner);
        return wolf;
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void feedingBowlHoldsAStackOfFood(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, PetFurniture.FEEDING_BOWL.get(DyeColor.RED).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIRT, 4));
        helper.useBlock(pos, player);
        DisplayBlockEntity bowl = helper.getBlockEntity(pos);
        helper.assertTrue(bowl.isEmpty(), "dirt isn't pet food");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BEEF, 20));
        for (int i = 0; i < 20; i++) {
            helper.useBlock(pos, player);
        }
        helper.assertValueEqual(bowl.getItems().get(0).getCount(), 16, "the bowl holds 16");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 4, "four beef left over");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 200)
    public static void hurtPetsEatFromTheBowl(GameTestHelper helper) {
        floor(helper);
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, PetFurniture.FEEDING_BOWL.get(DyeColor.BLUE).get());
        DisplayBlockEntity bowl = helper.getBlockEntity(pos);
        bowl.insert(new ItemStack(Items.COOKED_COD, 3), null);
        bowl.insert(new ItemStack(Items.COOKED_COD, 3), null);
        Wolf wolf = tamedWolf(helper, new BlockPos(4, 1, 4));
        wolf.setHealth(5);
        helper.succeedWhen(() -> {
            helper.assertTrue(wolf.getHealth() > 5, "the wolf should have healed");
            helper.assertValueEqual(bowl.getItems().get(0).getCount(), 1, "one fish eaten");
        });
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void catsTreatPetBedsAsBeds(GameTestHelper helper) {
        helper.assertTrue(PetFurniture.PET_BED.get(DyeColor.WHITE).get().defaultBlockState().is(BlockTags.BEDS), "pet bed");
        helper.assertFalse(PetFurniture.FEEDING_BOWL.get(DyeColor.WHITE).get().defaultBlockState().is(BlockTags.BEDS), "bowl");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 400)
    public static void tamedWolvesRestOnPetBeds(GameTestHelper helper) {
        floor(helper);
        BlockPos bed = new BlockPos(6, 1, 6);
        helper.setBlock(bed, PetFurniture.PET_BED.get(DyeColor.RED).get());
        ServerPlayer owner = helper.makeMockServerPlayerInLevel();
        Wolf wolf = ownedWolf(helper, new BlockPos(1, 1, 1), owner);
        helper.succeedWhen(() -> {
            helper.assertTrue(wolf.blockPosition().equals(helper.absolutePos(bed)), "the wolf should be on the bed");
            helper.assertTrue(wolf.isInSittingPose(), "the wolf should lie down");
            helper.getLevel().getServer().getPlayerList().remove(owner);
        });
    }
}
