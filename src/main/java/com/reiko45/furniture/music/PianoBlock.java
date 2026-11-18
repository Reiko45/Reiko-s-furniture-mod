package com.reiko45.furniture.music;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A playable upright piano. Right-click the keyboard: the further right you click, the higher the
 * note (two octaves, like a note block).
 */
public class PianoBlock extends FurnitureBlock {
    public PianoBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    /**
     * The note (0-24) for a click at {@code hit} on a piano facing {@code facing}. The lowest notes are on
     * the left of the player sitting at the piano, who looks the opposite way to {@code facing}.
     */
    public static int noteFor(Direction facing, Vec3 hitInBlock) {
        Direction playerLeft = facing.getClockWise();
        double along = (hitInBlock.x - 0.5) * playerLeft.getStepX() + (hitInBlock.z - 0.5) * playerLeft.getStepZ();
        double fraction = 0.5 - along;
        return Mth.clamp((int) Math.round(fraction * 24), 0, 24);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level instanceof ServerLevel server) {
            Vec3 inBlock = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            int note = noteFor(state.getValue(FACING), inBlock);
            server.playSound(null, pos, NoteBlockInstrument.HARP.getSoundEvent().value(), SoundSource.RECORDS, 2.0F,
                    NoteBlock.getPitchFromNote(note));
            server.sendParticles(ParticleTypes.NOTE, hit.getLocation().x, pos.getY() + 1.1, hit.getLocation().z, 0,
                    note / 24.0, 0.0, 0.0, 1.0);
            server.gameEvent(player, GameEvent.NOTE_BLOCK_PLAY, pos);
            FurnitureActionTrigger.award(player, FurnitureActionTrigger.PIANO);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
