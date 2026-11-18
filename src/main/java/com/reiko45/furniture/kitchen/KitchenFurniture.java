package com.reiko45.furniture.kitchen;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.display.DisplayFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlockEntity;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import com.reiko45.furniture.kitchen.appliance.DrinkItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Kitchen section: counter, upper cabinet, sink, kitchen island and glass upper cabinet in every wood
 * type, plus a stove, a fridge and a microwave.
 * Models are in {@code models/block/template/kitchen/}.
 */
public final class KitchenFurniture {
    private static final Room ROOM = Room.KITCHEN;

    // Shapes for a north-facing block, in pixels.
    private static final VoxelShape FULL = boxes(box(0, 0, 0, 16, 16, 16));
    private static final VoxelShape UPPER_CABINET_SHAPE = boxes(box(0, 4, 7.5, 16, 16, 16));
    private static final VoxelShape ISLAND_SHAPE = boxes(box(0, 0, 1.5, 16, 14, 14.5), box(0, 14, 0, 16, 16, 16));

    /** Kitchen counter with a stone top and three rows of storage. Forms corners like stairs. */
    public static final WoodSet<CounterBlock> COUNTER = FurnitureRegistry.woodSet("counter", ROOM,
            wood -> new CounterBlock(wood.properties(), 3, FULL));

    /** Wall cabinet that hangs at the top of the block, two rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> UPPER_CABINET = FurnitureRegistry.woodSet("upper_cabinet", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 2, UPPER_CABINET_SHAPE));

    /** Fills buckets and glass bottles with water. */
    public static final WoodSet<SinkBlock> SINK = FurnitureRegistry.woodSet("sink", ROOM,
            wood -> new SinkBlock(wood.properties(), FULL));

    /** Cooks food like a smoker. */
    public static final DeferredBlock<StoveBlock> STOVE = FurnitureRegistry.register("stove", ROOM,
            () -> new StoveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(13))));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StoveBlockEntity>> STOVE_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("stove",
                    () -> BlockEntityType.Builder.of(StoveBlockEntity::new, STOVE.get()).build(null));

    /** Two-block-tall fridge with six rows of storage. */
    public static final DeferredBlock<FridgeBlock> FRIDGE = FurnitureRegistry.register("fridge", ROOM,
            () -> new FridgeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion(), 6, FULL, FULL));

    /** Cooks food like the stove, twice as fast. */
    public static final DeferredBlock<MicrowaveBlock> MICROWAVE = FurnitureRegistry.register("microwave", ROOM,
            () -> new MicrowaveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(7))));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MicrowaveBlockEntity>> MICROWAVE_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("microwave",
                    () -> BlockEntityType.Builder.of(MicrowaveBlockEntity::new, MICROWAVE.get()).build(null));

    /** Free-standing counter with doors on both sides and three rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> KITCHEN_ISLAND = FurnitureRegistry.woodSet("kitchen_island", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 3, ISLAND_SHAPE));

    /** Wall cabinet with glass doors, two rows of storage. */
    public static final WoodSet<FurnitureStorageBlock> GLASS_UPPER_CABINET = FurnitureRegistry.woodSet("glass_upper_cabinet", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 2, UPPER_CABINET_SHAPE));

    /** Board for a single item, like a loaf of bread or a melon. */
    public static final WoodSet<DisplayFurnitureBlock> CUTTING_BOARD = FurnitureRegistry.woodSet("cutting_board", ROOM,
            wood -> new DisplayFurnitureBlock(wood.properties(), boxes(box(2, 0, 4, 15, 1, 12)),
                    List.of(new DisplaySlot(7.5F, 1, 8, true, 0.45F))));

    /** Hanging lamp with a fabric shade. */
    public static final ColorSet<SwitchableFurnitureBlock> PENDANT_LAMP = FurnitureRegistry.colorSet("pendant_lamp", ROOM,
            color -> new SwitchableFurnitureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .mapColor(color.getMapColor()).strength(0.5F).noOcclusion()
                    .lightLevel(SwitchableFurnitureBlock.lightWhenOn(14)), boxes(box(4, 3.5, 4, 12, 16, 12))));

    /** Whatever is inside is destroyed when the lid closes. */
    public static final DeferredBlock<TrashCanBlock> TRASH_CAN = FurnitureRegistry.register("trash_can", ROOM,
            () -> new TrashCanBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F)
                    .sound(SoundType.METAL).noOcclusion(), boxes(box(3.5, 0, 3.5, 12.5, 12, 12.5))));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrashCanBlockEntity>> TRASH_CAN_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("trash_can",
                    () -> BlockEntityType.Builder.of(TrashCanBlockEntity::new, TRASH_CAN.get()).build(null));

    private static BlockBehaviour.Properties appliance() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL).noOcclusion();
    }

    /** Makes coffee and hot cocoa. */
    public static final DeferredBlock<ApplianceBlock> COFFEE_MACHINE = FurnitureRegistry.register("coffee_machine", ROOM,
            () -> new ApplianceBlock(appliance(), boxes(box(3, 0, 2.5, 13, 14, 13)), ApplianceType.COFFEE_MACHINE));

    /** Toasts bread. */
    public static final DeferredBlock<ApplianceBlock> TOASTER = FurnitureRegistry.register("toaster", ROOM,
            () -> new ApplianceBlock(appliance(), boxes(box(2, 0, 5, 14, 7.5, 11)), ApplianceType.TOASTER));

    /** Blends fruit into smoothies. */
    public static final DeferredBlock<ApplianceBlock> BLENDER = FurnitureRegistry.register("blender", ROOM,
            () -> new ApplianceBlock(appliance(), boxes(box(5, 0, 4.5, 11, 13, 11)), ApplianceType.BLENDER));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ApplianceBlockEntity>> APPLIANCE_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("appliance", () -> BlockEntityType.Builder.of(ApplianceBlockEntity::new,
                    COFFEE_MACHINE.get(), TOASTER.get(), BLENDER.get()).build(null));

    // Food and drink made by the appliances.
    public static final DeferredItem<DrinkItem> COFFEE = ModItems.ITEMS.register("coffee", () -> new DrinkItem(
            new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F)
                    .alwaysEdible().usingConvertsTo(Items.GLASS_BOTTLE)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0), 1.0F).build())));
    public static final DeferredItem<DrinkItem> HOT_COCOA = ModItems.ITEMS.register("hot_cocoa", () -> new DrinkItem(
            new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5F)
                    .alwaysEdible().usingConvertsTo(Items.GLASS_BOTTLE)
                    .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 600, 0), 1.0F).build())));
    public static final DeferredItem<DrinkItem> FRUIT_SMOOTHIE = ModItems.ITEMS.register("fruit_smoothie", () -> new DrinkItem(
            new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F)
                    .alwaysEdible().usingConvertsTo(Items.GLASS_BOTTLE)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F).build())));
    public static final DeferredItem<Item> TOAST = ModItems.ITEMS.register("toast", () -> new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.8F).build())));

    private KitchenFurniture() {
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
