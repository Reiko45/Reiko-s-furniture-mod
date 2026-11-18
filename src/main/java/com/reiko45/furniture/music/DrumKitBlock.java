package com.reiko45.furniture.music;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A drum kit: hit the cymbal for a hi-hat, the snare for a snare drum, or low down for the bass drum. */
public class DrumKitBlock extends FurnitureBlock {
    public DrumKitBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    /** Which drum is hit, from the height of the click within the block (0-1). */
    public static NoteBlockInstrument drumFor(double height) {
        if (height >= 0.6) {
            return NoteBlockInstrument.HAT;
        }
        return height >= 0.42 ? NoteBlockInstrument.SNARE : NoteBlockInstrument.BASEDRUM;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level instanceof ServerLevel server) {
            NoteBlockInstrument drum = drumFor(hit.getLocation().y - pos.getY());
            server.playSound(null, pos, drum.getSoundEvent().value(), SoundSource.RECORDS, 2.0F, 1.0F);
            server.sendParticles(ParticleTypes.NOTE, hit.getLocation().x, pos.getY() + 1.0, hit.getLocation().z, 0,
                    drum.ordinal() / 24.0, 0.0, 0.0, 1.0);
            server.gameEvent(player, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
