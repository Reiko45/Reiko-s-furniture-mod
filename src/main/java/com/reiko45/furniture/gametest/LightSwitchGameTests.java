package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.bedroom.BedroomFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.lighting.LightSwitchBlock;
import com.reiko45.furniture.core.lighting.LightSwitchBlockEntity;
import com.reiko45.furniture.core.lighting.LightSwitchItem;
import com.reiko45.furniture.core.lighting.Lighting;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import com.reiko45.furniture.livingroom.LivingRoomFurniture;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class LightSwitchGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void switchTurnsLinkedLampsOnAndOff(GameTestHelper helper) {
        BlockPos table = new BlockPos(1, 1, 1);
        BlockPos floor = new BlockPos(3, 1, 1);
        BlockPos pendant = new BlockPos(5, 1, 1);
        helper.setBlock(table, BedroomFurniture.TABLE_LAMP.get(FurnitureWood.OAK).get());
        BlockState floorLamp = LivingRoomFurniture.FLOOR_LAMP.get(FurnitureWood.BIRCH).get().defaultBlockState();
        helper.setBlock(floor, floorLamp);
        helper.setBlock(floor.above(), floorLamp.setValue(TallBlockHelper.HALF, DoubleBlockHalf.UPPER));
        helper.setBlock(pendant, KitchenFurniture.PENDANT_LAMP.get(DyeColor.YELLOW).get());

        ItemStack item = new ItemStack(Lighting.LIGHT_SWITCH.get());
        LightSwitchItem.link(item, helper.absolutePos(table), null);
        // Clicking the upper half of the floor lamp links the lamp by its lower half.
        BlockPos upper = helper.absolutePos(floor.above());
        LightSwitchItem.link(item, Lighting.lampPos(helper.getLevel().getBlockState(upper), upper), null);
        LightSwitchItem.link(item, helper.absolutePos(pendant), null);
        LightSwitchItem.link(item, helper.absolutePos(pendant), null); // a second click unlinks it again
        LightSwitchItem.link(item, helper.absolutePos(pendant), null);
        helper.assertValueEqual(item.get(ModDataComponents.LINKED_LAMPS.get()).size(), 3, "linked lamps");

        BlockPos switchPos = new BlockPos(3, 1, 4);
        helper.setBlock(switchPos, Lighting.LIGHT_SWITCH.get());
        LightSwitchBlockEntity entity = helper.getBlockEntity(switchPos);
        entity.setLamps(item.get(ModDataComponents.LINKED_LAMPS.get()));

        helper.useBlock(switchPos, helper.makeMockPlayer(GameType.SURVIVAL));
        for (BlockPos lamp : List.of(table, floor, floor.above(), pendant)) {
            helper.assertBlockProperty(lamp, SwitchableFurnitureBlock.LIT, true);
        }
        helper.assertBlockProperty(switchPos, LightSwitchBlock.ON, true);
        helper.useBlock(switchPos, helper.makeMockPlayer(GameType.SURVIVAL));
        for (BlockPos lamp : List.of(table, floor, floor.above(), pendant)) {
            helper.assertBlockProperty(lamp, SwitchableFurnitureBlock.LIT, false);
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void brokenSwitchKeepsItsLamps(GameTestHelper helper) {
        BlockPos lamp = new BlockPos(1, 1, 1);
        helper.setBlock(lamp, BedroomFurniture.TABLE_LAMP.get(FurnitureWood.OAK).get());
        BlockPos switchPos = new BlockPos(3, 1, 1);
        helper.setBlock(switchPos, Lighting.LIGHT_SWITCH.get());
        LightSwitchBlockEntity entity = helper.getBlockEntity(switchPos);
        entity.setLamps(List.of(helper.absolutePos(lamp)));
        helper.getLevel().destroyBlock(helper.absolutePos(switchPos), true);
        ItemEntity drop = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(switchPos)).inflate(2))
                .stream().filter(item -> item.getItem().is(Lighting.LIGHT_SWITCH.get().asItem())).findFirst().orElse(null);
        helper.assertTrue(drop != null, "the switch dropped");
        helper.assertValueEqual(drop.getItem().get(ModDataComponents.LINKED_LAMPS.get()), List.of(helper.absolutePos(lamp)),
                "the dropped switch remembers its lamp");
        // Only lamps count: the TV switches on too, but isn't a lamp.
        helper.assertFalse(Lighting.isLamp(LivingRoomFurniture.TV.get().defaultBlockState()), "a TV is not a lamp");
        helper.assertTrue(Lighting.isLamp(LivingRoomFurniture.CEILING_LIGHT.get().defaultBlockState()), "a ceiling light is");
        helper.succeed();
    }
}
