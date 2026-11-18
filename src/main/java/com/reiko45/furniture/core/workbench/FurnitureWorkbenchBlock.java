package com.reiko45.furniture.core.workbench;

import com.reiko45.furniture.core.ShapeUtil;
import com.reiko45.furniture.core.block.FurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Carpenter's bench: turns planks into any wooden furniture piece of that wood. */
public class FurnitureWorkbenchBlock extends FurnitureBlock {
    public static final Component TITLE = Component.translatable("container.reikos_furniture.furniture_workbench");

    public FurnitureWorkbenchBlock(Properties properties) {
        super(properties, ShapeUtil.boxes(ShapeUtil.box(0, 12, 0, 16, 15, 16), ShapeUtil.box(1, 0, 1, 15, 12, 15)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> new WorkbenchMenu(id, inventory, ContainerLevelAccess.create(level, pos)), TITLE));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
