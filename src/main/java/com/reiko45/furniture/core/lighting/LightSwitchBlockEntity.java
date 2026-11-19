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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Remembers the lamps a light switch controls. The list comes from the switch item when it is placed
 * and goes back onto the item when the switch is broken.
 */
public class LightSwitchBlockEntity extends BlockEntity {
    /** How far a switch with {@code AutoLink} looks for lamps. */
    public static final int AUTO_LINK_RANGE = 6;

    private List<BlockPos> lamps = List.of();
    /**
     * Set on switches in structures (like the village workshop), whose lamp positions can't be saved in
     * advance: the first time the switch is used, it links the lamps around it.
     */
    private boolean autoLink;

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

    public boolean autoLink() {
        return autoLink;
    }

    /** Links every lamp within {@link #AUTO_LINK_RANGE} blocks (up to the maximum), and stops auto-linking. */
    public void linkNearby(Level level) {
        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-AUTO_LINK_RANGE, -AUTO_LINK_RANGE, -AUTO_LINK_RANGE),
                worldPosition.offset(AUTO_LINK_RANGE, AUTO_LINK_RANGE, AUTO_LINK_RANGE))) {
            BlockState state = level.getBlockState(pos);
            if (Lighting.isLamp(state)) {
                BlockPos lamp = Lighting.lampPos(state, pos).immutable();
                if (!found.contains(lamp)) {
                    found.add(lamp);
                }
            }
        }
        autoLink = false;
        setLamps(found);
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
        if (autoLink) {
            tag.putBoolean("AutoLink", true);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Tag list = tag.get("Lamps");
        lamps = list == null ? List.of() : BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, list).result().orElse(List.of());
        autoLink = tag.getBoolean("AutoLink");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Lamps");
    }
}
