package com.reiko45.furniture.core.block;

import com.reiko45.furniture.core.blockentity.FurnitureStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Furniture with an inventory (drawers, cabinets, wardrobes, ...). Right-click opens a chest-style
 * screen with {@link #rows()} rows of 9 slots. While someone has it open, {@link #OPEN} is true so the
 * model can show open doors or drawers. Contents drop when the block is broken.
 */
public class FurnitureStorageBlock extends FurnitureBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private final int rows;

    /**
     * @param rows number of 9-slot rows, from 1 to 6
     */
    public FurnitureStorageBlock(Properties properties, int rows, VoxelShape northShape) {
        super(properties, northShape);
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Storage rows must be between 1 and 6, got " + rows);
        }
        this.rows = rows;
        registerDefaultState(defaultBlockState().setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    public int rows() {
        return rows;
    }

    /** Whether this piece uses the shared storage block entity type (the trash can and safe have their own). */
    public boolean usesSharedStorage() {
        return true;
    }

    public SoundEvent openSound() {
        return SoundEvents.BARREL_OPEN;
    }

    public SoundEvent closeSound() {
        return SoundEvents.BARREL_CLOSE;
    }

    /** Position of the block that holds the inventory. Two-block-tall pieces keep it in the lower half. */
    public BlockPos storagePos(BlockState state, BlockPos pos) {
        return pos;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FurnitureStorageBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide
                && level.getBlockEntity(storagePos(state, pos)) instanceof FurnitureStorageBlockEntity storage) {
            player.openMenu(storage);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Scheduled by the block entity to check whether anyone still has the inventory open.
        if (level.getBlockEntity(pos) instanceof FurnitureStorageBlockEntity storage) {
            storage.recheckOpen();
        }
    }

    /** Whether the contents spill out when it is broken. Presents keep them inside instead, like shulker boxes. */
    protected boolean dropsContents() {
        return true;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (dropsContents()) {
            Containers.dropContentsOnDestroy(state, newState, level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(storagePos(state, pos)));
    }
}
