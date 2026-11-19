package com.reiko45.furniture.core.lighting;

import com.reiko45.furniture.core.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Remembers the lamps a light switch controls. The list comes from the switch item when it is placed
 * and goes back onto the item when the switch is broken.
 */
public class LightSwitchBlockEntity extends BlockEntity {
    private List<BlockPos> lamps = List.of();

    public LightSwitchBlockEntity(BlockPos pos, BlockState state) {
        super(Lighting.LIGHT_SWITCH_BLOCK_ENTITY.get(), pos, state);
    }

    public List<BlockPos> lamps() {
        return lamps;
    }

    /** Keeps lamps within reach of the switch, up to the maximum. */
    public void setLamps(List<BlockPos> lamps) {
        List<BlockPos> kept = new ArrayList<>();
        for (BlockPos lamp : lamps) {
            if (kept.size() < Lighting.MAX_LAMPS && lamp.closerThan(worldPosition, Lighting.MAX_DISTANCE)) {
                kept.add(lamp.immutable());
            }
        }
        this.lamps = List.copyOf(kept);
        setChanged();
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        setLamps(input.getOrDefault(ModDataComponents.LINKED_LAMPS.get(), List.of()));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!lamps.isEmpty()) {
            components.set(ModDataComponents.LINKED_LAMPS.get(), lamps);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, lamps).ifSuccess(list -> tag.put("Lamps", list));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Tag list = tag.get("Lamps");
        lamps = list == null ? List.of() : BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, list).result().orElse(List.of());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Lamps");
    }
}
