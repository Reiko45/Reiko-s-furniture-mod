package com.reiko45.furniture.theater;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** A framed movie poster on the wall. Right-click to change to the next of {@link #DESIGNS} posters. */
public class MoviePosterBlock extends FurnitureBlock {
    public static final int DESIGNS = 4;
    public static final IntegerProperty DESIGN = IntegerProperty.create("design", 0, DESIGNS - 1);

    public MoviePosterBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(2, 0, 15, 14, 16, 16)));
        registerDefaultState(defaultBlockState().setValue(DESIGN, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DESIGN);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.cycle(DESIGN), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
