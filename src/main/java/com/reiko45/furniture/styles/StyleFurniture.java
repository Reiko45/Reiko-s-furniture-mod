package com.reiko45.furniture.styles;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.FurnitureBedBlock;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import com.reiko45.furniture.kids.BunkBedBlock;
import com.reiko45.furniture.kids.BunkTopBlock;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.livingroom.FloorLampBlock;
import com.reiko45.furniture.livingroom.SofaBlock;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * The furniture styles: Rustic, Modern, Royal, Japanese, Medieval, Victorian, Futuristic and Beach House
 * versions of the bed, bunk bed, desk, bookshelf, table and floor lamps, kitchen counter and upper cabinet, dining
 * chair and table, coffee table, nightstand, dresser, wardrobe, sofa and armchair. They work like
 * the classic pieces and are listed in their own creative tab.
 */
public final class StyleFurniture {
    /** The pieces of one style. Ids are {@code <wood>_<style>_<piece>} and {@code <color>_<style>_<piece>}. */
    public record Pieces(Style style,
                         WoodSet<FurnitureBedBlock> bed,
                         WoodSet<BunkBedBlock> bunkBed,
                         WoodSet<BunkTopBlock> bunkBedTop,
                         WoodSet<SeatBlock> diningChair,
                         WoodSet<ConnectingTableBlock> diningTable,
                         WoodSet<ConnectingTableBlock> coffeeTable,
                         WoodSet<FurnitureStorageBlock> nightstand,
                         WoodSet<FurnitureStorageBlock> dresser,
                         WoodSet<TallStorageFurnitureBlock> wardrobe,
                         ColorSet<SofaBlock> sofa,
                         ColorSet<SeatBlock> armchair,
                         WoodSet<FurnitureBlock> desk,
                         WoodSet<FurnitureStorageBlock> bookshelf,
                         WoodSet<SwitchableFurnitureBlock> tableLamp,
                         WoodSet<FloorLampBlock> floorLamp,
                         WoodSet<CounterBlock> counter,
                         WoodSet<FurnitureStorageBlock> upperCabinet) {
    }

    /** Outlines and seat heights for one style's pieces (in pixels, front facing north). */
    private record Shapes(@Nullable VoxelShape bed, VoxelShape chair, double chairSeat,
                          VoxelShape tableTop, VoxelShape tableLeg, VoxelShape coffeeTop, VoxelShape coffeeLeg,
                          VoxelShape nightstand, VoxelShape dresser, VoxelShape wardrobeLower, VoxelShape wardrobeUpper,
                          double sofaSeat, VoxelShape armchair, double armchairSeat) {
    }

    private static final VoxelShape NIGHTSTAND = boxes(box(1, 0, 1, 15, 14, 15), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape DRESSER = boxes(box(0.5, 0, 1, 15.5, 14, 15.5), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape WARDROBE_LOWER = boxes(box(0, 0, 0.5, 16, 16, 16));
    private static final VoxelShape WARDROBE_UPPER = boxes(box(0.5, 0, 0.5, 15.5, 14, 16), box(0, 14, 0, 16, 16, 16));
    private static final VoxelShape ARMCHAIR = boxes(box(1, 0, 1, 15, 10, 15), box(2.5, 10, 12, 13.5, 15, 15));
    // The shapes of the second set of pieces are the same in every style.
    private static final VoxelShape DESK = boxes(box(0, 14, 0, 16, 16, 16), box(1, 0, 1, 4, 14, 15),
            box(9, 0, 0.5, 15, 14, 15), box(3, 6, 13.5, 9, 14, 14.5));
    private static final VoxelShape FULL = boxes(box(0, 0, 0, 16, 16, 16));
    private static final VoxelShape TABLE_LAMP = boxes(box(4.5, 0, 4.5, 11.5, 11, 11.5));
    private static final VoxelShape FLOOR_LAMP_LOWER = boxes(box(4.5, 0, 4.5, 11.5, 1.5, 11.5), box(7, 1.5, 7, 9, 16, 9));
    private static final VoxelShape FLOOR_LAMP_UPPER = boxes(box(7, 0, 7, 9, 6, 9), box(3.5, 6, 3.5, 12.5, 15, 12.5));
    private static final VoxelShape UPPER_CABINET = boxes(box(0, 4, 7.5, 16, 16, 16));

    private static final Map<Style, Shapes> SHAPES = new EnumMap<>(Style.class);

    static {
        // Rustic: chunky log legs and thick tops.
        SHAPES.put(Style.RUSTIC, new Shapes(null,
                boxes(box(2, 0, 2, 14, 9, 14), box(2, 9, 12, 14, 16, 14.5)), 9,
                boxes(box(0, 13, 0, 16, 16, 16)), boxes(box(1, 0, 1, 4, 13, 4)),
                boxes(box(0, 6, 0, 16, 9, 16)), boxes(box(1, 0, 1, 4, 6, 4)),
                NIGHTSTAND, DRESSER, WARDROBE_LOWER, WARDROBE_UPPER,
                8, ARMCHAIR, 7));
        // Modern: low platform bed, thin legs, thin tops, flat fronts.
        SHAPES.put(Style.MODERN, new Shapes(boxes(box(0, 0, 0, 16, 8, 16)),
                boxes(box(2.5, 0, 2.5, 13.5, 9, 13.5), box(2.5, 9, 12, 13.5, 16, 13.5)), 9,
                boxes(box(0, 15, 0, 16, 16, 16)), boxes(box(1, 0, 1, 2.5, 15, 2.5)),
                boxes(box(0, 6, 0, 16, 7.5, 16)), boxes(box(1, 0, 1, 2.5, 6, 2.5)),
                boxes(box(1, 0, 1.5, 15, 13, 15)), boxes(box(0, 0, 1, 16, 13, 16)),
                WARDROBE_LOWER, boxes(box(0, 0, 0.5, 16, 16, 16)),
                7, boxes(box(1, 0, 1, 15, 9, 15), box(1, 9, 12, 15, 14, 15)), 7));
        // Royal: carved legs and tall, padded backs.
        SHAPES.put(Style.ROYAL, new Shapes(null,
                boxes(box(2, 0, 2, 14, 9, 14), box(2.5, 9, 12.5, 13.5, 16, 14)), 9,
                boxes(box(0, 14, 0, 16, 16, 16)), boxes(box(1, 0, 1, 3.5, 14, 3.5)),
                boxes(box(0, 7, 0, 16, 9, 16)), boxes(box(1, 0, 1, 3.5, 7, 3.5)),
                NIGHTSTAND, DRESSER, WARDROBE_LOWER, WARDROBE_UPPER,
                8, boxes(box(1, 0, 1, 15, 10, 15), box(1, 10, 11.5, 15, 16, 15)), 7));
        // Japanese: a futon on a low platform, floor chairs, low tables, tansu chests and a shoji wardrobe.
        SHAPES.put(Style.JAPANESE, new Shapes(boxes(box(0, 0, 0, 16, 6, 16)),
                boxes(box(2, 0, 2, 14, 4, 14), box(2, 4, 11.5, 14, 12, 13.5)), 4,
                boxes(box(0, 7, 0, 16, 8.5, 16)), boxes(box(1.5, 0, 1.5, 3, 7, 3)),
                boxes(box(0, 4.5, 0, 16, 6, 16)), boxes(box(0.5, 0, 0.5, 3, 4.5, 3)),
                boxes(box(1, 0, 1.5, 15, 10, 15)), boxes(box(0, 0, 1, 16, 10, 16), box(8, 10, 1, 16, 16, 16)),
                WARDROBE_LOWER, boxes(box(0, 0, 0.5, 16, 16, 16)),
                5, boxes(box(1, 0, 1, 15, 5, 15), box(1, 5, 11.5, 15, 12, 15)), 5));
        // Medieval: a four-poster bed, heavy chairs, trestle tables and iron-strapped storage.
        SHAPES.put(Style.MEDIEVAL, new Shapes(null,
                boxes(box(1.5, 0, 1.5, 14.5, 9, 14.5), box(2, 9, 12, 14, 16, 14.5)), 9,
                boxes(box(0, 13, 0, 16, 16, 16)), boxes(box(1, 0, 0.5, 4.5, 13, 5)),
                boxes(box(0, 6, 0, 16, 9, 16)), boxes(box(1, 0, 1, 4, 6, 4)),
                NIGHTSTAND, DRESSER, WARDROBE_LOWER, WARDROBE_UPPER,
                7, boxes(box(1, 0, 1, 15, 12, 15), box(1, 12, 12, 15, 16, 15)), 7));
        // Victorian: a sleigh bed, turned legs, marble tops and button-tufted upholstery.
        SHAPES.put(Style.VICTORIAN, new Shapes(null,
                boxes(box(1.5, 0, 1.5, 14.5, 9.5, 14.5), box(2.5, 9.5, 12.5, 13.5, 16, 14)), 9.5,
                boxes(box(0, 13, 0, 16, 16, 16)), boxes(box(1, 0, 1, 3.5, 13, 3.5)),
                boxes(box(0, 6.5, 0, 16, 9, 16)), boxes(box(1, 0, 1, 3.5, 6.5, 3.5)),
                NIGHTSTAND, DRESSER, WARDROBE_LOWER, WARDROBE_UPPER,
                7.5, boxes(box(1, 0, 1, 15, 10, 15), box(1, 10, 9, 15, 16, 15)), 7.5));
        // Futuristic: a floating platform bed, pod chairs and white shells on chrome.
        SHAPES.put(Style.FUTURISTIC, new Shapes(boxes(box(0, 0, 0, 16, 8, 16)),
                boxes(box(2, 0, 2, 14, 9, 14), box(2, 9, 11.5, 14, 16, 14)), 9,
                boxes(box(0, 14.5, 0, 16, 16, 16)), boxes(box(1, 0, 1, 2.5, 14.5, 2.5)),
                boxes(box(0, 6, 0, 16, 7.5, 16)), boxes(box(1, 0, 1, 2.5, 6, 2.5)),
                boxes(box(1, 0, 1.5, 15, 13, 15)), boxes(box(0, 0, 1, 16, 13, 16)),
                WARDROBE_LOWER, boxes(box(0, 0, 0.5, 16, 16, 16)),
                7, boxes(box(1.5, 0, 1.5, 14.5, 13, 14.5), box(1.5, 13, 12, 14.5, 16, 14.5)), 8));
        // Beach house: whitewashed slats, wicker and striped cushions.
        SHAPES.put(Style.BEACH, new Shapes(null,
                boxes(box(2, 0, 2, 14, 9.5, 14), box(2, 9.5, 12, 14, 16, 14.25)), 9.5,
                boxes(box(0, 13.5, 0, 16, 16, 16)), boxes(box(1, 0, 1, 3, 13.5, 3)),
                boxes(box(0, 6, 0, 16, 8.5, 16)), boxes(box(1, 0, 1, 3, 6, 3)),
                NIGHTSTAND, DRESSER, WARDROBE_LOWER, WARDROBE_UPPER,
                7, boxes(box(1, 0, 1, 15, 10, 15), box(1, 10, 11, 15, 15, 15)), 7));
    }

    private static final Map<Style, Pieces> PIECES = new EnumMap<>(Style.class);

    static {
        for (Style style : Style.values()) {
            PIECES.put(style, register(style, SHAPES.get(style)));
        }
    }

    private StyleFurniture() {
    }

    private static Pieces register(Style style, Shapes shapes) {
        WoodSet<FurnitureBedBlock> bed = FurnitureRegistry.woodSet(style.piece("bed"), Room.BEDROOM,
                wood -> new FurnitureBedBlock(wood.properties().strength(0.2F).pushReaction(PushReaction.DESTROY), shapes.bed()));
        // The bunk bed and its top bunk refer to each other, so each looks the other up when placed.
        @SuppressWarnings("unchecked")
        WoodSet<BunkTopBlock>[] top = new WoodSet[1];
        @SuppressWarnings("unchecked")
        WoodSet<BunkBedBlock>[] bunk = new WoodSet[1];
        bunk[0] = FurnitureRegistry.woodSet(style.piece("bunk_bed"), Room.KIDS_ROOM, wood -> new BunkBedBlock(
                wood.properties().strength(0.2F).pushReaction(PushReaction.DESTROY), () -> top[0].get(wood).get()));
        top[0] = FurnitureRegistry.woodBlocks(style.piece("bunk_bed_top"), wood -> new BunkTopBlock(
                wood.properties().strength(0.2F).pushReaction(PushReaction.DESTROY), () -> bunk[0].get(wood).get()));
        WoodSet<SeatBlock> chair = FurnitureRegistry.woodSet(style.piece("dining_chair"), Room.DINING_ROOM,
                wood -> new SeatBlock(wood.properties(), shapes.chair(), shapes.chairSeat()));
        WoodSet<ConnectingTableBlock> table = FurnitureRegistry.woodSet(style.piece("dining_table"), Room.DINING_ROOM,
                wood -> new ConnectingTableBlock(wood.properties(), shapes.tableTop(), shapes.tableLeg()));
        WoodSet<ConnectingTableBlock> coffeeTable = FurnitureRegistry.woodSet(style.piece("coffee_table"), Room.LIVING_ROOM,
                wood -> new ConnectingTableBlock(wood.properties(), shapes.coffeeTop(), shapes.coffeeLeg()));
        WoodSet<FurnitureStorageBlock> nightstand = FurnitureRegistry.woodSet(style.piece("nightstand"), Room.BEDROOM,
                wood -> new FurnitureStorageBlock(wood.properties(), 1, shapes.nightstand()));
        WoodSet<FurnitureStorageBlock> dresser = FurnitureRegistry.woodSet(style.piece("dresser"), Room.BEDROOM,
                wood -> new FurnitureStorageBlock(wood.properties(), 3, shapes.dresser()));
        WoodSet<TallStorageFurnitureBlock> wardrobe = FurnitureRegistry.woodSet(style.piece("wardrobe"), Room.BEDROOM,
                wood -> new TallStorageFurnitureBlock(wood.properties(), 6, shapes.wardrobeLower(), shapes.wardrobeUpper()));
        ColorSet<SofaBlock> sofa = FurnitureRegistry.colorSet(style.piece("sofa"), Room.LIVING_ROOM,
                color -> new SofaBlock(fabric(color), shapes.sofaSeat(), style));
        ColorSet<SeatBlock> armchair = FurnitureRegistry.colorSet(style.piece("armchair"), Room.LIVING_ROOM,
                color -> new SeatBlock(fabric(color), shapes.armchair(), shapes.armchairSeat()));
        WoodSet<FurnitureBlock> desk = FurnitureRegistry.woodSet(style.piece("desk"), Room.BEDROOM,
                wood -> new FurnitureBlock(wood.properties(), DESK));
        WoodSet<FurnitureStorageBlock> bookshelf = FurnitureRegistry.woodSet(style.piece("bookshelf"), Room.LIVING_ROOM,
                wood -> new FurnitureStorageBlock(wood.properties(), 2, FULL));
        WoodSet<SwitchableFurnitureBlock> tableLamp = FurnitureRegistry.woodSet(style.piece("table_lamp"), Room.BEDROOM,
                wood -> new SwitchableFurnitureBlock(wood.properties().lightLevel(SwitchableFurnitureBlock.lightWhenOn(15)), TABLE_LAMP));
        WoodSet<FloorLampBlock> floorLamp = FurnitureRegistry.woodSet(style.piece("floor_lamp"), Room.LIVING_ROOM,
                wood -> new FloorLampBlock(wood.properties().lightLevel(FloorLampBlock::lightLevel), FLOOR_LAMP_LOWER, FLOOR_LAMP_UPPER));
        WoodSet<CounterBlock> counter = FurnitureRegistry.woodSet(style.piece("counter"), Room.KITCHEN,
                wood -> new CounterBlock(wood.properties(), 3, FULL));
        WoodSet<FurnitureStorageBlock> upperCabinet = FurnitureRegistry.woodSet(style.piece("upper_cabinet"), Room.KITCHEN,
                wood -> new FurnitureStorageBlock(wood.properties(), 2, UPPER_CABINET));
        return new Pieces(style, bed, bunk[0], top[0], chair, table, coffeeTable, nightstand, dresser, wardrobe, sofa, armchair,
                desk, bookshelf, tableLamp, floorLamp, counter, upperCabinet);
    }

    private static BlockBehaviour.Properties fabric(DyeColor color) {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor()).strength(0.8F).noOcclusion();
    }

    public static Pieces get(Style style) {
        return PIECES.get(style);
    }

    public static Map<Style, Pieces> all() {
        return Collections.unmodifiableMap(PIECES);
    }

    /** Loads this class so the blocks above get registered. */
    public static void init() {
    }
}
