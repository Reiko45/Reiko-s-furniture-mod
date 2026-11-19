package com.reiko45.furniture.core.mail;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import com.reiko45.furniture.core.registry.ModMenus;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Mailboxes: send items to other players, who find them in any of their mailboxes. */
public final class Mail {
    private static final VoxelShape SHAPE = boxes(box(7, 0, 7, 9, 10, 9), box(4, 10, 3, 12, 15, 13));

    public static final WoodSet<MailboxBlock> MAILBOX = FurnitureRegistry.woodSet("mailbox", Room.OUTDOOR,
            wood -> new MailboxBlock(wood.properties(), SHAPE));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MailboxBlockEntity>> MAILBOX_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("mailbox", () -> BlockEntityType.Builder.of(MailboxBlockEntity::new,
                    MAILBOX.all().stream().map(Holder::value).toArray(Block[]::new)).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<MailboxMenu>> MENU =
            ModMenus.MENUS.register("mailbox", () -> IMenuTypeExtension.create(MailboxMenu::new));

    private Mail() {
    }

    public static void init() {
    }
}
