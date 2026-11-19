package com.reiko45.furniture.studio;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** A canvas hung on a wall, showing its painting. Placed with the canvas item; drops it with the painting. */
public class CanvasBlock extends FurnitureBlock implements EntityBlock {
    public CanvasBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 0, 15, 16, 16, 16)));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CanvasBlockEntity(pos, state);
    }
}
