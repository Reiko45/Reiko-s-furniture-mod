package com.reiko45.furniture.greenhouse;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import com.reiko45.furniture.core.display.DisplaySlot;
import com.reiko45.furniture.core.registry.ModItems;
import java.util.List;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Greenhouse and garden: plant stands, window flower boxes and garden shed cabinets in every wood;
 * growing pots (terracotta and 16 glazes) and hanging planters in every color; and the watering can.
 */
public final class GreenhouseFurniture {
    private static final Room ROOM = Room.GREENHOUSE;

    private static final VoxelShape POT = boxes(box(2, 0, 2, 14, 10, 14));
    private static final List<DisplaySlot> POT_SLOT = List.of(DisplaySlot.plant(8, 9.5F, 8, 1.0F));

    /** A wooden stand with a small pot on top for one plant. */
    public static final WoodSet<PlantHolderBlock> PLANT_STAND = FurnitureRegistry.woodSet("plant_stand", ROOM,
            wood -> new PlantHolderBlock(wood.properties(), boxes(box(2, 0, 2, 14, 10.5, 14), box(4.5, 10.5, 4.5, 11.5, 15.5, 11.5)),
                    List.of(DisplaySlot.plant(8, 15, 8, 0.6F))));

    /** A big pot whose plant grows bigger over time. */
    public static final DeferredBlock<GrowingPotBlock> TERRACOTTA_GROWING_POT = FurnitureRegistry.register("terracotta_growing_pot", ROOM,
            () -> new GrowingPotBlock(pot(MapColor.TERRACOTTA_ORANGE), POT, POT_SLOT));

    public static final ColorSet<GrowingPotBlock> GROWING_POT = FurnitureRegistry.colorSet("growing_pot", ROOM,
            color -> new GrowingPotBlock(pot(terracottaColor(color)), POT, POT_SLOT), false);

    /** A pot on cords that hangs from the ceiling. */
    public static final ColorSet<PlantHolderBlock> HANGING_PLANTER = FurnitureRegistry.colorSet("hanging_planter", ROOM,
            color -> new PlantHolderBlock(pot(terracottaColor(color)), boxes(box(3.5, 2, 3.5, 12.5, 16, 12.5)),
                    List.of(DisplaySlot.plant(8, 8.5F, 8, 0.55F))), false);

    /** A box of soil on the wall under a window, for three flowers. */
    public static final WoodSet<PlantHolderBlock> WINDOW_FLOWER_BOX = FurnitureRegistry.woodSet("window_flower_box", ROOM,
            wood -> new PlantHolderBlock(wood.properties(), boxes(box(0, 0, 9, 16, 6, 16)),
                    List.of(DisplaySlot.plant(3.5F, 5, 12.5F, 0.6F), DisplaySlot.plant(8, 5, 12.5F, 0.6F),
                            DisplaySlot.plant(12.5F, 5, 12.5F, 0.6F))));

    /** Two blocks tall, with six rows of storage for tools and seeds. */
    public static final WoodSet<TallStorageFurnitureBlock> GARDEN_SHED_CABINET = FurnitureRegistry.woodSet("garden_shed_cabinet", ROOM,
            wood -> new TallStorageFurnitureBlock(wood.properties(), 6, boxes(box(0, 0, 0.5, 16, 16, 16)),
                    boxes(box(0, 0, 0, 16, 15, 16))));

    public static final DeferredItem<WateringCanItem> WATERING_CAN = ModItems.ITEMS.register("watering_can",
            () -> new WateringCanItem(new Item.Properties()));

    private GreenhouseFurniture() {
    }

    public static void init() {
    }

    private static BlockBehaviour.Properties pot(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(0.5F).sound(SoundType.DECORATED_POT).noOcclusion();
    }

    /** The map color of a dye's terracotta. */
    private static MapColor terracottaColor(DyeColor color) {
        return switch (color) {
            case WHITE -> MapColor.TERRACOTTA_WHITE;
            case ORANGE -> MapColor.TERRACOTTA_ORANGE;
            case MAGENTA -> MapColor.TERRACOTTA_MAGENTA;
            case LIGHT_BLUE -> MapColor.TERRACOTTA_LIGHT_BLUE;
            case YELLOW -> MapColor.TERRACOTTA_YELLOW;
            case LIME -> MapColor.TERRACOTTA_LIGHT_GREEN;
            case PINK -> MapColor.TERRACOTTA_PINK;
            case GRAY -> MapColor.TERRACOTTA_GRAY;
            case LIGHT_GRAY -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case CYAN -> MapColor.TERRACOTTA_CYAN;
            case PURPLE -> MapColor.TERRACOTTA_PURPLE;
            case BLUE -> MapColor.TERRACOTTA_BLUE;
            case BROWN -> MapColor.TERRACOTTA_BROWN;
            case GREEN -> MapColor.TERRACOTTA_GREEN;
            case RED -> MapColor.TERRACOTTA_RED;
            case BLACK -> MapColor.TERRACOTTA_BLACK;
        };
    }
}
