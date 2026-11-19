package com.reiko45.furniture.theater;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.registry.ModItems;
import com.reiko45.furniture.kitchen.appliance.ApplianceBlock;
import com.reiko45.furniture.kitchen.appliance.ApplianceType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Home theater: a projector and a three-by-two projector screen, recliners in every color, speakers in
 * every wood, a popcorn machine and movie posters.
 */
public final class TheaterFurniture {
    private static final Room ROOM = Room.THEATER;

    /** Three blocks wide and two tall, placed as one; lights up with the film a projector plays. */
    public static final DeferredBlock<ProjectorScreenBlock> PROJECTOR_SCREEN = FurnitureRegistry.register("projector_screen", ROOM,
            () -> new ProjectorScreenBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.8F)
                    .sound(SoundType.WOOL).noOcclusion().lightLevel(state -> state.getValue(ProjectorScreenBlock.LIT) ? 9 : 0)));

    /** Plays three films on a screen in front of it. */
    public static final DeferredBlock<ProjectorBlock> PROJECTOR = FurnitureRegistry.register("projector", ROOM,
            () -> new ProjectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(1.5F)
                    .sound(SoundType.METAL).noOcclusion().lightLevel(SwitchableFurnitureBlock.lightWhenOn(6)),
                    boxes(box(3, 0, 2, 13, 10, 14))));

    /** A seat that tilts back with its footrest out. */
    public static final ColorSet<ReclinerBlock> RECLINER = FurnitureRegistry.colorSet("recliner", ROOM,
            color -> new ReclinerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.8F).noOcclusion(), boxes(box(0, 0, 0, 16, 9, 16), box(0, 9, 12, 16, 16, 16)), 8));

    /** Notes come out while a jukebox or record player nearby plays. */
    public static final WoodSet<SpeakerBlock> SPEAKER = FurnitureRegistry.woodSet("speaker", ROOM,
            wood -> new SpeakerBlock(wood.properties(), boxes(box(3, 0, 4, 13, 16, 12))));

    /** Makes popcorn from wheat seeds and paper. */
    public static final DeferredBlock<ApplianceBlock> POPCORN_MACHINE = FurnitureRegistry.register("popcorn_machine", ROOM,
            () -> new ApplianceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.5F)
                    .sound(SoundType.METAL).noOcclusion(), boxes(box(3, 0, 3, 13, 16, 13)), ApplianceType.POPCORN_MACHINE));

    /** Four posters; right-click to change. */
    public static final DeferredBlock<MoviePosterBlock> MOVIE_POSTER = FurnitureRegistry.register("movie_poster", ROOM,
            () -> new MoviePosterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.5F)
                    .sound(SoundType.WOOD).noCollission()));

    /** A paper bag of popcorn: quick to eat. */
    public static final DeferredItem<Item> POPCORN = ModItems.ITEMS.register("popcorn", () -> new Item(new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).fast().build())));

    private TheaterFurniture() {
    }

    public static void init() {
    }
}
