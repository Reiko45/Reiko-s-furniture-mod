package com.reiko45.furniture.decor;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

/**
 * A picture frame on the wall. It shows any item; a filled map is shown at the full size of the frame,
 * and nearby players get the map's updates like with an item frame.
 */
public class PictureFrameBlock extends DisplayFurnitureBlock {
    /** The picture area, in pixels from the frame's bottom-left corner. */
    public static final float PICTURE_SIZE = 12;

    public PictureFrameBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(1, 1, 14.5, 15, 15, 16)),
                List.of(new DisplaySlot(8, 3.2F, 14.4F, false, 0.6F)));
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.DISPLAY.get()) {
            return null;
        }
        return (lvl, pos, st, frame) -> sendMap((ServerLevel) lvl, pos, (DisplayBlockEntity) frame);
    }

    /** Every half second, sends the framed map's changes to players within 64 blocks. */
    private static void sendMap(ServerLevel level, BlockPos pos, DisplayBlockEntity frame) {
        if ((level.getGameTime() + pos.hashCode()) % 10 != 0) {
            return;
        }
        ItemStack stack = frame.getItems().get(0);
        MapId id = stack.get(DataComponents.MAP_ID);
        MapItemSavedData data = id == null ? null : MapItem.getSavedData(id, level);
        if (data == null) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().closerThan(pos, 64)) {
                data.tickCarriedBy(player, stack);
                Packet<?> packet = data.getUpdatePacket(id, player);
                if (packet != null) {
                    player.connection.send(packet);
                }
            }
        }
    }
}
