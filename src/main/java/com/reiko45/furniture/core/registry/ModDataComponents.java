package com.reiko45.furniture.core.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.reiko45.furniture.ReikosFurniture;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Item data components added by the mod. */
public final class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ReikosFurniture.MODID);

    /**
     * Furniture on a moving dolly: the block (lower half for tall pieces) and its saved block entity
     * (inventory, safe owner, ...).
     */
    public record CarriedFurniture(BlockState state, CompoundTag data) {
        public static final Codec<CarriedFurniture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("state").forGetter(CarriedFurniture::state),
                CompoundTag.CODEC.fieldOf("data").forGetter(CarriedFurniture::data)).apply(instance, CarriedFurniture::new));
        public static final StreamCodec<ByteBuf, CarriedFurniture> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CarriedFurniture::state,
                ByteBufCodecs.COMPOUND_TAG, CarriedFurniture::data,
                CarriedFurniture::new);
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CarriedFurniture>> CARRIED_FURNITURE =
            COMPONENTS.registerComponentType("carried_furniture",
                    builder -> builder.persistent(CarriedFurniture.CODEC).networkSynchronized(CarriedFurniture.STREAM_CODEC));

    private ModDataComponents() {
    }
}
