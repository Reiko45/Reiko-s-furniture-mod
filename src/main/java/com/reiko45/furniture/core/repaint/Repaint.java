package com.reiko45.furniture.core.repaint;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.core.stain.Stains;
import com.reiko45.furniture.kids.BunkBedBlock;
import com.reiko45.furniture.kids.BunkTopBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Changes a placed piece instead of crafting a new one. Sneak and right-click furniture with:
 * <ul>
 *   <li>a dye, to stain a wooden piece (a sponge takes the stain off again);</li>
 *   <li>planks of another wood, to make it in that wood;</li>
 *   <li>a dye or wool, to change the color of a fabric piece;</li>
 *   <li>a style's material (a log, iron nugget, gold nugget, bamboo, ...), to turn it into that style.</li>
 * </ul>
 * The piece keeps its facing, its other block state and anything stored in it. Each change uses one item.
 */
public final class Repaint {
    /** Where a block sits among the furniture sets: which piece, style, and wood or color. */
    private record Place(String piece, @Nullable Style style, @Nullable FurnitureWood wood, @Nullable DyeColor color) {
    }

    private static Map<Block, Place> places;
    /** piece -> style (null for classic) -> the set. */
    private static Map<String, Map<Style, WoodSet<?>>> woodSets;
    private static Map<String, WoodSet<?>> classicWoodSets;
    private static Map<String, Map<Style, ColorSet<?>>> colorSets;
    private static Map<String, ColorSet<?>> classicColorSets;
    private static Map<Item, FurnitureWood> planks;

    private Repaint() {
    }

    /** Built on first use, after every block (including furniture in other mods' woods) is registered. */
    private static synchronized void index() {
        if (places != null) {
            return;
        }
        places = new HashMap<>();
        woodSets = new HashMap<>();
        classicWoodSets = new HashMap<>();
        colorSets = new HashMap<>();
        classicColorSets = new HashMap<>();
        planks = new LinkedHashMap<>();
        List<FurnitureWood> woods = new ArrayList<>(List.of(FurnitureWood.values()));
        woods.addAll(FurnitureWood.modded());
        for (FurnitureWood wood : woods) {
            planks.putIfAbsent(wood.planks().asItem(), wood);
        }
        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            String piece = basePiece(set.piece(), set.style());
            if (set.style() == null) {
                classicWoodSets.put(piece, set);
            } else {
                woodSets.computeIfAbsent(piece, p -> new HashMap<>()).put(set.style(), set);
            }
            for (FurnitureWood wood : woods) {
                var block = set.get(wood);
                if (block != null) {
                    places.put(block.get(), new Place(piece, set.style(), wood, null));
                }
            }
        }
        for (ColorSet<?> set : FurnitureRegistry.colorSets()) {
            String piece = basePiece(set.piece(), set.style());
            if (set.style() == null) {
                classicColorSets.put(piece, set);
            } else {
                colorSets.computeIfAbsent(piece, p -> new HashMap<>()).put(set.style(), set);
            }
            for (DyeColor color : DyeColor.values()) {
                places.put(set.get(color).get(), new Place(piece, set.style(), null, color));
            }
        }
    }

    private static String basePiece(String piece, @Nullable Style style) {
        return style == null ? piece : piece.substring(style.id().length() + 1);
    }

    /** The style a material item turns furniture into, or null. */
    @Nullable
    public static Style styleFor(ItemStack stack) {
        if (stack.is(ItemTags.LOGS)) {
            return Style.RUSTIC;
        }
        if (stack.is(Items.IRON_NUGGET)) {
            return Style.MODERN;
        }
        if (stack.is(Items.GOLD_NUGGET)) {
            return Style.ROYAL;
        }
        if (stack.is(Items.BAMBOO)) {
            return Style.JAPANESE;
        }
        if (stack.is(Items.IRON_BARS)) {
            return Style.MEDIEVAL;
        }
        if (stack.is(ItemTags.CANDLES)) {
            return Style.VICTORIAN;
        }
        if (stack.is(Items.GLOWSTONE_DUST)) {
            return Style.FUTURISTIC;
        }
        if (stack.is(Items.SAND) || stack.is(Items.RED_SAND)) {
            return Style.BEACH;
        }
        return null;
    }

    @Nullable
    private static DyeColor colorFor(ItemStack stack) {
        if (stack.getItem() instanceof DyeItem dye) {
            return dye.getDyeColor();
        }
        for (DyeColor color : DyeColor.values()) {
            if (stack.is(BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool")))) {
                return color;
            }
        }
        return null;
    }

    /** What a block becomes when the stack is used on it, or null if the stack doesn't change it. */
    @Nullable
    public static Block target(Block block, ItemStack stack) {
        index();
        Place place = places.get(block);
        if (place == null || stack.isEmpty()) {
            return null;
        }
        Style style = styleFor(stack);
        if (style != null) {
            return find(place.piece(), style, place.wood(), place.color());
        }
        FurnitureWood wood = planks.get(stack.getItem());
        if (wood != null && place.wood() != null) {
            return find(place.piece(), place.style(), wood, null);
        }
        DyeColor color = colorFor(stack);
        if (color != null && place.color() != null) {
            return find(place.piece(), place.style(), null, color);
        }
        return null;
    }

    @Nullable
    private static Block find(String piece, @Nullable Style style, @Nullable FurnitureWood wood, @Nullable DyeColor color) {
        if (wood != null) {
            WoodSet<?> set = style == null ? classicWoodSets.get(piece) : woodSets.getOrDefault(piece, Map.of()).get(style);
            var block = set == null ? null : set.get(wood);
            return block == null ? null : block.get();
        }
        ColorSet<?> set = style == null ? classicColorSets.get(piece) : colorSets.getOrDefault(piece, Map.of()).get(style);
        return set == null ? null : set.get(color).get();
    }

    /** Game bus listener: sneak + right-click furniture with a material. */
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (use(event.getLevel(), event.getPos(), event.getEntity(), event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        }
    }

    /**
     * A sneaking player uses {@code stack} on the block at {@code pos}. Returns true if the stack is a
     * material for this piece (the click is then used up, even if the piece already is that way).
     */
    public static boolean use(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!player.isSecondaryUseActive()) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        Block target = target(state.getBlock(), stack);
        if (target == null) {
            return stain(level, pos, state, player, stack);
        }
        if (target != state.getBlock() && !level.isClientSide) {
            apply(level, pos, target);
            stack.consume(1, player);
            FurnitureActionTrigger.award(player, FurnitureActionTrigger.REPAINT);
        }
        return true;
    }

    /** Dye stains a wooden piece; a sponge takes the stain off. */
    private static boolean stain(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        if (!isWooden(state.getBlock())) {
            return false;
        }
        if (stack.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide && Stains.getPiece(level, pos) != dye.getDyeColor()) {
                Stains.setPiece(level, pos, dye.getDyeColor());
                stack.consume(1, player);
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                FurnitureActionTrigger.award(player, FurnitureActionTrigger.REPAINT);
            }
            return true;
        }
        if (stack.is(Items.SPONGE) && Stains.getPiece(level, pos) != null) {
            if (!level.isClientSide) {
                Stains.setPiece(level, pos, null);
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

    /** Whether a block is a wooden piece (part of a wood set, in any wood). */
    public static boolean isWooden(Block block) {
        index();
        Place place = places.get(block);
        return place != null && place.wood() != null;
    }

    /** Replaces the piece at {@code pos} (all its parts) with {@code target}, keeping its state and contents. */
    public static void apply(Level level, BlockPos pos, Block target) {
        BlockState state = level.getBlockState(pos);
        List<BlockPos> parts = parts(level, pos, state);
        HolderLookup.Provider registries = level.registryAccess();
        Map<BlockPos, BlockState> placed = new LinkedHashMap<>();
        for (BlockPos part : parts) {
            BlockState old = level.getBlockState(part);
            BlockState updated = copyProperties(old, target.defaultBlockState());
            BlockEntity entity = level.getBlockEntity(part);
            CompoundTag data = entity == null ? null : entity.saveWithoutMetadata(registries);
            // Without its block entity the old block has nothing to drop when it is replaced.
            level.removeBlockEntity(part);
            level.setBlock(part, updated, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            BlockEntity replaced = level.getBlockEntity(part);
            if (replaced != null && data != null) {
                replaced.loadWithComponents(data, registries);
                replaced.setChanged();
            }
            placed.put(part, updated);
        }
        // Top bunks follow their bunk bed.
        if (target instanceof BunkBedBlock bunk) {
            for (BlockPos part : parts) {
                BlockState bed = level.getBlockState(part);
                BlockState above = level.getBlockState(part.above());
                if (above.getBlock() instanceof BunkTopBlock) {
                    level.setBlock(part.above(), bunk.topState(bed.getValue(BedBlock.FACING), bed.getValue(BedBlock.PART),
                            bed.getValue(FurnitureBedBlock.COLOR)),
                            Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                }
            }
        }
        if (parts.size() == 1) {
            // Single blocks may join differently now (sofas join only their own style, rugs their own color).
            BlockState joined = Block.updateFromNeighbourShapes(placed.get(pos), level, pos);
            level.setBlock(pos, joined, Block.UPDATE_ALL);
        }
        for (BlockPos part : placed.keySet()) {
            BlockState now = level.getBlockState(part);
            now.updateNeighbourShapes(level, part, Block.UPDATE_ALL);
            level.updateNeighborsAt(part, now.getBlock());
            level.sendBlockUpdated(part, state, now, Block.UPDATE_CLIENTS);
        }
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, pos, target.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
    }

    /** Every block of the piece: both halves of tall pieces and doors, both parts of beds. */
    public static List<BlockPos> parts(Level level, BlockPos pos, BlockState state) {
        List<BlockPos> parts = new ArrayList<>();
        parts.add(pos);
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            BlockPos other = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            if (level.getBlockState(other).is(state.getBlock())) {
                parts.add(other);
            }
        } else if (state.getBlock() instanceof BedBlock && state.hasProperty(BedBlock.PART)) {
            BlockPos other = pos.relative(state.getValue(BedBlock.PART) == BedPart.FOOT
                    ? state.getValue(BedBlock.FACING) : state.getValue(BedBlock.FACING).getOpposite());
            if (level.getBlockState(other).is(state.getBlock())) {
                parts.add(other);
            }
        }
        return parts;
    }

    /**
     * The blocks a piece covers, worked out from its state alone (for when the piece is already being
     * removed): the other half of tall pieces and beds, and the top bunks above a bunk bed.
     */
    public static List<BlockPos> partPositions(BlockPos pos, BlockState state) {
        List<BlockPos> parts = new ArrayList<>();
        parts.add(pos);
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            parts.add(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below());
        } else if (state.getBlock() instanceof BedBlock && state.hasProperty(BedBlock.PART)) {
            parts.add(pos.relative(state.getValue(BedBlock.PART) == BedPart.FOOT
                    ? state.getValue(BedBlock.FACING) : state.getValue(BedBlock.FACING).getOpposite()));
        }
        if (state.getBlock() instanceof BunkBedBlock) {
            for (BlockPos part : List.copyOf(parts)) {
                parts.add(part.above());
            }
        }
        return parts;
    }

    /** {@code target} with every property that {@code from} also has set to the same value. */
    public static BlockState copyProperties(BlockState from, BlockState target) {
        BlockState out = target;
        for (Property<?> property : from.getProperties()) {
            if (out.hasProperty(property)) {
                out = copy(from, out, property);
            }
        }
        return out;
    }

    private static <T extends Comparable<T>> BlockState copy(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}
