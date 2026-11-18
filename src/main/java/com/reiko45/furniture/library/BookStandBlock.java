package com.reiko45.furniture.library;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.List;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

/** A small slanted stand that shows one book. */
public class BookStandBlock extends DisplayFurnitureBlock {
    public BookStandBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(3, 0, 3.5, 13, 10.5, 12.5)), List.of(new DisplaySlot(8, 9.6F, 8.5F, true, 0.45F)));
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.is(ItemTags.BOOKSHELF_BOOKS);
    }
}
