package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Tests that cover every furniture block, whatever room it is in. Run with {@code ./gradlew runGameTestServer}. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class CoreGameTests {
    static final String EMPTY = "empty";

    @GameTest(template = EMPTY)
    public static void everyBlockDropsItself(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            Block block = holder.get();
            List<ItemStack> drops = Block.getDrops(block.defaultBlockState(), helper.getLevel(), pos, null);
            if (drops.stream().noneMatch(stack -> stack.is(block.asItem()))) {
                helper.fail(BuiltInRegistries.BLOCK.getKey(block) + " does not drop itself, got " + drops);
            }
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void storageSizeMatchesRows(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            if (holder.get() instanceof FurnitureStorageBlock storage) {
                helper.setBlock(pos, storage.defaultBlockState());
                FurnitureStorageBlockEntity entity = helper.getBlockEntity(pos);
                helper.assertValueEqual(entity.getContainerSize(), storage.rows() * 9,
                        BuiltInRegistries.BLOCK.getKey(storage) + " slot count");
            }
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void storageDropsContentsWhenBroken(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            if (holder.get() instanceof FurnitureStorageBlock storage && !(storage instanceof TallStorageFurnitureBlock)) {
                helper.setBlock(pos, storage.defaultBlockState());
                FurnitureStorageBlockEntity entity = helper.getBlockEntity(pos);
                entity.setItem(0, new ItemStack(Items.DIAMOND, 3));
                helper.destroyBlock(pos);
                helper.assertItemEntityPresent(Items.DIAMOND, pos, 2.0);
                break;
            }
        }
        helper.succeed();
    }
}
