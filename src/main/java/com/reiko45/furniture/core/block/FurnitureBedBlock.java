package com.reiko45.furniture.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * A wooden bed that works like a vanilla bed (sleep through the night, set your spawn point).
 * It is drawn with normal block models, and the blanket color is part of the block state:
 * right-click with any dye to recolor it.
 */
public class FurnitureBedBlock extends BedBlock {
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public FurnitureBedBlock(Properties properties) {
        super(DyeColor.RED, properties);
        registerDefaultState(defaultBlockState().setValue(COLOR, DyeColor.RED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof DyeItem dye && state.getValue(COLOR) != dye.getDyeColor()) {
            if (!level.isClientSide) {
                DyeColor color = dye.getDyeColor();
                level.setBlock(pos, state.setValue(COLOR, color), Block.UPDATE_ALL);
                BlockPos otherPos = pos.relative(getConnectedDirection(state));
                BlockState other = level.getBlockState(otherPos);
                if (other.is(this)) {
                    level.setBlock(otherPos, other.setValue(COLOR, color), Block.UPDATE_ALL);
                }
                onRecolor(level, pos, otherPos, color);
                stack.consume(1, player);
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    /** Called after both halves were dyed. Bunk beds use this to recolor the top bunk too. */
    protected void onRecolor(Level level, BlockPos pos, BlockPos otherPos, DyeColor color) {
    }
}
