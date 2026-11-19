package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import com.reiko45.furniture.core.lighting.Lighting;
import com.reiko45.furniture.holiday.CandyBowlBlock;
import com.reiko45.furniture.holiday.HolidayFurniture;
import com.reiko45.furniture.holiday.SnowGlobeBlock;
import com.reiko45.furniture.holiday.StringLightsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class HolidayGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void presentKeepsWhatIsInside(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, HolidayFurniture.PRESENT.get(DyeColor.RED).get());
        FurnitureStorageBlockEntity present = helper.getBlockEntity(pos);
        helper.assertValueEqual(present.getContainerSize(), 9, "one row");
        present.setItem(0, new ItemStack(Items.DIAMOND, 3));

        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        AABB around = new AABB(helper.absolutePos(pos)).inflate(2);
        helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class, around).stream()
                .noneMatch(item -> item.getItem().is(Items.DIAMOND)), "the diamonds don't spill out");
        ItemStack dropped = helper.getLevel().getEntitiesOfClass(ItemEntity.class, around).stream().map(ItemEntity::getItem)
                .filter(stack -> stack.is(HolidayFurniture.PRESENT.get(DyeColor.RED).get().asItem())).findFirst().orElse(ItemStack.EMPTY);
        ItemContainerContents contents = dropped.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        helper.assertTrue(contents.nonEmptyStream().anyMatch(stack -> stack.is(Items.DIAMOND) && stack.getCount() == 3), "they are in the present");

        helper.setBlock(pos, HolidayFurniture.PRESENT.get(DyeColor.RED).get());
        FurnitureStorageBlockEntity placed = helper.getBlockEntity(pos);
        placed.applyComponentsFromItemStack(dropped);
        helper.assertTrue(placed.getItem(0).is(Items.DIAMOND), "and come back out when it is placed again");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void christmasTreeLightsUp(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState lower = HolidayFurniture.CHRISTMAS_TREE.get().defaultBlockState();
        helper.setBlock(pos, lower);
        TallBlockHelper.placeUpperHalf(helper.getLevel(), helper.absolutePos(pos), lower);
        helper.assertBlockProperty(pos.above(), TallBlockHelper.HALF, DoubleBlockHalf.UPPER);
        helper.assertTrue(Lighting.isLamp(helper.getBlockState(pos)), "a light switch can control it");

        helper.useBlock(pos.above(), helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, SwitchableFurnitureBlock.LIT, true);
        helper.assertBlockProperty(pos.above(), SwitchableFurnitureBlock.LIT, true);
        helper.assertValueEqual(helper.getBlockState(pos).getLightEmission(helper.getLevel(), helper.absolutePos(pos)), 12, "light");
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos.above(), SwitchableFurnitureBlock.LIT, false);
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void stringLightsJoinUp(GameTestHelper helper) {
        StringLightsBlock lights = HolidayFurniture.WARM_STRING_LIGHTS.get();
        BlockState wall = lights.defaultBlockState().setValue(FurnitureBlock.FACING, Direction.NORTH).setValue(StringLightsBlock.FACE, AttachFace.WALL);
        BlockPos first = new BlockPos(1, 2, 1);
        BlockPos second = first.east();
        helper.setBlock(first, wall);
        helper.setBlock(second, lights.joined(wall, helper.getLevel(), helper.absolutePos(second)));
        // Placing the second one updates the first.
        helper.assertBlockProperty(first, StringLightsBlock.EAST, true);
        helper.assertBlockProperty(first, StringLightsBlock.WEST, false);
        helper.assertBlockProperty(second, StringLightsBlock.WEST, true);
        helper.assertBlockProperty(second, StringLightsBlock.EAST, false);

        // A strand under the ceiling next to it doesn't join a strand on the wall.
        BlockPos third = second.east();
        helper.setBlock(third, wall.setValue(StringLightsBlock.FACE, AttachFace.CEILING));
        helper.assertBlockProperty(second, StringLightsBlock.EAST, false);
        // Nor do strands of the other kind.
        helper.setBlock(third, HolidayFurniture.COLORFUL_STRING_LIGHTS.get().defaultBlockState().setValue(FurnitureBlock.FACING, Direction.NORTH));
        helper.assertBlockProperty(second, StringLightsBlock.EAST, false);
        helper.assertValueEqual(helper.getBlockState(first).setValue(SwitchableFurnitureBlock.LIT, true)
                .getLightEmission(helper.getLevel(), helper.absolutePos(first)), 10, "light when on");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 200)
    public static void snowGlobeSnowsForAWhile(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, HolidayFurniture.SNOW_GLOBE.get());
        helper.useBlock(pos, helper.makeMockPlayer(GameType.SURVIVAL));
        helper.assertBlockProperty(pos, SnowGlobeBlock.SHAKEN, true);
        helper.succeedWhen(() -> helper.assertBlockProperty(pos, SnowGlobeBlock.SHAKEN, false));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void candyBowlHoldsTreats(GameTestHelper helper) {
        CandyBowlBlock bowl = HolidayFurniture.CANDY_BOWL.get();
        helper.assertTrue(bowl.accepts(new ItemStack(Items.COOKIE)), "cookies");
        helper.assertTrue(bowl.accepts(new ItemStack(Items.PUMPKIN_PIE)), "pie");
        helper.assertFalse(bowl.accepts(new ItemStack(Items.STICK)), "not sticks");
        helper.assertValueEqual(bowl.slots().size(), 3, "three treats");
        helper.succeed();
    }
}
