package com.reiko45.furniture.kitchen;

import com.mojang.serialization.MapCodec;
import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Microwave: cooks food like the stove (smoker recipes, uses fuel), but twice as fast. */
public class MicrowaveBlock extends AbstractFurnaceBlock {
    public static final MapCodec<MicrowaveBlock> CODEC = simpleCodec(MicrowaveBlock::new);
    private static final Map<Direction, VoxelShape> SHAPES =
            ShapeUtil.horizontal(ShapeUtil.boxes(ShapeUtil.box(1.5, 0, 2, 14.5, 8, 13)));

    public MicrowaveBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<MicrowaveBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MicrowaveBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        // Run the furnace logic twice per tick: everything happens twice as fast.
        return createTickerHelper(type, KitchenFurniture.MICROWAVE_BLOCK_ENTITY.get(), (lvl, pos, st, microwave) -> {
            int before = microwave.getItem(2).getCount();
            AbstractFurnaceBlockEntity.serverTick(lvl, pos, st, microwave);
            AbstractFurnaceBlockEntity.serverTick(lvl, pos, lvl.getBlockState(pos), microwave);
            if (microwave.getItem(2).getCount() > before) {
                lvl.playSound(null, pos, ModSounds.MICROWAVE_DING.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
            }
        });
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof MicrowaveBlockEntity microwave) {
            player.openMenu(microwave);
            player.awardStat(Stats.INTERACT_WITH_SMOKER);
        }
    }
}
