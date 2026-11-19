package com.reiko45.furniture.studio;

import com.reiko45.furniture.core.advancement.FurnitureActionTrigger;
import com.reiko45.furniture.core.display.DisplayBlockEntity;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import com.reiko45.furniture.core.registry.ModDataComponents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * An easel. Put a canvas on it, then right-click with an empty hand to paint (sneak to take the canvas
 * off). A signed painting is finished, so right-clicking takes it off.
 */
public class EaselBlock extends DisplayFurnitureBlock {
    /** How far a painter may be from the easel, in blocks. */
    public static final double REACH = 8.0;

    public EaselBlock(Properties properties, VoxelShape northShape) {
        // The canvas is drawn by the display renderer, leaning on the easel.
        super(properties, northShape, List.of(new DisplaySlot(8, 4.5F, 7, false, 0.7F)));
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getItem() instanceof CanvasItem;
    }

    /** The canvas on the easel, or an empty stack. */
    public static ItemStack canvas(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof DisplayBlockEntity display ? display.getItems().get(0) : ItemStack.EMPTY;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack canvas = canvas(level, pos);
        if (!canvas.isEmpty() && !player.isSecondaryUseActive() && !CanvasItem.painting(canvas).signed()) {
            if (level.isClientSide) {
                StudioClient.openPaintScreen(pos, CanvasItem.painting(canvas));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    /**
     * Paints the canvas on an easel (server side), and signs it if a title is given. Does nothing if the
     * player is too far away or the canvas is already signed.
     *
     * @return whether the canvas was changed
     */
    public static boolean paint(Player player, BlockPos pos, String pixels, String title) {
        Level level = player.level();
        if (!(level.getBlockState(pos).getBlock() instanceof EaselBlock) || !Painting.valid(pixels)
                || player.distanceToSqr(pos.getCenter()) > REACH * REACH
                || !(level.getBlockEntity(pos) instanceof DisplayBlockEntity display)) {
            return false;
        }
        ItemStack canvas = display.getItems().get(0);
        if (!(canvas.getItem() instanceof CanvasItem) || CanvasItem.painting(canvas).signed()) {
            return false;
        }
        Painting painting = CanvasItem.painting(canvas).withPixels(pixels);
        String cleanTitle = title.strip();
        if (!cleanTitle.isEmpty()) {
            if (cleanTitle.length() > Painting.MAX_TITLE) {
                cleanTitle = cleanTitle.substring(0, Painting.MAX_TITLE);
            }
            painting = painting.signedBy(cleanTitle, player.getGameProfile().getName());
            FurnitureActionTrigger.award(player, FurnitureActionTrigger.THE_ARTIST);
        }
        ItemStack painted = canvas.copy();
        painted.set(ModDataComponents.PAINTING.get(), painting);
        display.getItems().set(0, painted);
        display.setChanged();
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), Block.UPDATE_CLIENTS);
        return true;
    }
}
