package com.reiko45.furniture.bar;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import com.reiko45.furniture.kitchen.appliance.DrinkItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;

/** A wine rack with nine cubbies, one bottle each: potions, honey, drinks or empty bottles. */
public class WineRackBlock extends DisplayFurnitureBlock {
    public WineRackBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 0, 1, 16, 16, 16)), cubbies());
    }

    private static List<DisplaySlot> cubbies() {
        List<DisplaySlot> slots = new ArrayList<>();
        for (float y : new float[] {10.76F, 5.76F, 0.76F}) {
            for (float x : new float[] {3.25F, 8F, 12.75F}) {
                slots.add(new DisplaySlot(x, y, 9, false, 0.28F));
            }
        }
        return slots;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getItem() instanceof PotionItem || stack.getItem() instanceof DrinkItem || stack.is(Items.GLASS_BOTTLE)
                || stack.is(Items.HONEY_BOTTLE) || stack.is(Items.EXPERIENCE_BOTTLE) || stack.is(Items.DRAGON_BREATH)
                || stack.is(Items.OMINOUS_BOTTLE);
    }
}
