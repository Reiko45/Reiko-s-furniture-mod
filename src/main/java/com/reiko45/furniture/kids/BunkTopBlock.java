package com.reiko45.furniture.kids;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The top bunk of a {@link BunkBedBlock}. It has no item of its own: it is placed and removed together
 * with the bed below, and you can stand on it but not sleep in it.
 */
public class BunkTopBlock extends Block {
    private static final VoxelShape SHAPE = ShapeUtil.boxes(ShapeUtil.box(0, 4, 0, 16, 9, 16),
            ShapeUtil.box(0, 0, 0, 2, 14, 2), ShapeUtil.box(14, 0, 0, 16, 14, 2),
            ShapeUtil.box(0, 0, 14, 2, 14, 16), ShapeUtil.box(14, 0, 14, 16, 14, 16));
    private final Supplier<? extends Block> bed;

    public BunkTopBlock(Properties properties, Supplier<? extends Block> bed) {
        super(properties);
        this.bed = bed;
        registerDefaultState(stateDefinition.any());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BedBlock.FACING, BedBlock.PART, FurnitureBedBlock.COLOR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // Without its bed underneath, the top bunk disappears.
        if (direction == Direction.DOWN && !(neighborState.getBlock() instanceof BunkBedBlock)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            // In creative mode, remove the bed below without dropping it.
            BlockPos below = pos.below();
            BlockState bedState = level.getBlockState(below);
            if (bedState.getBlock() instanceof BunkBedBlock) {
                BlockPos other = below.relative(BedBlock.getConnectedDirection(bedState));
                level.setBlock(other, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // Breaking the top bunk breaks the bunk bed below (which drops the item). Swapping it for
        // another top bunk (a different wood or style) keeps the bed.
        if (!(newState.getBlock() instanceof BunkTopBlock) && level.getBlockState(pos.below()).getBlock() instanceof BunkBedBlock) {
            level.destroyBlock(pos.below(), true);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(bed.get());
    }
}
