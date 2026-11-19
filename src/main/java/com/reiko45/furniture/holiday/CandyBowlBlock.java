package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

/** A bowl for treats: holds three pieces of food (cookies, pies, candy...). */
public class CandyBowlBlock extends DisplayFurnitureBlock {
    public CandyBowlBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(3, 0, 3, 13, 5, 13)),
                List.of(new DisplaySlot(6, 3.2F, 7, true, 0.4F), new DisplaySlot(10, 3.5F, 7.5F, true, 0.4F),
                        new DisplaySlot(8, 3.8F, 10, true, 0.4F)));
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.has(DataComponents.FOOD);
    }
}
