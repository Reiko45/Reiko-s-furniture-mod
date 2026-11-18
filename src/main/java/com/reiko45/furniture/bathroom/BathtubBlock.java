package com.reiko45.furniture.bathroom;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.SeatBlock;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Bathtub. Pieces facing the same way join side by side into a longer tub; each open end gets a wall.
 * Fill it with a water bucket, empty it with a bucket, and right-click with an empty hand to sit in it.
 */
public class BathtubBlock extends SeatBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    private static final VoxelShape BASE = ShapeUtil.boxes(ShapeUtil.box(0, 0, 0, 16, 2, 16),
            ShapeUtil.box(0, 2, 0, 16, 10, 2), ShapeUtil.box(0, 2, 14, 16, 10, 16));
    private static final VoxelShape LEFT_END = ShapeUtil.boxes(ShapeUtil.box(0, 2, 2, 2, 10, 14));
    private static final VoxelShape RIGHT_END = ShapeUtil.boxes(ShapeUtil.box(14, 2, 2, 16, 10, 14));
    private final Map<Part, Map<Direction, VoxelShape>> shapes = new EnumMap<>(Part.class);

    public BathtubBlock(Properties properties) {
        super(properties, BASE, 3);
        for (Part part : Part.values()) {
            VoxelShape shape = BASE;
            if (part.hasLeftEnd()) {
                shape = Shapes.or(shape, LEFT_END);
            }
            if (part.hasRightEnd()) {
                shape = Shapes.or(shape, RIGHT_END);
            }
            shapes.put(part, ShapeUtil.horizontal(shape.optimize()));
        }
        registerDefaultState(defaultBlockState().setValue(PART, Part.SINGLE).setValue(FILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART, FILLED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(PART)).get(state.getValue(FACING));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(PART, partFor(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        return direction.getAxis().isHorizontal() ? updated.setValue(PART, partFor(updated, level, pos)) : updated;
    }

    private static Part partFor(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        boolean left = isTub(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
        boolean right = isTub(level.getBlockState(pos.relative(facing.getClockWise())), facing);
        return left && right ? Part.MIDDLE : right ? Part.LEFT : left ? Part.RIGHT : Part.SINGLE;
    }

    private static boolean isTub(BlockState state, Direction facing) {
        return state.getBlock() instanceof BathtubBlock && state.getValue(FACING) == facing;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        boolean filled = state.getValue(FILLED);
        if (stack.is(Items.WATER_BUCKET) && !filled) {
            return exchange(level, pos, state, player, hand, stack, new ItemStack(Items.BUCKET), true);
        }
        if (stack.is(Items.BUCKET) && filled) {
            return exchange(level, pos, state, player, hand, stack, new ItemStack(Items.WATER_BUCKET), false);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    private static ItemInteractionResult exchange(Level level, BlockPos pos, BlockState state, Player player,
                                                  InteractionHand hand, ItemStack used, ItemStack result, boolean fill) {
        if (!level.isClientSide) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(used, player, result));
            level.setBlock(pos, state.setValue(FILLED, fill), Block.UPDATE_ALL);
            level.playSound(null, pos, fill ? SoundEvents.BUCKET_EMPTY : SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Which piece of a (long) tub a block is. The left end has the left wall, and so on. */
    public enum Part implements StringRepresentable {
        SINGLE("single"),
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        public boolean hasLeftEnd() {
            return this == SINGLE || this == LEFT;
        }

        public boolean hasRightEnd() {
            return this == SINGLE || this == RIGHT;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
