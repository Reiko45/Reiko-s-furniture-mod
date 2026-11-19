package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.mail.Mail;
import com.reiko45.furniture.core.mail.MailboxBlock;
import com.reiko45.furniture.core.mail.MailboxBlockEntity;
import com.reiko45.furniture.core.mail.MailboxMenu;
import com.reiko45.furniture.core.mail.PostOffice;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class MailGameTests {
    private static PostOffice office(GameTestHelper helper) {
        return PostOffice.get(helper.getLevel().getServer());
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void mailArrivesInTheInbox(GameTestHelper helper) {
        UUID recipient = UUID.randomUUID();
        PostOffice office = office(helper);
        office.addOwner(recipient);
        SimpleContainer parcel = new SimpleContainer(new ItemStack(Items.DIAMOND, 3), new ItemStack(Items.COOKIE, 10));
        helper.assertTrue(office.deliver(recipient, parcel), "everything delivered");
        helper.assertTrue(parcel.isEmpty(), "the parcel is empty");
        Container inbox = office.inboxContainer(recipient);
        helper.assertTrue(inbox.getItem(0).is(Items.DIAMOND) && inbox.getItem(0).getCount() == 3, "diamonds in the inbox");
        helper.assertTrue(office.hasMail(recipient), "has mail");

        // A second parcel of cookies joins the cookies already there.
        office.deliver(recipient, new SimpleContainer(new ItemStack(Items.COOKIE, 5)));
        helper.assertValueEqual(inbox.getItem(1).getCount(), 15, "cookies stacked");
        inbox.removeItemNoUpdate(0);
        inbox.removeItemNoUpdate(1);
        helper.assertFalse(office.hasMail(recipient), "taken out");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void fullInboxKeepsTheRest(GameTestHelper helper) {
        UUID recipient = UUID.randomUUID();
        PostOffice office = office(helper);
        office.addOwner(recipient);
        SimpleContainer stone = new SimpleContainer(PostOffice.INBOX_SIZE);
        for (int i = 0; i < PostOffice.INBOX_SIZE; i++) {
            stone.setItem(i, new ItemStack(Items.STONE, 64));
        }
        office.deliver(recipient, stone);
        SimpleContainer parcel = new SimpleContainer(new ItemStack(Items.EMERALD, 4));
        helper.assertFalse(office.deliver(recipient, parcel), "the inbox is full");
        helper.assertValueEqual(parcel.getItem(0).getCount(), 4, "the emeralds stay in the parcel");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void postOfficeIsSaved(GameTestHelper helper) {
        UUID recipient = UUID.randomUUID();
        PostOffice office = new PostOffice();
        office.addOwner(recipient);
        office.deliver(recipient, new SimpleContainer(new ItemStack(Items.MAP, 2)));
        var registries = helper.getLevel().registryAccess();
        PostOffice loaded = PostOffice.load(office.save(new CompoundTag(), registries), registries);
        helper.assertTrue(loaded.isOwner(recipient), "owner remembered");
        helper.assertTrue(loaded.inboxContainer(recipient).getItem(0).is(Items.MAP), "mail remembered");
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY, timeoutTicks = 200)
    public static void flagGoesUpForMail(GameTestHelper helper) {
        UUID owner = UUID.randomUUID();
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, Mail.MAILBOX.get(FurnitureWood.OAK).get());
        MailboxBlockEntity mailbox = helper.getBlockEntity(pos);
        mailbox.setOwner(owner, "Someone");
        office(helper).addOwner(owner);
        office(helper).deliver(owner, new SimpleContainer(new ItemStack(Items.PAPER)));
        helper.succeedWhen(() -> helper.assertBlockProperty(pos, MailboxBlock.FLAG, true));
    }

    @GameTest(template = CoreGameTests.EMPTY)
    @SuppressWarnings("removal") // a mock server player is needed to send by name
    public static void sendingByNameFromTheMailbox(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        office(helper).addOwner(player.getUUID());
        MailboxMenu menu = new MailboxMenu(1, player.getInventory(), true, player.getGameProfile().getName(),
                office(helper).inboxContainer(player.getUUID()), ContainerLevelAccess.NULL);
        // The parcel slots come right after the 27 inbox slots.
        menu.getSlot(PostOffice.INBOX_SIZE).set(new ItemStack(Items.APPLE, 6));
        menu.send(player, player.getGameProfile().getName());
        helper.assertTrue(office(helper).inboxContainer(player.getUUID()).getItem(0).is(Items.APPLE), "sent to yourself");
        helper.assertTrue(menu.getSlot(PostOffice.INBOX_SIZE).getItem().isEmpty(), "parcel sent");

        menu.getSlot(PostOffice.INBOX_SIZE).set(new ItemStack(Items.APPLE, 1));
        menu.send(player, "nobody-has-this-name");
        helper.assertValueEqual(menu.getSlot(PostOffice.INBOX_SIZE).getItem().getCount(), 1, "unknown players get nothing");
        helper.succeed();
    }
}
