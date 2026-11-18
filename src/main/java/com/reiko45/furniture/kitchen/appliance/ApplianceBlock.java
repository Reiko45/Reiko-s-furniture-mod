package com.reiko45.furniture.kitchen.appliance;

import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.kitchen.KitchenFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Coffee machine, toaster or blender. Right-click with ingredients to put them in (one at a time);
 * once they match a recipe it runs and the result pops out. Right-click with an empty hand to take
 * the ingredients back out before it starts.
 */
public class ApplianceBlock extends FurnitureBlock implements EntityBlock {
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");
    private final ApplianceType type;

    public ApplianceBlock(Properties properties, VoxelShape northShape, ApplianceType type) {
        super(properties, northShape);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(RUNNING, false));
    }

    public ApplianceType type() {
        return type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RUNNING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ApplianceBlockEntity(pos, state);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != KitchenFurniture.APPLIANCE_BLOCK_ENTITY.get()) {
            return null;
        }
        return (lvl, pos, st, appliance) -> ApplianceBlockEntity.serverTick(lvl, pos, st, (ApplianceBlockEntity) appliance);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ApplianceBlockEntity appliance && appliance.accepts(level, stack)) {
            if (!level.isClientSide && appliance.add(level, stack, player)) {
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.2F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ApplianceBlockEntity appliance && !appliance.isRunning() && !appliance.isEmpty()) {
            if (!level.isClientSide) {
                for (ItemStack stack : appliance.takeAll()) {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ApplianceBlockEntity appliance) {
            appliance.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(RUNNING) && type != ApplianceType.BLENDER && random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.WHITE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 0, 0.02, 0);
        }
    }
}
