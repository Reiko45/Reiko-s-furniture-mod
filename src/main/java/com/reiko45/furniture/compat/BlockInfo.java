package com.reiko45.furniture.compat;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.lighting.LightSwitchBlockEntity;
import com.reiko45.furniture.core.mail.MailboxBlockEntity;
import com.reiko45.furniture.core.mail.PostOffice;
import com.reiko45.furniture.core.stain.Stains;
import com.reiko45.furniture.greenhouse.GrowingPotBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlockEntity;
import com.reiko45.furniture.laundry.LaundryMachineBlockEntity;
import com.reiko45.furniture.nursery.AquariumBlockEntity;
import com.reiko45.furniture.office.SafeBlockEntity;
import com.reiko45.furniture.studio.CanvasBlockEntity;
import com.reiko45.furniture.studio.Painting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the block tooltip mods (Jade, WTHIT) show for furniture, shared by both plugins: appliance and
 * laundry progress, safe and mailbox owners, mail waiting, linked lamps, aquarium fish, how far a pot has
 * grown, the wood stain and a painting's title.
 */
public final class BlockInfo {
    private BlockInfo() {
    }

    /** Whether a block is one of the mod's. */
    public static boolean isFurniture(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(ReikosFurniture.MODID);
    }

    /** Server side: the data the client can't see on its own, written into the tag sent to it. */
    public static void write(BlockEntity blockEntity, CompoundTag tag) {
        if (blockEntity instanceof ApplianceBlockEntity appliance && appliance.isRunning() && appliance.total() > 0) {
            tag.putInt("Progress", appliance.progress() * 100 / appliance.total());
        }
        if (blockEntity instanceof LaundryMachineBlockEntity laundry && laundry.timer() > 0) {
            tag.putInt("Timer", laundry.timer());
        }
        if (blockEntity instanceof SafeBlockEntity safe && !safe.ownerName().isEmpty()) {
            tag.putString("Owner", safe.ownerName());
        }
        if (blockEntity instanceof MailboxBlockEntity mailbox && mailbox.owner() != null) {
            tag.putString("Owner", mailbox.ownerName());
            if (blockEntity.getLevel() instanceof ServerLevel level) {
                tag.putInt("Mail", PostOffice.get(level.getServer()).mailCount(mailbox.owner()));
            }
        }
        if (blockEntity instanceof LightSwitchBlockEntity lightSwitch) {
            tag.putInt("Lamps", lightSwitch.lamps().size());
        }
    }

    /** Client side: the tooltip lines for a block, from its state, its synced block entity and the server's data. */
    public static List<Component> lines(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, CompoundTag data) {
        List<Component> lines = new ArrayList<>();
        if (data.contains("Progress")) {
            lines.add(Component.translatable("tooltip.reikos_furniture.progress", data.getInt("Progress")));
        }
        if (data.contains("Timer")) {
            lines.add(Component.translatable("tooltip.reikos_furniture.done_in", (data.getInt("Timer") + 19) / 20));
        }
        if (data.contains("Owner")) {
            lines.add(Component.translatable("tooltip.reikos_furniture.owner", data.getString("Owner")));
        }
        if (data.contains("Mail")) {
            lines.add(Component.translatable("tooltip.reikos_furniture.mail", data.getInt("Mail")));
        }
        if (data.contains("Lamps")) {
            lines.add(Component.translatable("tooltip.reikos_furniture.lamps", data.getInt("Lamps")));
        }
        if (blockEntity instanceof AquariumBlockEntity aquarium) {
            lines.add(Component.translatable("tooltip.reikos_furniture.fish", aquarium.count(), 3));
        }
        if (state.getBlock() instanceof GrowingPotBlock && GrowingPotBlock.hasPlant(level, pos)) {
            lines.add(Component.translatable("tooltip.reikos_furniture.growth", state.getValue(GrowingPotBlock.GROWTH), GrowingPotBlock.FULL));
        }
        if (blockEntity instanceof CanvasBlockEntity canvas && canvas.painting().signed()) {
            Painting painting = canvas.painting();
            lines.add(Component.translatable("tooltip.reikos_furniture.painting", painting.title(), painting.author()));
        }
        DyeColor stain = Stains.getPiece(level, pos);
        if (stain != null) {
            lines.add(Component.translatable("item.reikos_furniture.stain",
                    Component.translatable("color.minecraft." + stain.getName())).withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }
}
