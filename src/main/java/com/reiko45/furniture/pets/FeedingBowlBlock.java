package com.reiko45.furniture.pets;

import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Feeding bowl: fill it with meat or fish (up to 16). Every few seconds, a hurt tamed wolf or cat
 * nearby eats one and heals.
 */
public class FeedingBowlBlock extends DisplayFurnitureBlock {
    public static final int HEAL = 4;

    public FeedingBowlBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape, List.of(new DisplaySlot(8, 1.2F, 8, true, 0.45F)));
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.is(ItemTags.MEAT) || stack.is(ItemTags.FISHES);
    }

    @Override
    public int maxPerSlot() {
        return 16;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.DISPLAY.get()) {
            return null;
        }
        return (lvl, pos, st, bowl) -> feed((ServerLevel) lvl, pos, (DisplayBlockEntity) bowl);
    }

    private static void feed(ServerLevel level, BlockPos pos, DisplayBlockEntity bowl) {
        if (level.getGameTime() % 40 != 0) {
            return;
        }
        ItemStack food = bowl.getItems().get(0);
        if (food.isEmpty()) {
            return;
        }
        List<TamableAnimal> hungry = level.getEntitiesOfClass(TamableAnimal.class, new AABB(pos).inflate(6),
                pet -> pet.isTame() && (pet instanceof Wolf || pet instanceof Cat) && pet.getHealth() < pet.getMaxHealth());
        if (hungry.isEmpty()) {
            return;
        }
        TamableAnimal pet = hungry.get(0);
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, food), pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5,
                6, 0.15, 0.05, 0.15, 0.02);
        pet.heal(HEAL);
        bowl.shrink(0);
        level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0.8F, 1.0F);
    }
}
