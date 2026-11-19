package com.reiko45.furniture.theater;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.entity.SeatEntity;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A film projector. Place it facing a {@link ProjectorScreenBlock} (its lens points away from you) up to
 * {@link #RANGE} blocks away. Each right-click goes to the next film: off, film 1, 2, 3, then off again;
 * the screen shows what the projector plays. Redstone switches it on and off.
 */
public class ProjectorBlock extends SwitchableFurnitureBlock {
    public static final int RANGE = 12;
    public static final IntegerProperty FILM = ProjectorScreenBlock.FILM;

    public ProjectorBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(FILM, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILM);
    }

    /** The way the lens points: away from whoever placed it. */
    public static Direction lens(BlockState state) {
        return state.getValue(FACING).getOpposite();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
        BlockState now = level.getBlockState(pos);
        if (!level.isClientSide && now.getBlock() instanceof ProjectorBlock && now.getValue(LIT) && findScreen(level, pos, now) == null) {
            player.displayClientMessage(Component.translatable("message.reikos_furniture.projector_no_screen"), true);
        }
        return result;
    }

    @Override
    protected BlockState nextState(BlockState state) {
        if (!state.getValue(LIT)) {
            return state.setValue(LIT, true).setValue(FILM, 1);
        }
        int film = state.getValue(FILM);
        return film < ProjectorScreenBlock.FILMS ? state.setValue(FILM, film + 1) : state.setValue(LIT, false).setValue(FILM, 1);
    }

    @Override
    protected void setSwitched(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.setSwitched(level, pos, oldState, newState);
        BlockPos screen = findScreen(level, pos, newState);
        if (screen != null) {
            ProjectorScreenBlock.show(level, screen, newState.getValue(LIT) ? newState.getValue(FILM) : 0);
            if (newState.getValue(LIT) && level instanceof ServerLevel server) {
                awardAudience(server, pos);
            }
        }
    }

    @Override
    protected void playSwitchSound(Level level, BlockPos pos, BlockState before, BlockState after) {
        if (after.getValue(LIT)) {
            level.playSound(null, pos, ModSounds.PROJECTOR.get(), SoundSource.BLOCKS, 0.7F, 1.0F);
        } else {
            super.playSwitchSound(level, pos, before, after);
        }
    }

    /**
     * The screen in front of the lens: the first screen part facing back at the projector, at the lens's
     * height or up to two blocks above or one below it, before a solid block gets in the way.
     */
    @Nullable
    public static BlockPos findScreen(Level level, BlockPos pos, BlockState state) {
        Direction lens = lens(state);
        for (int distance = 1; distance <= RANGE; distance++) {
            BlockPos ahead = pos.relative(lens, distance);
            for (int dy : new int[] {0, 1, -1, 2}) {
                BlockState candidate = level.getBlockState(ahead.above(dy));
                if (candidate.getBlock() instanceof ProjectorScreenBlock && candidate.getValue(FACING) == lens.getOpposite()) {
                    return ahead.above(dy);
                }
            }
            if (level.getBlockState(ahead).isSolidRender(level, ahead)) {
                return null;
            }
        }
        return null;
    }

    /** Players watching from a recliner nearby get the Movie Night advancement. */
    static void awardAudience(ServerLevel level, BlockPos pos) {
        for (Player player : level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(RANGE + 4))) {
            if (player.getVehicle() instanceof SeatEntity seat
                    && level.getBlockState(seat.blockPosition()).getBlock() instanceof ReclinerBlock) {
                FurnitureActionTrigger.award(player, FurnitureActionTrigger.MOVIE_NIGHT);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && state.getValue(LIT)) {
            BlockPos screen = findScreen(level, pos, state);
            if (screen != null) {
                ProjectorScreenBlock.show(level, screen, 0);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** The fan whirs and dust glints in the beam while it plays. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        if (random.nextInt(2) == 0) {
            level.playLocalSound(pos, ModSounds.PROJECTOR.get(), SoundSource.BLOCKS, 0.25F, 0.95F + random.nextFloat() * 0.1F, false);
        }
        Direction lens = lens(state);
        double along = 0.8 + random.nextDouble() * 2.5;
        level.addParticle(ParticleTypes.WHITE_ASH, pos.getX() + 0.5 + lens.getStepX() * along,
                pos.getY() + 0.45 + random.nextDouble() * 0.2, pos.getZ() + 0.5 + lens.getStepZ() * along, 0, 0, 0);
    }
}
