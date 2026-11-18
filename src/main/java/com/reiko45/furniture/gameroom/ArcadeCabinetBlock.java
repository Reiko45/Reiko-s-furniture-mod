package com.reiko45.furniture.gameroom;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.block.TallBlockHelper;
import com.reiko45.furniture.core.registry.ModSounds;
import com.reiko45.furniture.livingroom.FloorLampBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Two-block-tall arcade cabinet. Right-click (or redstone) switches it on; the screen plays a little
 * game. Each time it is switched on you get a random high score.
 */
public class ArcadeCabinetBlock extends FloorLampBlock {
    /** Scores from here up count for the "High Score" advancement. */
    public static final int HIGH_SCORE = 9000;

    public ArcadeCabinetBlock(Properties properties, VoxelShape lowerShape, VoxelShape upperShape) {
        super(properties, lowerShape, upperShape);
    }

    /** Light level: the screen glows a little while it is on. */
    public static int screenLight(BlockState state) {
        return state.getValue(LIT) && TallBlockHelper.isUpper(state) ? 9 : 0;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        if (!level.isClientSide && level.getBlockState(pos).getValue(LIT)) {
            int score = (1 + level.random.nextInt(99)) * 100;
            player.displayClientMessage(Component.translatable("message.reikos_furniture.arcade_score", score), true);
            if (score >= HIGH_SCORE) {
                FurnitureActionTrigger.award(player, FurnitureActionTrigger.ARCADE_HIGH_SCORE);
            }
        }
        return result;
    }

    @Override
    protected void playSwitchSound(Level level, BlockPos pos, BlockState before, BlockState after) {
        if (after.getValue(LIT)) {
            level.playSound(null, pos, ModSounds.ARCADE_BLIP.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
        } else {
            super.playSwitchSound(level, pos, before, after);
        }
    }
}
