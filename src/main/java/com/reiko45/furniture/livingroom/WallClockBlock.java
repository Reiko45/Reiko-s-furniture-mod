package com.reiko45.furniture.livingroom;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Wall clock. The dial shows the time of day like the vanilla clock; right-click to read the exact time. */
public class WallClockBlock extends FurnitureBlock implements EntityBlock {
    public WallClockBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WallClockBlockEntity(pos, state);
    }

    /** Minecraft time to a 24-hour clock: tick 0 is 06:00, tick 6000 is noon. */
    public static String formatTime(long dayTime) {
        long ticks = Math.floorMod(dayTime, 24000L);
        int hours = (int) ((ticks / 1000 + 6) % 24);
        int minutes = (int) (ticks % 1000 * 60 / 1000);
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("message.reikos_furniture.clock_time",
                    formatTime(level.getDayTime())), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
