package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.decor.DecorFurniture;
import com.reiko45.furniture.decor.Paintings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests for picture frames and paintings. */
@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class ArtGameTests {
    @GameTest(template = CoreGameTests.EMPTY)
    public static void pictureFrameShowsAMap(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, DecorFurniture.PICTURE_FRAME.get(FurnitureWood.DARK_OAK).get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FILLED_MAP));
        helper.useBlock(pos, player);
        DisplayBlockEntity frame = helper.getBlockEntity(pos);
        helper.assertTrue(frame.getItems().get(0).is(Items.FILLED_MAP), "the map is in the frame");
        helper.assertTrue(player.getMainHandItem().isEmpty(), "taken from the hand");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void paintingsCanBePlaced(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().registryOrThrow(Registries.PAINTING_VARIANT);
        for (Paintings.Art art : Paintings.ALL) {
            Holder<PaintingVariant> variant = registry.getHolder(art.key())
                    .orElseThrow(() -> new net.minecraft.gametest.framework.GameTestAssertException(art.name() + " is missing"));
            helper.assertValueEqual(variant.value().width(), art.width(), art.name() + " width");
            helper.assertValueEqual(variant.value().height(), art.height(), art.name() + " height");
            helper.assertTrue(variant.is(PaintingVariantTags.PLACEABLE), art.name() + " can be placed by the painting item");
        }
        helper.succeed();
    }
}
