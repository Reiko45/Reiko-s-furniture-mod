package com.reiko45.furniture.core.network;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.catalog.CatalogCrafting;
import com.reiko45.furniture.core.mail.MailboxMenu;
import com.reiko45.furniture.studio.EaselBlock;
import com.reiko45.furniture.studio.Painting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

    /** Client to server: craft a piece from the catalog (once, or as many as possible). */
    public record CatalogCraft(ResourceLocation recipe, boolean all) implements CustomPacketPayload {
        public static final Type<CatalogCraft> TYPE = new Type<>(ReikosFurniture.id("catalog_craft"));
        public static final StreamCodec<RegistryFriendlyByteBuf, CatalogCraft> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, CatalogCraft::recipe, ByteBufCodecs.BOOL, CatalogCraft::all, CatalogCraft::new);

        @Override
        public Type<CatalogCraft> type() {
            return TYPE;
        }

        static void handle(CatalogCraft message, IPayloadContext context) {
            if (context.player() instanceof ServerPlayer player) {
                CatalogCrafting.craft(player, message.recipe(), message.all());
            }
        }
    }

    /** Client to server: the pixels painted on the canvas on an easel, and a title to sign it with (or ""). */
    public record PaintCanvas(BlockPos easel, String pixels, String title) implements CustomPacketPayload {
        public static final Type<PaintCanvas> TYPE = new Type<>(ReikosFurniture.id("paint_canvas"));
        public static final StreamCodec<RegistryFriendlyByteBuf, PaintCanvas> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, PaintCanvas::easel,
                ByteBufCodecs.stringUtf8(Painting.SIZE * Painting.SIZE), PaintCanvas::pixels,
                ByteBufCodecs.stringUtf8(Painting.MAX_TITLE * 4), PaintCanvas::title,
                PaintCanvas::new);

        @Override
        public Type<PaintCanvas> type() {
            return TYPE;
        }

        static void handle(PaintCanvas message, IPayloadContext context) {
            EaselBlock.paint(context.player(), message.easel(), message.pixels(), message.title());
        }
    }

    /** Mod bus listener. */
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SendMail.TYPE, SendMail.STREAM_CODEC, SendMail::handle);
        registrar.playToServer(CatalogCraft.TYPE, CatalogCraft.STREAM_CODEC, CatalogCraft::handle);
        registrar.playToServer(PaintCanvas.TYPE, PaintCanvas.STREAM_CODEC, PaintCanvas::handle);
    }
}
