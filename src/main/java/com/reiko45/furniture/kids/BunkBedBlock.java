package com.reiko45.furniture.kids;

import com.reiko45.furniture.core.block.FurnitureBedBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jetbrains.annotations.Nullable;

/**
 * Bunk bed: a normal bed (sleep in it, set spawn) with a decorative top bunk on top of each half.
 * It needs two free blocks above, and breaking any part removes the whole bunk bed.
 */
public class BunkBedBlock extends FurnitureBedBlock {
    private final Supplier<? extends BunkTopBlock> top;

    public BunkBedBlock(Properties properties, Supplier<? extends BunkTopBlock> top) {
        super(properties);
        this.top = top;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        BlockPos foot = context.getClickedPos();
        BlockPos head = foot.relative(state.getValue(FACING));
        Level level = context.getLevel();
        boolean room = foot.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(foot.above()).canBeReplaced(context)
                && level.getBlockState(head.above()).canBeReplaced(context);
        return room ? state : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            DyeColor color = state.getValue(COLOR);
            level.setBlock(pos.above(), topState(facing, BedPart.FOOT, color), Block.UPDATE_ALL);
            level.setBlock(pos.relative(facing).above(), topState(facing, BedPart.HEAD, color), Block.UPDATE_ALL);
        }
    }

    public BlockState topState(Direction facing, BedPart part, DyeColor color) {
        return top.get().defaultBlockState().setValue(FACING, facing).setValue(PART, part).setValue(COLOR, color);
    }

    @Override
    protected void onRecolor(Level level, BlockPos pos, BlockPos otherPos, DyeColor color) {
        for (BlockPos above : new BlockPos[] {pos.above(), otherPos.above()}) {
            BlockState state = level.getBlockState(above);
            if (state.getBlock() instanceof BunkTopBlock) {
                level.setBlock(above, state.setValue(COLOR, color), Block.UPDATE_ALL);
            }
        }
    }
}
