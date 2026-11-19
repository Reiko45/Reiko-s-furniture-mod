package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.studio.CanvasBlockEntity;
import com.reiko45.furniture.studio.CanvasItem;
import com.reiko45.furniture.studio.EaselBlock;
import com.reiko45.furniture.studio.Painting;
import com.reiko45.furniture.studio.StudioFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class StudioGameTests {
    /** A painting with a red top row and the rest bare. */
    private static String redTop() {
        StringBuilder pixels = new StringBuilder(Painting.BLANK.pixels());
        for (int x = 0; x < Painting.SIZE; x++) {
            pixels.setCharAt(x, Painting.of(DyeColor.RED));
        }
        return pixels.toString();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void paintingAndSigningOnTheEasel(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, StudioFurniture.EASEL.get(FurnitureWood.SPRUCE).get());
        BlockPos easel = helper.absolutePos(pos);
        DisplayBlockEntity display = helper.getBlockEntity(pos);
        helper.assertFalse(((EaselBlock) helper.getBlockState(pos).getBlock()).accepts(new ItemStack(Items.PAPER)), "only canvases");
        helper.assertTrue(display.insert(new ItemStack(StudioFurniture.CANVAS.get()), null), "canvas on the easel");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(easel.getCenter().add(2, 0, 0));
        String pixels = redTop();
        helper.assertTrue(EaselBlock.paint(player, easel, pixels, ""), "painted");
        Painting painting = CanvasItem.painting(EaselBlock.canvas(helper.getLevel(), easel));
        helper.assertValueEqual(painting.pixels(), pixels, "pixels saved");
        helper.assertFalse(painting.signed(), "not signed yet");

        helper.assertFalse(EaselBlock.paint(player, easel, "not a painting", ""), "bad pixels are refused");
        player.setPos(easel.getCenter().add(20, 0, 0));
        helper.assertFalse(EaselBlock.paint(player, easel, Painting.BLANK.pixels(), ""), "too far away");
        player.setPos(easel.getCenter().add(2, 0, 0));

        helper.assertTrue(EaselBlock.paint(player, easel, pixels, "  Red Sky  "), "signed");
        painting = CanvasItem.painting(EaselBlock.canvas(helper.getLevel(), easel));
        helper.assertValueEqual(painting.title(), "Red Sky", "title");
        helper.assertValueEqual(painting.author(), player.getGameProfile().getName(), "author");
        helper.assertValueEqual(EaselBlock.canvas(helper.getLevel(), easel).getHoverName().getString(), "Red Sky", "named after the title");
        helper.assertFalse(EaselBlock.paint(player, easel, Painting.BLANK.pixels(), ""), "a signed painting can't be changed");
        helper.assertValueEqual(CanvasItem.painting(EaselBlock.canvas(helper.getLevel(), easel)).pixels(), pixels, "still red");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void canvasOnTheWallKeepsItsPainting(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Painting painting = new Painting(redTop(), "Dawn", "Someone");
        ItemStack item = new ItemStack(StudioFurniture.CANVAS.get());
        item.set(ModDataComponents.PAINTING.get(), painting);

        helper.setBlock(pos, StudioFurniture.CANVAS.get());
        CanvasBlockEntity canvas = helper.getBlockEntity(pos);
        canvas.applyComponentsFromItemStack(item);
        helper.assertValueEqual(canvas.painting(), painting, "placed with its painting");

        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        ItemStack dropped = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(pos)).inflate(2))
                .stream().map(ItemEntity::getItem).filter(stack -> stack.is(StudioFurniture.CANVAS.get().asItem()))
                .findFirst().orElse(ItemStack.EMPTY);
        helper.assertValueEqual(CanvasItem.painting(dropped), painting, "the dropped canvas keeps it");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void paintingsAreSavedAndChecked(GameTestHelper helper) {
        Painting painting = new Painting(redTop(), "Dawn", "Someone");
        var encoded = Painting.CODEC.encodeStart(NbtOps.INSTANCE, painting).getOrThrow();
        helper.assertValueEqual(Painting.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow(), painting, "round trip");
        helper.assertTrue(Painting.CODEC.parse(NbtOps.INSTANCE, StringTag.valueOf("x")).isError(), "garbage is refused");
        helper.assertFalse(Painting.valid("z".repeat(Painting.SIZE * Painting.SIZE)), "unknown colors are refused");
        helper.assertValueEqual(Painting.color(Painting.of(DyeColor.BLUE)), DyeColor.BLUE.getTextureDiffuseColor(), "palette");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 300)
    public static void potteryWheelThrowsAVase(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, StudioFurniture.POTTERY_WHEEL.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CLAY_BALL, 3));
        for (int i = 0; i < 3; i++) {
            helper.useBlock(pos, player);
        }
        helper.succeedWhen(() -> helper.assertItemEntityPresent(StudioFurniture.TERRACOTTA_VASE.get().asItem(), pos, 2.0));
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 300)
    public static void sewingMachineSewsCushions(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, StudioFurniture.SEWING_MACHINE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LIME_WOOL));
        helper.useBlock(pos, player);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STRING));
        helper.useBlock(pos, player);
        helper.succeedWhen(() -> {
            int sewn = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(pos)).inflate(2))
                    .stream().map(ItemEntity::getItem)
                    .filter(stack -> stack.is(StudioFurniture.FLOOR_CUSHION.get(DyeColor.LIME).get().asItem()))
                    .mapToInt(ItemStack::getCount).sum();
            helper.assertValueEqual(sewn, 2, "two lime cushions");
        });
    }
}
