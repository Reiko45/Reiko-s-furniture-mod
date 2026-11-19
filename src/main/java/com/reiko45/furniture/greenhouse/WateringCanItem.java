package com.reiko45.furniture.greenhouse;

import com.reiko45.furniture.bathroom.BathtubBlock;
import com.reiko45.furniture.core.registry.ModDataComponents;
import com.reiko45.furniture.kitchen.SinkBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * A watering can. Fill it from water, a sink, a filled bathtub or a water cauldron; then right-click
 * plants to water everything in a 3 by 3 area: crops, saplings and other plants get an extra growth
 * tick, farmland gets wet, and growing pots grow a stage. Holds {@link #CAPACITY} waterings.
 */
public class WateringCanItem extends Item {
    public static final int CAPACITY = 16;
    private static final int BAR_COLOR = 0x3F76E4;

    public WateringCanItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static int water(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.WATER.get(), 0);
    }

    public static void setWater(ItemStack stack, int water) {
        stack.set(ModDataComponents.WATER.get(), Mth.clamp(water, 0, CAPACITY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK && level.getFluidState(hit.getBlockPos()).is(FluidTags.WATER)
                && water(stack) < CAPACITY) {
            fill(level, hit.getBlockPos(), player, stack);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        // Water in front of the block takes precedence (aiming at the bottom of a pond fills the can).
        if (player != null) {
            BlockHitResult fluid = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (fluid.getType() == HitResult.Type.BLOCK && level.getFluidState(fluid.getBlockPos()).is(FluidTags.WATER)) {
                if (water(stack) < CAPACITY) {
                    fill(level, fluid.getBlockPos(), player, stack);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                return InteractionResult.PASS;
            }
        }
        boolean source = state.getBlock() instanceof SinkBlock
                || state.getBlock() instanceof BathtubBlock && state.getValue(BathtubBlock.FILLED)
                || state.is(Blocks.WATER_CAULDRON);
        if (source) {
            if (water(stack) >= CAPACITY) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide && state.is(Blocks.WATER_CAULDRON)) {
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            }
            fill(level, pos, player, stack);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (water(stack) <= 0) {
            if (player != null && !level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.reikos_furniture.watering_can_empty"), true);
            }
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            pour(level, pos, player);
            if (player == null || !player.getAbilities().instabuild) {
                setWater(stack, water(stack) - 1);
            }
        }
        if (player != null) {
            player.getCooldowns().addCooldown(this, 8);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void fill(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!level.isClientSide) {
            setWater(stack, CAPACITY);
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 0.8F, 1.2F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        }
    }

    /** Waters the 3 by 3 area around a block: the block's own layer, the layer above and the one below. */
    public static void pour(Level level, BlockPos center, Player player) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-1, -1, -1), center.offset(1, 1, 1))) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof FarmBlock && state.getValue(FarmBlock.MOISTURE) < FarmBlock.MAX_MOISTURE) {
                level.setBlock(pos, state.setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), Block.UPDATE_CLIENTS);
            } else if (block instanceof GrowingPotBlock) {
                GrowingPotBlock.grow(level, pos.immutable(), player);
            } else if (level instanceof ServerLevel server && state.isRandomlyTicking() && grows(block)) {
                state.randomTick(server, pos.immutable(), server.random);
            }
        }
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SPLASH, center.getX() + 0.5, center.getY() + 1.1, center.getZ() + 0.5,
                    24, 0.9, 0.1, 0.9, 0.0);
        }
        level.playSound(null, center, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0F, 0.8F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, center);
    }

    /** Plants that watering helps: crops, saplings, flowers, berries, stems, sugar cane, cactus and bamboo. */
    private static boolean grows(Block block) {
        return block instanceof BushBlock || block instanceof SugarCaneBlock || block instanceof CactusBlock
                || block instanceof BambooStalkBlock || block instanceof BambooSaplingBlock;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return water(stack) < CAPACITY;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * water(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.reikos_furniture.watering_can.water", water(stack), CAPACITY)
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.reikos_furniture.watering_can.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
