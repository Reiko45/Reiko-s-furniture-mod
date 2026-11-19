package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.List;

/** A stocking hung on the wall, with one gift peeking out of the top. */
public class StockingBlock extends DisplayFurnitureBlock {
    public StockingBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(3, 2, 14.5, 11, 15, 16)),
                List.of(new DisplaySlot(7, 12.5F, 15F, false, 0.35F)));
    }
}
