package com.reiko45.furniture.core.registry;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.entity.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ReikosFurniture.MODID);

    /** Invisible entity that players ride while sitting on chairs, benches and sofas. */
    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT = ENTITIES.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.001F, 0.001F)
                    .noSummon()
                    .clientTrackingRange(10)
                    .build("seat"));

    private ModEntities() {
    }
}
