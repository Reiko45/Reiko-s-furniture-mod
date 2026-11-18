package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.config.FurnitureConfig;
import com.reiko45.furniture.office.OfficeFurniture;
import com.reiko45.furniture.office.SafeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for the config settings. Each change is put back in the same tick, so other tests don't see it. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class ConfigGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void defaultSettings(GameTestHelper helper) {
        helper.assertTrue(FurnitureConfig.SERVER_SPEC.isLoaded(), "the server config is loaded");
        helper.assertValueEqual(FurnitureConfig.laundryCycleTicks(), 100, "laundry cycle");
        helper.assertValueEqual(FurnitureConfig.applianceTicks(100), 100, "appliance time");
        helper.assertTrue(FurnitureConfig.safeOwnerOnly(), "safes are locked");
        helper.assertTrue(FurnitureConfig.moddedWoods(), "modded woods on");
        helper.assertValueEqual(FurnitureConfig.villageWorkshopWeight(), 2, "workshop weight");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void settingsChangeTheGame(GameTestHelper helper) {
        var server = FurnitureConfig.SERVER;
        server.laundryCycleSeconds.set(2);
        server.applianceSpeed.set(2.0);
        int laundry = FurnitureConfig.laundryCycleTicks();
        int appliance = FurnitureConfig.applianceTicks(100);
        server.laundryCycleSeconds.set(server.laundryCycleSeconds.getDefault());
        server.applianceSpeed.set(server.applianceSpeed.getDefault());
        helper.assertValueEqual(laundry, 40, "two-second laundry cycle");
        helper.assertValueEqual(appliance, 50, "twice as fast appliances");

        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, OfficeFurniture.SAFE.get());
        SafeBlockEntity safe = helper.getBlockEntity(pos);
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
        safe.setOwner(owner);
        server.safeOwnerOnly.set(false);
        boolean open = safe.canAccess(stranger);
        server.safeOwnerOnly.set(true);
        helper.assertTrue(open, "anyone can open safes when safe_owner_only is off");
        helper.assertFalse(safe.canAccess(stranger), "locked again");
        helper.succeed();
    }
}
