package com.reiko45.furniture.compat.wthit;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.compat.BlockInfo;
import com.reiko45.furniture.core.lighting.LightSwitchBlockEntity;
import com.reiko45.furniture.core.mail.MailboxBlockEntity;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlockEntity;
import com.reiko45.furniture.laundry.LaundryMachineBlockEntity;
import com.reiko45.furniture.office.SafeBlockEntity;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * WTHIT support: the same extra tooltip lines as with Jade (see {@link BlockInfo}). Listed in
 * {@code waila_plugins.json}, so WTHIT loads it only when installed.
 */
public class FurnitureWthitPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addFeatureConfig(ReikosFurniture.id("furniture"), true);
        for (Class<?> type : new Class<?>[] {ApplianceBlockEntity.class, LaundryMachineBlockEntity.class, SafeBlockEntity.class,
                MailboxBlockEntity.class, LightSwitchBlockEntity.class}) {
            registrar.addBlockData(Data.INSTANCE, type);
        }
        registrar.addComponent(Lines.INSTANCE, TooltipPosition.BODY, Block.class);
    }

    enum Data implements IDataProvider<BlockEntity> {
        INSTANCE;

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
            BlockInfo.write(accessor.getTarget(), data.raw());
        }
    }

    enum Lines implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (BlockInfo.isFurniture(accessor.getBlockState())) {
                BlockInfo.lines(accessor.getLevel(), accessor.getPosition(), accessor.getBlockState(), accessor.getBlockEntity(),
                        accessor.getData().raw()).forEach(tooltip::addLine);
            }
        }
    }
}
