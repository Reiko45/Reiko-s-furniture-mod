package com.reiko45.furniture.outdoor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A wooden box full of soil. Flowers, saplings and crops can be planted on top, and crops grow as fast
 * as on watered farmland.
 */
public class PlanterBoxBlock extends Block {
    public PlanterBoxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        return facing == Direction.UP ? TriState.TRUE : TriState.DEFAULT;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
