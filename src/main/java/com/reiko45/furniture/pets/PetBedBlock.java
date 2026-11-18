package com.reiko45.furniture.pets;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A cushioned pet bed. Cats sit on it like on a vanilla bed (it is in the {@code minecraft:beds} tag),
 * and idle tamed wolves walk over and lie down on it ({@link RestOnPetBedGoal}). Players can't sleep in it.
 */
public class PetBedBlock extends FurnitureBlock {
    public PetBedBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    /** It's low enough to step onto, so mobs may walk over it (other furniture blocks their path). */
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.LAND;
    }
}
