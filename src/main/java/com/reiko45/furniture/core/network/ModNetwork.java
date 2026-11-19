package com.reiko45.furniture.core.network;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.mail.MailboxMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** The mod's network messages. Handlers run on the main thread. */
public final class ModNetwork {
    private ModNetwork() {
    }

    /** Client to server: send the parcel in the open mailbox screen to the named player. */
    public record SendMail(String recipient) implements CustomPacketPayload {
        public static final Type<SendMail> TYPE = new Type<>(ReikosFurniture.id("send_mail"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SendMail> STREAM_CODEC =
                ByteBufCodecs.stringUtf8(64).<RegistryFriendlyByteBuf>cast().map(SendMail::new, SendMail::recipient);

        @Override
        public Type<SendMail> type() {
            return TYPE;
        }

        static void handle(SendMail message, IPayloadContext context) {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof MailboxMenu menu) {
                menu.send(player, message.recipient());
            }
        }
    }

    /** Mod bus listener. */
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SendMail.TYPE, SendMail.STREAM_CODEC, SendMail::handle);
    }
}
