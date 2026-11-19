package com.reiko45.furniture.holiday;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.livingroom.FloorLampBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A Christmas tree, two blocks tall, with twinkling lights: right-click (or redstone, or a light
 * switch) to turn them on and off.
 */
public class ChristmasTreeBlock extends FloorLampBlock {
    public ChristmasTreeBlock(Properties properties, VoxelShape lowerShape, VoxelShape upperShape) {
        super(properties, lowerShape, upperShape);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        BlockState now = level.getBlockState(pos);
        if (!level.isClientSide && now.is(this) && now.getValue(LIT)) {
            FurnitureActionTrigger.award(player, FurnitureActionTrigger.DECK_THE_HALLS);
        }
        return result;
    }
}
