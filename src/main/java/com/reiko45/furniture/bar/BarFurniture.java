package com.reiko45.furniture.bar;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import com.reiko45.furniture.kitchen.appliance.DrinkItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** Home bar section: bar counters and wine racks in every wood, bar stools in every color, an espresso machine and a chalkboard. */
public final class BarFurniture {
    private static final Room ROOM = Room.BAR;

    /** Three rows of storage behind the bar; joins and turns corners like kitchen counters. */
    public static final WoodSet<CounterBlock> BAR_COUNTER = FurnitureRegistry.woodSet("bar_counter", ROOM,
            wood -> new CounterBlock(wood.properties(), 3, boxes(box(0, 0, 0, 16, 16, 16))));

    public static final ColorSet<SeatBlock> BAR_STOOL = FurnitureRegistry.colorSet("bar_stool", ROOM,
            color -> new SeatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.8F).noOcclusion(), boxes(box(4, 0, 4, 12, 13.5, 12)), 13.5));

    /** Shows up to nine bottles. */
    public static final WoodSet<WineRackBlock> WINE_RACK = FurnitureRegistry.woodSet("wine_rack", ROOM,
            wood -> new WineRackBlock(wood.properties()));

    /** Makes espresso and lattes. */
    public static final DeferredBlock<ApplianceBlock> ESPRESSO_MACHINE = FurnitureRegistry.register("espresso_machine", ROOM,
            () -> new ApplianceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)
                    .noOcclusion(), boxes(box(2, 0, 1.5, 14, 12, 14)), ApplianceType.ESPRESSO_MACHINE));

    /** Write on it like a sign. */
    public static final DeferredBlock<ChalkboardBlock> CHALKBOARD = FurnitureRegistry.register("chalkboard", ROOM,
            () -> new ChalkboardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission().strength(1.0F)
                    .sound(SoundType.WOOD)),
            (block, room) -> new ChalkboardItem(block, new Item.Properties().stacksTo(16), room));

    /** A quick shot of energy: Haste for a minute. */
    public static final DeferredItem<DrinkItem> ESPRESSO = ModItems.ITEMS.register("espresso", () -> new DrinkItem(
            new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.2F)
                    .alwaysEdible().usingConvertsTo(Items.GLASS_BOTTLE)
                    .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0), 1.0F).build())));

    /** Milky and filling. */
    public static final DeferredItem<DrinkItem> LATTE = ModItems.ITEMS.register("latte", () -> new DrinkItem(
            new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.8F)
                    .alwaysEdible().usingConvertsTo(Items.GLASS_BOTTLE)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0), 1.0F).build())));

    private BarFurniture() {
    }

    public static void init() {
    }
}
