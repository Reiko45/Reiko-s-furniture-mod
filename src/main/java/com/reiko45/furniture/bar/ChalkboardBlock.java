package com.reiko45.furniture.bar;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A wall menu board you write on like a sign. It is a vanilla wall sign with its own look (a
 * {@link WoodType} whose sign texture is the chalkboard) and white "chalk" text.
 */
public class ChalkboardBlock extends WallSignBlock {
    /** The chalkboard's sign look: {@code textures/entity/signs/chalkboard.png}. */
    public static final WoodType TYPE = WoodType.register(new WoodType("reikos_furniture:chalkboard", BlockSetType.OAK));

    public ChalkboardBlock(Properties properties) {
        super(TYPE, properties);
    }

    /**
     * Vanilla wall signs take their name from the standing sign's item; the chalkboard is its own block
     * (and its item asks the block for the name), so it names itself.
     */
    @Override
    public String getDescriptionId() {
        return Util.makeDescriptionId("block", BuiltInRegistries.BLOCK.getKey(this));
    }

    /**
     * Chalk is white: new chalkboards start with white text on both sides. It glows, because sign text
     * is otherwise drawn darkened and white would look grey on the board.
     */
    public static void whiten(SignBlockEntity sign) {
        sign.setText(sign.getText(true).setColor(DyeColor.WHITE).setHasGlowingText(true), true);
        sign.setText(sign.getText(false).setColor(DyeColor.WHITE).setHasGlowingText(true), false);
    }
}
