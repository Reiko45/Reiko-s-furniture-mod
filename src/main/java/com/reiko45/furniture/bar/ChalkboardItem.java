package com.reiko45.furniture.bar;

import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.item.FurnitureBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Places a chalkboard, makes its text white and opens the editor, like placing a sign. */
public class ChalkboardItem extends FurnitureBlockItem {
    public ChalkboardItem(Block block, Properties properties, @Nullable Room room) {
        super(block, properties, room);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean updated = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof SignBlockEntity sign && state.getBlock() instanceof SignBlock block) {
            ChalkboardBlock.whiten(sign);
            if (!updated && player != null) {
                block.openTextEdit(player, sign, true);
            }
        }
        return updated;
    }
}
