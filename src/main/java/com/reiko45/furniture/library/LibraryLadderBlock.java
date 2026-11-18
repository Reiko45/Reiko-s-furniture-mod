package com.reiko45.furniture.library;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A library ladder leaning on the bookcase (or wall) behind it. You can climb it, and right-clicking
 * slides it, with any ladder pieces stacked on it, one block sideways towards the side you clicked,
 * if there's room and something to lean on there.
 */
public class LibraryLadderBlock extends FurnitureBlock {
    public LibraryLadderBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(1.5, 0, 13, 14.5, 16, 16)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            Vec3 inBlock = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            if (!slide(level, pos, state, towardsLeft(state.getValue(FACING), inBlock))) {
                player.displayClientMessage(Component.translatable("message.reikos_furniture.ladder_cannot_slide"), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Whether a click at this point (inside the block) is on the left half as seen from the front. */
    static boolean towardsLeft(Direction facing, Vec3 inBlock) {
        // Seen from the front (standing on the facing side), the left is the facing's clockwise side.
        Direction left = facing.getClockWise();
        double along = (inBlock.x - 0.5) * left.getStepX() + (inBlock.z - 0.5) * left.getStepZ();
        return along > 0;
    }

    /** Slides the ladder column at {@code pos} one block to the left or right, as seen from the front. */
    public boolean slide(Level level, BlockPos pos, BlockState state, boolean left) {
        Direction facing = state.getValue(FACING);
        Direction side = left ? facing.getClockWise() : facing.getCounterClockWise();
        List<BlockPos> column = column(level, pos, state);
        for (BlockPos part : column) {
            BlockPos target = part.relative(side);
            BlockState behind = level.getBlockState(target.relative(facing.getOpposite()));
            if (!level.getBlockState(target).canBeReplaced() || behind.isAir()) {
                return false;
            }
        }
        for (BlockPos part : column) {
            BlockState moving = level.getBlockState(part);
            level.setBlock(part, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(part.relative(side), moving, Block.UPDATE_ALL);
        }
        level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.6F, 0.7F);
        return true;
    }

    /** This ladder piece and the pieces stacked above and below it that face the same way. */
    private List<BlockPos> column(Level level, BlockPos pos, BlockState state) {
        List<BlockPos> column = new ArrayList<>();
        BlockPos bottom = pos;
        while (isSame(level.getBlockState(bottom.below()), state)) {
            bottom = bottom.below();
        }
        for (BlockPos part = bottom; isSame(level.getBlockState(part), state); part = part.above()) {
            column.add(part);
        }
        return column;
    }

    private boolean isSame(BlockState other, BlockState state) {
        return other.getBlock() instanceof LibraryLadderBlock && other.getValue(FACING) == state.getValue(FACING);
    }
}
