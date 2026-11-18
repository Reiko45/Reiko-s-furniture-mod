package com.reiko45.furniture.core.catalog;

import com.reiko45.furniture.core.registry.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredItem;

/** The furniture catalog: a book listing every piece by room and style, with its recipe. */
public final class Catalog {
    public static final DeferredItem<CatalogItem> CATALOG = ModItems.ITEMS.register("furniture_catalog",
            () -> new CatalogItem(new Item.Properties().stacksTo(1)));

    private Catalog() {
    }

    public static void init() {
    }

    public static class CatalogItem extends Item {
        public CatalogItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            if (level.isClientSide && FMLEnvironment.dist.isClient()) {
                CatalogClient.open();
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("item.reikos_furniture.furniture_catalog.tooltip").withStyle(ChatFormatting.GRAY));
        }
    }
}
