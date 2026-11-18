package com.reiko45.furniture.laundry;

import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.registry.ModSounds;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
 * Washing machine or dryer: one row of storage. Close the door with items inside and it runs a cycle:
 * the washer takes the dye out of things, the dryer dries wet sponges.
 */
public class LaundryMachineBlock extends FurnitureStorageBlock {
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    /** What a machine does to each item. */
    public enum Kind {
        WASHER(LaundryMachineBlockEntity::wash, () -> ModSounds.WASHER_RUNNING.get()),
        DRYER(LaundryMachineBlockEntity::dry, () -> ModSounds.DRYER_RUNNING.get());

        private final UnaryOperator<ItemStack> process;
        private final java.util.function.Supplier<SoundEvent> sound;

        Kind(UnaryOperator<ItemStack> process, java.util.function.Supplier<SoundEvent> sound) {
            this.process = process;
            this.sound = sound;
        }

        /** The item after a cycle; the same stack if nothing changes. */
        public ItemStack process(ItemStack stack) {
            return process.apply(stack);
        }

        public SoundEvent sound() {
            return sound.get();
        }
    }

    private final Kind kind;

    public LaundryMachineBlock(Properties properties, VoxelShape northShape, Kind kind) {
        super(properties, 1, northShape);
        this.kind = kind;
        registerDefaultState(defaultBlockState().setValue(RUNNING, false));
    }

    public Kind kind() {
        return kind;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RUNNING);
    }

    @Override
    public boolean usesSharedStorage() {
        return false;
    }

    @Override
    public SoundEvent openSound() {
        return SoundEvents.IRON_TRAPDOOR_OPEN;
    }

    @Override
    public SoundEvent closeSound() {
        return SoundEvents.IRON_TRAPDOOR_CLOSE;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LaundryMachineBlockEntity(pos, state);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != LaundryFurniture.LAUNDRY_MACHINE_BLOCK_ENTITY.get()) {
            return null;
        }
        return (lvl, pos, st, machine) -> LaundryMachineBlockEntity.serverTick(lvl, pos, st, (LaundryMachineBlockEntity) machine);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (state.getValue(RUNNING)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.reikos_furniture.laundry_busy"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }
}
