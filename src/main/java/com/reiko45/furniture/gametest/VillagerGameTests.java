package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.villager.FurnitureMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the furniture maker villager. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class VillagerGameTests {
    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 600)
    public static void villagerTakesTheFurnitureMakerJob(GameTestHelper helper) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
            }
        }
        helper.setBlock(new BlockPos(5, 1, 5), Workbench.BLOCK.get());
        Villager villager = helper.spawn(EntityType.VILLAGER, new BlockPos(2, 1, 2));
        helper.succeedWhen(() -> helper.assertValueEqual(villager.getVillagerData().getProfession(), FurnitureMaker.FURNITURE_MAKER.get(),
                "profession"));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void furnitureMakerSellsFurniture(GameTestHelper helper) {
        Villager villager = helper.spawn(EntityType.VILLAGER, new BlockPos(2, 1, 2));
        villager.setNoAi(true);
        villager.setVillagerData(villager.getVillagerData().setProfession(FurnitureMaker.FURNITURE_MAKER.get()).setLevel(1));
        helper.assertValueEqual(villager.getOffers().size(), 2, "novice trades");
        for (MerchantOffer offer : villager.getOffers()) {
            boolean buysPlanks = offer.getResult().is(Items.EMERALD) && offer.getCostA().getItem().builtInRegistryHolder().is(net.minecraft.tags.ItemTags.PLANKS);
            boolean sellsFurniture = BuiltInRegistries.ITEM.getKey(offer.getResult().getItem()).getNamespace().equals(ReikosFurniture.MODID);
            helper.assertTrue(buysPlanks || sellsFurniture, "unexpected trade " + offer.getCostA() + " -> " + offer.getResult());
        }
        helper.assertFalse(villager.getVillagerData().getProfession() == VillagerProfession.NONE, "still a furniture maker");
        helper.succeed();
    }
}
