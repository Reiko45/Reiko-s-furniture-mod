package com.reiko45.furniture.holiday;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Holiday decorations. Winter: a Christmas tree, presents and stockings in every color, warm and colorful
 * string lights, a wreath and a snow globe. Halloween: a gravestone, a fake cobweb, hanging bats, a candy
 * bowl and a witch's cauldron.
 */
public final class HolidayFurniture {
    private static final Room ROOM = Room.HOLIDAY;

    /** Two blocks tall with twinkling lights; a lamp, so a light switch can turn it on. */
    public static final DeferredBlock<ChristmasTreeBlock> CHRISTMAS_TREE = FurnitureRegistry.register("christmas_tree", ROOM,
            () -> new ChristmasTreeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.5F).sound(SoundType.AZALEA_LEAVES)
                    .noOcclusion().lightLevel(SwitchableFurnitureBlock.lightWhenOn(12)),
                    boxes(box(1, 0, 1, 15, 16, 15)), boxes(box(4, 0, 4, 12, 15, 12))));

    /** One row of storage that stays inside when the present is broken. */
    public static final ColorSet<PresentBlock> PRESENT = FurnitureRegistry.colorSet("present", ROOM,
            color -> new PresentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.5F).noOcclusion().pushReaction(PushReaction.DESTROY), boxes(box(2, 0, 2, 14, 12, 14))));

    public static final DeferredBlock<StringLightsBlock> WARM_STRING_LIGHTS = FurnitureRegistry.register("warm_string_lights", ROOM,
            () -> new StringLightsBlock(stringLights()));
    public static final DeferredBlock<StringLightsBlock> COLORFUL_STRING_LIGHTS = FurnitureRegistry.register("colorful_string_lights", ROOM,
            () -> new StringLightsBlock(stringLights()));

    public static final DeferredBlock<FurnitureBlock> WREATH = FurnitureRegistry.register("wreath", ROOM,
            () -> new FurnitureBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.3F).sound(SoundType.AZALEA_LEAVES)
                    .noOcclusion().noCollission(), boxes(box(1, 1, 15, 15, 15, 16))));

    /** Holds one gift. */
    public static final ColorSet<StockingBlock> STOCKING = FurnitureRegistry.colorSet("stocking", ROOM,
            color -> new StockingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.3F).noOcclusion().noCollission()));

    /** Shake it to make it snow. */
    public static final DeferredBlock<SnowGlobeBlock> SNOW_GLOBE = FurnitureRegistry.register("snow_globe", ROOM,
            () -> new SnowGlobeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(0.5F).sound(SoundType.GLASS).noOcclusion()));

    public static final DeferredBlock<FurnitureBlock> GRAVESTONE = FurnitureRegistry.register("gravestone", ROOM,
            () -> new FurnitureBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE)
                    .noOcclusion(), boxes(box(3, 0, 5.5, 13, 14, 10.5))));

    /** Looks like a cobweb, but doesn't slow anyone down. */
    public static final DeferredBlock<Block> FAKE_COBWEB = FurnitureRegistry.register("fake_cobweb", ROOM,
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(0.2F).sound(SoundType.WOOL)
                    .noOcclusion().noCollission()));

    /** A few bats hanging upside down from the ceiling. */
    public static final DeferredBlock<FurnitureBlock> HANGING_BATS = FurnitureRegistry.register("hanging_bats", ROOM,
            () -> new FurnitureBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.3F).sound(SoundType.WOOL)
                    .noOcclusion().noCollission(), boxes(box(2, 6, 2, 14, 16, 14))));

    /** Holds three treats. */
    public static final DeferredBlock<CandyBowlBlock> CANDY_BOWL = FurnitureRegistry.register("candy_bowl", ROOM,
            () -> new CandyBowlBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.5F)
                    .sound(SoundType.DECORATED_POT).noOcclusion()));

    /** Bubbling green brew, just for show. */
    public static final DeferredBlock<WitchCauldronBlock> WITCH_CAULDRON = FurnitureRegistry.register("witch_cauldron", ROOM,
            () -> new WitchCauldronBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.0F).sound(SoundType.METAL)
                    .noOcclusion().lightLevel(state -> 8)));

    private HolidayFurniture() {
    }

    public static void init() {
    }

    private static BlockBehaviour.Properties stringLights() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.2F).sound(SoundType.LANTERN).noOcclusion()
                .noCollission().lightLevel(SwitchableFurnitureBlock.lightWhenOn(10));
    }
}
