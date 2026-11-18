package com.reiko45.furniture.decor;

import static com.reiko45.furniture.decor.DecorFurniture.BASEBOARD;
import static com.reiko45.furniture.decor.DecorFurniture.CROWN_MOLDING;
import static com.reiko45.furniture.decor.DecorFurniture.FRENCH_DOOR;
import static com.reiko45.furniture.decor.DecorFurniture.PARQUET;
import static com.reiko45.furniture.decor.DecorFurniture.PARQUET_SLAB;
import static com.reiko45.furniture.decor.DecorFurniture.PICTURE_FRAME;
import static com.reiko45.furniture.decor.DecorFurniture.SLIDING_DOOR;
import static com.reiko45.furniture.decor.DecorFurniture.TILES;
import static com.reiko45.furniture.decor.DecorFurniture.WALLPAPER;
import static com.reiko45.furniture.decor.DecorFurniture.WINDOW;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.WallTrimBlock;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureBlockTagsProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models, recipes and tags for the decor section. P = planks, L = log, S = wooden slab, D = dye, W = paper, G = glass. */
public class DecorData implements RoomData {
    private static final Room ROOM = Room.DECOR;

    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (DyeColor color : DyeColor.values()) {
            Block block = WALLPAPER.get(color).get();
            cube(models, block, ReikosFurniture.id("block/wallpaper/" + color.getName()));
        }
        TILES.forEach((pattern, block) -> cube(models, block.get(), ReikosFurniture.id("block/tiles/" + pattern.id())));

        models.woodSimple(PARQUET, "parquet");
        for (FurnitureWood wood : FurnitureWood.values()) {
            SlabBlock slab = PARQUET_SLAB.get(wood).get();
            String name = name(slab);
            ModelFile bottom = models.woodModel(name, models.template(ROOM, "parquet_slab"), wood);
            ModelFile top = models.woodModel(name + "_top", models.template(ROOM, "parquet_slab_top"), wood);
            ModelFile full = new ModelFile.UncheckedModelFile(ReikosFurniture.id("block/" + name(PARQUET.get(wood).get())));
            models.slabBlock(slab, bottom, top, full);
            models.blockItem(slab, ReikosFurniture.id("block/" + name));
        }
        trims(models, BASEBOARD, "baseboard");
        trims(models, CROWN_MOLDING, "crown_molding");
        models.woodFacing(PICTURE_FRAME, "picture_frame");
        for (FurnitureWood wood : FurnitureWood.values()) {
            frenchDoor(models, FRENCH_DOOR.get(wood).get(), wood);
            slidingDoor(models, SLIDING_DOOR.get(wood).get(), wood);
            window(models, WINDOW.get(wood).get(), wood);
        }
    }

    /**
     * French doors follow the vanilla door layout: the closed model is turned by facing, and opening turns
     * it a further quarter. The handle is on the edge away from the hinge.
     */
    private static void frenchDoor(FurnitureBlockStateProvider models, DoorBlock door, FurnitureWood wood) {
        String name = name(door);
        ModelFile handleNorth = models.woodModel(name + "_bottom_handle_north", models.template(ROOM, "french_door_bottom_handle_north"), wood);
        ModelFile handleSouth = models.woodModel(name + "_bottom_handle_south", models.template(ROOM, "french_door_bottom_handle_south"), wood);
        ModelFile top = models.woodModel(name + "_top", models.template(ROOM, "french_door_top"), wood);
        models.getVariantBuilder(door).forAllStatesExcept(state -> {
            Direction facing = state.getValue(DoorBlock.FACING);
            boolean open = state.getValue(DoorBlock.OPEN);
            boolean leftHinge = state.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT;
            int y = switch (facing) {
                case SOUTH -> 90;
                case WEST -> 180;
                case NORTH -> 270;
                default -> 0;
            };
            if (open) {
                y = (y + (leftHinge ? 90 : 270)) % 360;
            }
            ModelFile model = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? top : (leftHinge != open ? handleSouth : handleNorth);
            return ConfiguredModel.builder().modelFile(model).rotationY(y).build();
        }, DoorBlock.POWERED);
        models.woodItem(door, ROOM, "french_door_item", wood);
    }

    private static void slidingDoor(FurnitureBlockStateProvider models, SlidingDoorBlock door, FurnitureWood wood) {
        String name = name(door);
        Map<String, ModelFile> files = new HashMap<>();
        for (String half : new String[] {"lower", "upper"}) {
            for (String open : new String[] {"", "_open"}) {
                files.put(half + open, models.woodModel(name + "_" + half + open, models.template(ROOM, "sliding_door_" + half + open), wood));
            }
        }
        models.facingBlock(door, state -> files.get((state.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.UPPER ? "upper" : "lower")
                + (state.getValue(SlidingDoorBlock.OPEN) ? "_open" : "")));
        models.woodItem(door, ROOM, "sliding_door_item", wood);
    }

    /** The pane (closed or tilted open) plus a frame bar on each side that doesn't join another window. */
    private static void window(FurnitureBlockStateProvider models, FramedWindowBlock window, FurnitureWood wood) {
        String name = name(window);
        ModelFile pane = models.woodModel(name + "_pane", models.template(ROOM, "window_pane"), wood);
        ModelFile paneOpen = models.woodModel(name + "_pane_open", models.template(ROOM, "window_pane_open"), wood);
        Map<BooleanProperty, ModelFile> frames = Map.of(
                FramedWindowBlock.UP, models.woodModel(name + "_frame_top", models.template(ROOM, "window_frame_top"), wood),
                FramedWindowBlock.DOWN, models.woodModel(name + "_frame_bottom", models.template(ROOM, "window_frame_bottom"), wood),
                FramedWindowBlock.LEFT, models.woodModel(name + "_frame_left", models.template(ROOM, "window_frame_left"), wood),
                FramedWindowBlock.RIGHT, models.woodModel(name + "_frame_right", models.template(ROOM, "window_frame_right"), wood));
        var builder = models.getMultipartBuilder(window);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int y = FurnitureBlockStateProvider.yRot(facing);
            builder.part().modelFile(pane).rotationY(y).addModel()
                    .condition(FramedWindowBlock.FACING, facing).condition(FramedWindowBlock.OPEN, false).end();
            builder.part().modelFile(paneOpen).rotationY(y).addModel()
                    .condition(FramedWindowBlock.FACING, facing).condition(FramedWindowBlock.OPEN, true).end();
            frames.forEach((side, frame) -> builder.part().modelFile(frame).rotationY(y).addModel()
                    .condition(FramedWindowBlock.FACING, facing).condition(side, false).end());
        }
        models.woodItem(window, ROOM, "window_item", wood);
    }

    private static void cube(FurnitureBlockStateProvider models, Block block, ResourceLocation texture) {
        String name = name(block);
        models.simpleBlock(block, models.models().cubeAll(name, texture));
        models.blockItem(block, ReikosFurniture.id("block/" + name));
    }

    /** A wall trim has a model for each corner shape. */
    private static void trims(FurnitureBlockStateProvider models, WoodSet<WallTrimBlock> set, String template) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            WallTrimBlock block = set.get(wood).get();
            String name = name(block);
            Map<WallTrimBlock.Shape, ModelFile> files = new EnumMap<>(WallTrimBlock.Shape.class);
            for (WallTrimBlock.Shape shape : WallTrimBlock.Shape.values()) {
                String suffix = shape == WallTrimBlock.Shape.STRAIGHT ? "" : "_" + shape.getSerializedName();
                files.put(shape, models.woodModel(name + suffix, models.template(ROOM, template + "_" + shape.getSerializedName()), wood));
            }
            models.facingBlock(block, state -> files.get(state.getValue(WallTrimBlock.SHAPE)));
            models.blockItem(block, ReikosFurniture.id("block/" + name));
        }
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            Item dye = DyeItem.byColor(color);
            recipes.shaped(WALLPAPER.get(color).get(), 8, "WPW", "PDP", "WPW")
                    .define('W', Items.PAPER).define('P', ItemTags.PLANKS).define('D', dye)
                    .unlockedBy("has_paper", FurnitureRecipeProvider.hasItem(Items.PAPER)).save(output);
        }
        tiles(recipes, output, DecorFurniture.Tiles.WHITE, Items.WHITE_CONCRETE, Items.WHITE_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.BLACK, Items.BLACK_CONCRETE, Items.BLACK_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.CHECKERED, Items.WHITE_CONCRETE, Items.BLACK_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.BLUE, Items.BLUE_CONCRETE, Items.BLUE_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.GREEN, Items.GREEN_CONCRETE, Items.GREEN_CONCRETE);
        tiles(recipes, output, DecorFurniture.Tiles.TERRACOTTA, Items.TERRACOTTA, Items.TERRACOTTA);
        tiles(recipes, output, DecorFurniture.Tiles.MARBLE, Items.CALCITE, Items.CALCITE);
        tiles(recipes, output, DecorFurniture.Tiles.HEXAGON, Items.WHITE_CONCRETE, Items.LIGHT_GRAY_CONCRETE);

        recipes.woodShaped(output, PARQUET, 2, wood -> Map.of(), "SS", "SS");
        recipes.woodShaped(output, PARQUET_SLAB, 6, wood -> Map.of('Q', Ingredient.of(PARQUET.get(wood).get())), "QQQ");
        recipes.woodShaped(output, BASEBOARD, 6, wood -> Map.of(), "SSS");
        recipes.woodShaped(output, CROWN_MOLDING, 6, wood -> Map.of(), "S", "S", "S");
        recipes.woodShaped(output, FRENCH_DOOR, 3, wood -> Map.of('G', Ingredient.of(Items.GLASS_PANE)), "PG", "PG", "PG");
        recipes.woodShaped(output, SLIDING_DOOR, 2, wood -> Map.of('G', Ingredient.of(Items.GLASS_PANE)), "LG", "LG", "LG");
        recipes.woodShaped(output, WINDOW, 2, wood -> Map.of('G', Ingredient.of(Items.GLASS)), "SGS");
        recipes.woodShaped(output, PICTURE_FRAME, 2, wood -> Map.of('W', Ingredient.of(Items.PAPER)), "SSS", "SWS", "SSS");
    }

    /** Four tiles from a 2x2 of two materials (the same one twice for single-color tiles). */
    private static void tiles(FurnitureRecipeProvider recipes, RecipeOutput output, DecorFurniture.Tiles pattern, Item a, Item b) {
        recipes.shaped(TILES.get(pattern).get(), 4, "AB", "BA")
                .define('A', a).define('B', b)
                .unlockedBy("has_material", FurnitureRecipeProvider.hasItem(a)).save(output);
    }

    @Override
    public void translations(java.util.function.BiConsumer<String, String> add) {
        for (Paintings.Art art : Paintings.ALL) {
            add.accept("painting.reikos_furniture." + art.name() + ".title", art.title());
            add.accept("painting.reikos_furniture." + art.name() + ".author", "Reiko's Furniture");
        }
    }

    @Override
    public void tags(FurnitureBlockTagsProvider tags) {
        // Villagers open wooden doors.
        FRENCH_DOOR.all().forEach(door -> tags.tagFor(BlockTags.WOODEN_DOORS).add(door.get()));
        PARQUET_SLAB.all().forEach(slab -> {
            tags.tagFor(BlockTags.SLABS).add(slab.get());
            tags.tagFor(BlockTags.WOODEN_SLABS).add(slab.get());
        });
    }
}
