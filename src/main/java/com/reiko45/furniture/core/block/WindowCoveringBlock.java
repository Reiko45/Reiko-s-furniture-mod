package com.reiko45.furniture.core.block;

import com.reiko45.furniture.core.ShapeUtil;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Blinds and shutters: hang in front of a window, right-click to open or close. While closed they
 * block light completely, so a room can be made dark in the daytime.
 */
public class WindowCoveringBlock extends FurnitureBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private final Map<Direction, VoxelShape> openShapes;
    private final SoundEvent sound;

    public WindowCoveringBlock(Properties properties, VoxelShape closedShape, VoxelShape openShape, SoundEvent sound) {
        super(properties, closedShape);
        this.openShapes = ShapeUtil.horizontal(openShape);
        this.sound = sound;
        registerDefaultState(defaultBlockState().setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? openShapes.get(state.getValue(FACING)) : super.getShape(state, level, pos, context);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN) ? 0 : 15;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.cycle(OPEN), Block.UPDATE_ALL);
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.7F, state.getValue(OPEN) ? 0.9F : 1.1F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
