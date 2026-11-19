package com.reiko45.furniture.material;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.kitchen.CounterBlock;
import com.reiko45.furniture.livingroom.FloorLampBlock;
import com.reiko45.furniture.outdoor.PlanterBoxBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Stone and metal furniture. Stone (white and black marble, quartz, blackstone, stone bricks): dining
 * tables, benches, coffee tables, counters and planters; the same in concrete of every color. Copper (in
 * its four ages, waxed or not): lamps, tables and chairs. Wrought iron: a garden chair, a garden table, a
 * bench and a lamp post.
 */
public final class MaterialFurniture {
    private static final VoxelShape FULL = boxes(box(0, 0, 0, 16, 16, 16));
    private static final VoxelShape TABLE_TOP = boxes(box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape TABLE_LEG = boxes(box(1, 0, 1, 4.5, 13, 4.5));
    private static final VoxelShape COFFEE_TOP = boxes(box(0, 6, 0, 16, 8, 16));
    private static final VoxelShape COFFEE_LEG = boxes(box(1, 0, 1, 4, 6, 4));
    private static final VoxelShape BENCH = boxes(box(0, 0, 3, 16, 8, 13));

    public static final MaterialSet<StoneMaterial, ConnectingTableBlock> STONE_DINING_TABLE = stone("dining_table",
            m -> new ConnectingTableBlock(stone(m.mapColor()), TABLE_TOP, TABLE_LEG));
    public static final MaterialSet<StoneMaterial, SeatBlock> STONE_BENCH = stone("bench",
            m -> new SeatBlock(stone(m.mapColor()), BENCH, 8));
    public static final MaterialSet<StoneMaterial, ConnectingTableBlock> STONE_COFFEE_TABLE = stone("coffee_table",
            m -> new ConnectingTableBlock(stone(m.mapColor()), COFFEE_TOP, COFFEE_LEG));
    public static final MaterialSet<StoneMaterial, CounterBlock> STONE_COUNTER = stone("counter",
            m -> new CounterBlock(stone(m.mapColor()), 3, FULL));
    /** Plants, flowers and crops grow on it, as on a wooden planter box. */
    public static final MaterialSet<StoneMaterial, PlanterBoxBlock> STONE_PLANTER = stone("planter",
            m -> new PlanterBoxBlock(stone(m.mapColor())));

    public static final ColorSet<ConnectingTableBlock> CONCRETE_TABLE = FurnitureRegistry.colorSet("concrete_table", Room.STONE,
            color -> new ConnectingTableBlock(concrete(color), TABLE_TOP, TABLE_LEG), false);
    public static final ColorSet<SeatBlock> CONCRETE_BENCH = FurnitureRegistry.colorSet("concrete_bench", Room.STONE,
            color -> new SeatBlock(concrete(color), BENCH, 8), false);
    public static final ColorSet<CounterBlock> CONCRETE_COUNTER = FurnitureRegistry.colorSet("concrete_counter", Room.STONE,
            color -> new CounterBlock(concrete(color), 3, FULL), false);
    public static final ColorSet<PlanterBoxBlock> CONCRETE_PLANTER = FurnitureRegistry.colorSet("concrete_planter", Room.STONE,
            color -> new PlanterBoxBlock(concrete(color)), false);

    public static final MaterialSet<CopperStage, CopperFurniture.Lamp> COPPER_LAMP = copper("lamp",
            stage -> new CopperFurniture.Lamp(copper(stage).lightLevel(state -> state.getValue(CopperFurniture.Lamp.LIT)
                    ? 15 - 3 * stage.age().ordinal() - (stage.age().ordinal() == 3 ? 2 : 0) : 0),
                    boxes(box(4.5, 0, 4.5, 11.5, 12, 11.5)), stage));
    public static final MaterialSet<CopperStage, CopperFurniture.Table> COPPER_TABLE = copper("table",
            stage -> new CopperFurniture.Table(copper(stage), boxes(box(0, 14, 0, 16, 16, 16)), boxes(box(1, 0, 1, 3, 14, 3)), stage));
    public static final MaterialSet<CopperStage, CopperFurniture.Chair> COPPER_CHAIR = copper("chair",
            stage -> new CopperFurniture.Chair(copper(stage), boxes(box(2.5, 0, 2.5, 13.5, 9, 13.5), box(2.5, 9, 12, 13.5, 16, 13.5)),
                    9, stage));

    public static final DeferredBlock<SeatBlock> WROUGHT_IRON_GARDEN_CHAIR = FurnitureRegistry.register("wrought_iron_garden_chair", Room.METAL,
            () -> new SeatBlock(iron(), boxes(box(2, 0, 2, 14, 8, 14), box(2, 8, 12, 14, 16, 14)), 8));
    public static final DeferredBlock<FurnitureBlock> WROUGHT_IRON_GARDEN_TABLE = FurnitureRegistry.register("wrought_iron_garden_table", Room.METAL,
            () -> new FurnitureBlock(iron(), boxes(box(1, 12, 1, 15, 13.5, 15), box(6, 0, 6, 10, 12, 10))));
    public static final DeferredBlock<SeatBlock> WROUGHT_IRON_BENCH = FurnitureRegistry.register("wrought_iron_bench", Room.METAL,
            () -> new SeatBlock(iron(), boxes(box(0, 0, 2, 16, 8, 13), box(0, 8, 12, 16, 15, 14)), 8));
    /** Two blocks tall; a lamp, so light switches and redstone can turn it on. */
    public static final DeferredBlock<FloorLampBlock> WROUGHT_IRON_LAMP_POST = FurnitureRegistry.register("wrought_iron_lamp_post", Room.METAL,
            () -> new FloorLampBlock(iron().lightLevel(FloorLampBlock::lightLevel),
                    boxes(box(5, 0, 5, 11, 2, 11), box(7, 2, 7, 9, 16, 9)), boxes(box(7, 0, 7, 9, 8, 9), box(4.5, 8, 4.5, 11.5, 16, 11.5))));

    private MaterialFurniture() {
    }

    public static void init() {
    }

    private static <B extends net.minecraft.world.level.block.Block> MaterialSet<StoneMaterial, B> stone(String piece,
                                                                                                    java.util.function.Function<StoneMaterial, B> factory) {
        return MaterialSet.register(piece, Room.STONE, StoneMaterial.class, m -> m.id() + "_" + piece, factory);
    }

    private static <B extends net.minecraft.world.level.block.Block> MaterialSet<CopperStage, B> copper(String piece,
                                                                                                   java.util.function.Function<CopperStage, B> factory) {
        return MaterialSet.register("copper_" + piece, Room.METAL, CopperStage.class, s -> s.prefix() + "copper_" + piece, factory);
    }

    private static BlockBehaviour.Properties stone(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(1.5F, 6.0F).sound(SoundType.STONE).noOcclusion();
    }

    private static BlockBehaviour.Properties concrete(DyeColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(1.5F, 6.0F).sound(SoundType.STONE).noOcclusion();
    }

    private static BlockBehaviour.Properties copper(CopperStage stage) {
        MapColor color = switch (stage.age()) {
            case UNAFFECTED -> MapColor.COLOR_ORANGE;
            case EXPOSED -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case WEATHERED -> MapColor.WARPED_STEM;
            case OXIDIZED -> MapColor.WARPED_NYLIUM;
        };
        return BlockBehaviour.Properties.of().mapColor(color).strength(2.0F, 6.0F).sound(SoundType.COPPER).noOcclusion();
    }

    private static BlockBehaviour.Properties iron() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.5F, 6.0F).sound(SoundType.METAL).noOcclusion();
    }
}
