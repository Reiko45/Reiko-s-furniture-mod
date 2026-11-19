package com.reiko45.furniture.compat.jade;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.compat.BlockInfo;
import com.reiko45.furniture.core.lighting.LightSwitchBlockEntity;
import com.reiko45.furniture.core.mail.MailboxBlockEntity;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlockEntity;
import com.reiko45.furniture.laundry.LaundryMachineBlockEntity;
import com.reiko45.furniture.office.SafeBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/** Jade support: extra lines in the block tooltip (see {@link BlockInfo}). Loaded by Jade only when it is installed. */
@WailaPlugin
public class FurnitureJadePlugin implements IWailaPlugin {
    static final ResourceLocation UID = ReikosFurniture.id("furniture");

    @Override
    public void register(IWailaCommonRegistration registration) {
        for (Class<?> type : new Class<?>[] {ApplianceBlockEntity.class, LaundryMachineBlockEntity.class, SafeBlockEntity.class,
                MailboxBlockEntity.class, LightSwitchBlockEntity.class}) {
            registration.registerBlockDataProvider(Data.INSTANCE, type);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(Lines.INSTANCE, Block.class);
    }

    enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            BlockInfo.write(accessor.getBlockEntity(), tag);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    enum Lines implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (BlockInfo.isFurniture(accessor.getBlockState())) {
                BlockInfo.lines(accessor.getLevel(), accessor.getPosition(), accessor.getBlockState(), accessor.getBlockEntity(),
                        accessor.getServerData()).forEach(tooltip::add);
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
