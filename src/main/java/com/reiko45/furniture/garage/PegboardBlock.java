package com.reiko45.furniture.garage;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/** A pegboard on the wall: hang up to six tools or weapons (anything that doesn't stack). */
public class PegboardBlock extends DisplayFurnitureBlock {
    public PegboardBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 0, 14.75, 16, 16, 16)), hooks());
    }

    private static List<DisplaySlot> hooks() {
        List<DisplaySlot> slots = new ArrayList<>();
        for (float y : new float[] {8.5F, 2.5F}) {
            for (float x : new float[] {3.5F, 8F, 12.5F}) {
                slots.add(new DisplaySlot(x, y, 14.6F, false, 0.32F));
            }
        }
        return slots;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }
}
