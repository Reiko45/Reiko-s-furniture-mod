package com.reiko45.furniture.core.mail;

import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A mailbox on a post. It belongs to whoever placed it (or, if nobody did, the first player to open
 * it). The owner reads their mail here; anyone can send mail from it, to the owner or anyone else
 * with a mailbox. The red flag is up while there is mail.
 */
public class MailboxBlock extends FurnitureBlock implements EntityBlock {
    public static final BooleanProperty FLAG = BooleanProperty.create("flag");

    public MailboxBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
        registerDefaultState(defaultBlockState().setValue(FLAG, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLAG);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player && level.getBlockEntity(pos) instanceof MailboxBlockEntity mailbox) {
            claim(level, mailbox, player);
        }
    }

    private static void claim(Level level, MailboxBlockEntity mailbox, Player player) {
        mailbox.setOwner(player.getUUID(), player.getGameProfile().getName());
        if (level.getServer() != null) {
            PostOffice.get(level.getServer()).addOwner(player.getUUID());
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)
                || !(level.getBlockEntity(pos) instanceof MailboxBlockEntity mailbox) || level.getServer() == null) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (mailbox.owner() == null) {
            claim(level, mailbox, player);
        }
        boolean owner = player.getUUID().equals(mailbox.owner());
        String ownerName = mailbox.ownerName();
        PostOffice office = PostOffice.get(level.getServer());
        serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, p) -> new MailboxMenu(id, inventory, owner, ownerName,
                owner ? office.inboxContainer(player.getUUID()) : new SimpleContainer(PostOffice.INBOX_SIZE),
                ContainerLevelAccess.create(level, pos)), Component.translatable("container.reikos_furniture.mailbox", ownerName)),
                buf -> {
                    buf.writeBoolean(owner);
                    buf.writeUtf(ownerName);
                });
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MailboxBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != Mail.MAILBOX_BLOCK_ENTITY.get() ? null
                : (lvl, pos, st, entity) -> MailboxBlockEntity.serverTick(lvl, pos, st, (MailboxBlockEntity) entity);
    }
}
