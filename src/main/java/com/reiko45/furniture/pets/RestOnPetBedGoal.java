package com.reiko45.furniture.pets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

/** Makes an idle tamed animal (wolves) walk to a nearby pet bed and lie down on it for a while. */
public class RestOnPetBedGoal extends MoveToBlockGoal {
    private final TamableAnimal pet;
    private int restTicks;

    public RestOnPetBedGoal(TamableAnimal pet) {
        super(pet, 1.0, 8, 2);
        this.pet = pet;
    }

    @Override
    public boolean canUse() {
        return pet.isTame() && !pet.isOrderedToSit() && pet.getTarget() == null && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return restTicks < 600 && pet.isTame() && !pet.isOrderedToSit() && pet.getTarget() == null && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof PetBedBlock;
    }

    /** Lie in the bed itself, not on top of the block above it. */
    @Override
    protected BlockPos getMoveToTarget() {
        return blockPos;
    }

    @Override
    public void start() {
        super.start();
        restTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        pet.setInSittingPose(false);
    }

    @Override
    public void tick() {
        super.tick();
        if (isReachedTarget()) {
            pet.setInSittingPose(true);
            restTicks++;
        } else {
            pet.setInSittingPose(false);
        }
    }
}
