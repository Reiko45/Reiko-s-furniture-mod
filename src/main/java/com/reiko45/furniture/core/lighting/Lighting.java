package com.reiko45.furniture.core.lighting;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureTags;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Light switches: link lamps to a switch (sneak and right-click them with the switch in hand), place
 * the switch on a wall, and one click turns them all on or off. Lamps are the blocks in
 * {@link FurnitureTags#LAMPS}.
 */
public final class Lighting {
    /** How many lamps one switch can control, and how far away they may be. */
    public static final int MAX_LAMPS = 32;
    public static final int MAX_DISTANCE = 32;

    public static final DeferredBlock<LightSwitchBlock> LIGHT_SWITCH = FurnitureRegistry.register("light_switch", Room.DECOR,
            () -> new LightSwitchBlock(BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(0.5F).sound(SoundType.STONE)
                    .noCollission().noOcclusion(), boxes(box(5, 4, 15, 11, 12, 16))),
            (block, room) -> new LightSwitchItem(block, new Item.Properties(), room));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightSwitchBlockEntity>> LIGHT_SWITCH_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("light_switch", () -> BlockEntityType.Builder.of(LightSwitchBlockEntity::new,
                    LIGHT_SWITCH.get()).build(null));

    private Lighting() {
    }

    public static void init() {
    }

    /** Whether the block at {@code pos} is a lamp a switch can control. */
    public static boolean isLamp(BlockState state) {
        return state.is(FurnitureTags.LAMPS) && state.hasProperty(SwitchableFurnitureBlock.LIT);
    }

    /** Lamps are remembered by their lower block (for two-block-tall floor lamps). */
    public static BlockPos lampPos(BlockState state, BlockPos pos) {
        return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    /**
     * Switches the linked lamps: off if any of them is on, otherwise all on. Lamps that are gone or not
     * loaded are skipped. Returns whether they are now on.
     */
    public static boolean toggle(Level level, List<BlockPos> lamps) {
        boolean anyOn = lamps.stream().anyMatch(pos -> level.isLoaded(pos) && isLamp(level.getBlockState(pos))
                && level.getBlockState(pos).getValue(SwitchableFurnitureBlock.LIT));
        for (BlockPos pos : lamps) {
            if (level.isLoaded(pos)) {
                setLit(level, pos, !anyOn);
            }
        }
        return !anyOn;
    }

    private static void setLit(Level level, BlockPos pos, boolean lit) {
        BlockState state = level.getBlockState(pos);
        if (!isLamp(state) || state.getValue(SwitchableFurnitureBlock.LIT) == lit) {
            return;
        }
        level.setBlock(pos, state.setValue(SwitchableFurnitureBlock.LIT, lit), Block.UPDATE_ALL);
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            BlockPos upper = pos.above();
            BlockState other = level.getBlockState(upper);
            if (other.is(state.getBlock()) && other.hasProperty(SwitchableFurnitureBlock.LIT)) {
                level.setBlock(upper, other.setValue(SwitchableFurnitureBlock.LIT, lit), Block.UPDATE_ALL);
            }
        }
    }
}
