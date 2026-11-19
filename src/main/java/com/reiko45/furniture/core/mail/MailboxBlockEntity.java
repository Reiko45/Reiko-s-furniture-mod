package com.reiko45.furniture.core.mail;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Who a mailbox belongs to. Every two seconds it raises or lowers the flag for the owner's mail. */
public class MailboxBlockEntity extends BlockEntity {
    @Nullable
    private UUID owner;
    private String ownerName = "";

    public MailboxBlockEntity(BlockPos pos, BlockState state) {
        super(Mail.MAILBOX_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    public UUID owner() {
        return owner;
    }

    public String ownerName() {
        return ownerName;
    }

    public void setOwner(UUID owner, String name) {
        this.owner = owner;
        this.ownerName = name;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MailboxBlockEntity mailbox) {
        if (level.getGameTime() % 40 != 0 || mailbox.owner == null || level.getServer() == null) {
            return;
        }
        boolean mail = PostOffice.get(level.getServer()).hasMail(mailbox.owner);
        if (state.getValue(MailboxBlock.FLAG) != mail) {
            level.setBlock(pos, state.setValue(MailboxBlock.FLAG, mail), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (owner != null) {
            tag.putUUID("Owner", owner);
            tag.putString("OwnerName", ownerName);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        ownerName = tag.getString("OwnerName");
    }
}
