package com.reiko45.furniture.theater;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * A projector screen three blocks wide and two tall, placed and broken as one piece (like a bed). The
 * item places the bottom middle; the rest fills in to its sides and above. A {@link ProjectorBlock}
 * pointed at it lights it up with one of {@link #FILMS} films, each spread over the six parts.
 */
public class ProjectorScreenBlock extends FurnitureBlock {
    public static final int FILMS = 3;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final IntegerProperty FILM = IntegerProperty.create("film", 1, FILMS);

    /** The six parts, by column (left to right as seen by the audience) and row. */
    public enum Part implements StringRepresentable {
        BOTTOM_LEFT(-1, 0), BOTTOM_MIDDLE(0, 0), BOTTOM_RIGHT(1, 0), TOP_LEFT(-1, 1), TOP_MIDDLE(0, 1), TOP_RIGHT(1, 1);

        private final int column;
        private final int row;

        Part(int column, int row) {
            this.column = column;
            this.row = row;
        }

        public int column() {
            return column;
        }

        public int row() {
            return row;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Nullable
        static Part at(int column, int row) {
            for (Part part : values()) {
                if (part.column == column && part.row == row) {
                    return part;
                }
            }
            return null;
        }
    }

    public ProjectorScreenBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 0, 14, 16, 16, 16)));
        registerDefaultState(defaultBlockState().setValue(PART, Part.BOTTOM_MIDDLE).setValue(LIT, false).setValue(FILM, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART, LIT, FILM);
    }

    /** One step to the audience's right, for a screen facing this way. */
    private static Direction right(Direction facing) {
        return facing.getCounterClockWise();
    }

    /** Where a part is, given where the bottom middle is. */
    public static BlockPos partPos(BlockPos main, Direction facing, Part part) {
        return main.relative(right(facing), part.column()).above(part.row());
    }

    /** Where the bottom middle is, given any part of the screen. */
    public static BlockPos mainPos(BlockPos pos, BlockState state) {
        Part part = state.getValue(PART);
        return pos.relative(right(state.getValue(FACING)), -part.column()).below(part.row());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        Level level = context.getLevel();
        BlockPos main = context.getClickedPos();
        if (main.getY() + 1 >= level.getMaxBuildHeight()) {
            return null;
        }
        for (Part part : Part.values()) {
            BlockPos pos = partPos(main, state.getValue(FACING), part);
            if (part != Part.BOTTOM_MIDDLE && !level.getBlockState(pos).canBeReplaced(context)) {
                return null;
            }
        }
        return state.setValue(PART, Part.BOTTOM_MIDDLE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) {
            return;
        }
        for (Part part : Part.values()) {
            if (part != Part.BOTTOM_MIDDLE) {
                BlockPos partPos = partPos(pos, state.getValue(FACING), part);
                level.setBlock(partPos, state.setValue(PART, part)
                        .setValue(WATERLOGGED, level.getFluidState(partPos).getType() == Fluids.WATER), Block.UPDATE_ALL);
            }
        }
    }

    /** A part disappears when one of its neighbouring parts is gone, so the whole screen goes at once. */
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level,
                                     BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighbor, level, pos, neighborPos);
        Part part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        int column = part.column();
        int row = part.row();
        if (direction == right(facing)) {
            column++;
        } else if (direction == right(facing).getOpposite()) {
            column--;
        } else if (direction == Direction.UP) {
            row++;
        } else if (direction == Direction.DOWN) {
            row--;
        } else {
            return updated;
        }
        Part expected = Part.at(column, row);
        if (expected != null && !(neighbor.is(this) && neighbor.getValue(PART) == expected && neighbor.getValue(FACING) == facing)) {
            return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return updated;
    }

    /** In creative mode, breaking any part must not drop the item from the bottom middle. */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && state.getValue(PART) != Part.BOTTOM_MIDDLE) {
            BlockPos main = mainPos(pos, state);
            BlockState mainState = level.getBlockState(main);
            if (mainState.is(this)) {
                level.setBlock(main, mainState.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(),
                        Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, main, Block.getId(mainState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /** Shows a film on every part of the screen (or blanks it when {@code film} is 0). */
    public static void show(Level level, BlockPos anyPart, int film) {
        BlockState state = level.getBlockState(anyPart);
        if (!(state.getBlock() instanceof ProjectorScreenBlock)) {
            return;
        }
        BlockPos main = mainPos(anyPart, state);
        Direction facing = state.getValue(FACING);
        for (Part part : Part.values()) {
            BlockPos pos = partPos(main, facing, part);
            BlockState partState = level.getBlockState(pos);
            if (partState.getBlock() instanceof ProjectorScreenBlock) {
                BlockState shown = film > 0 ? partState.setValue(LIT, true).setValue(FILM, film) : partState.setValue(LIT, false);
                if (shown != partState) {
                    level.setBlock(pos, shown, Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}
