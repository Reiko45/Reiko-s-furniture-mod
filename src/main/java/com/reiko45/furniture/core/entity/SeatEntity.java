package com.reiko45.furniture.core.entity;

import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Invisible entity a player rides while sitting on a {@link SeatBlock}. It removes itself as soon as
 * nobody is sitting on it or the seat block is gone.
 */
public class SeatEntity extends Entity {
    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    private SeatEntity(Level level, BlockPos pos, double height) {
        this(ModEntities.SEAT.get(), level);
        setPos(pos.getX() + 0.5, pos.getY() + height, pos.getZ() + 0.5);
    }

    /**
     * Seats the player on the block at {@code pos}.
     *
     * @param height seat height in blocks above the bottom of the block
     * @return true if the player is now sitting
     */
    public static boolean sit(Level level, BlockPos pos, double height, Player player) {
        if (!level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty()) {
            return false; // somebody is already sitting here
        }
        SeatEntity seat = new SeatEntity(level, pos, height);
        level.addFreshEntity(seat);
        if (!player.startRiding(seat)) {
            seat.discard();
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && (getPassengers().isEmpty()
                || !(level().getBlockState(blockPosition()).getBlock() instanceof SeatBlock))) {
            ejectPassengers();
            discard();
        }
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        return Vec3.ZERO;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        BlockPos pos = blockPosition();
        BlockState state = level().getBlockState(pos);
        Direction front = state.hasProperty(FurnitureBlock.FACING) ? state.getValue(FurnitureBlock.FACING) : Direction.NORTH;
        // Stand up in front of the seat if possible, otherwise to the side or behind it.
        for (Direction dir : new Direction[] {front, front.getClockWise(), front.getCounterClockWise(), front.getOpposite()}) {
            Vec3 spot = DismountHelper.findSafeDismountLocation(passenger.getType(), level(), pos.relative(dir), false);
            if (spot != null) {
                return spot;
            }
        }
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
