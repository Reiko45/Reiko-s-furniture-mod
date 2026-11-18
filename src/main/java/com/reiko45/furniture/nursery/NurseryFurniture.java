package com.reiko45.furniture.nursery;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.FurnitureStorageBlock;
import com.reiko45.furniture.core.registry.ModBlockEntities;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Nursery and hobby room: cribs and changing tables in every wood, baby mobiles in every color, an aquarium and a bird cage. */
public final class NurseryFurniture {
    private static final Room ROOM = Room.NURSERY;

    public static final WoodSet<FurnitureBlock> CRIB = FurnitureRegistry.woodSet("crib", ROOM,
            wood -> new FurnitureBlock(wood.properties(), boxes(box(0.5, 0, 0.5, 15.5, 13, 15.5))));

    /** One row of storage in its drawers. */
    public static final WoodSet<FurnitureStorageBlock> CHANGING_TABLE = FurnitureRegistry.woodSet("changing_table", ROOM,
            wood -> new FurnitureStorageBlock(wood.properties(), 1, boxes(box(0.5, 0, 1, 15.5, 16, 15.5))));

    /** Hangs from the ceiling; right-click to spin it. */
    public static final ColorSet<BabyMobileBlock> BABY_MOBILE = FurnitureRegistry.colorSet("baby_mobile", ROOM,
            color -> new BabyMobileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.5F).noOcclusion().noCollission(), boxes(box(2, 6.5, 2, 14, 16, 14))));

    /** Holds up to three fish from fish buckets. */
    public static final DeferredBlock<AquariumBlock> AQUARIUM = FurnitureRegistry.register("aquarium", ROOM,
            () -> new AquariumBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.8F)
                    .sound(SoundType.GLASS).noOcclusion()));

    /** Keeps a parrot from your shoulder. */
    public static final DeferredBlock<BirdCageBlock> BIRD_CAGE = FurnitureRegistry.register("bird_cage", ROOM,
            () -> new BirdCageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(1.0F)
                    .sound(SoundType.METAL).noOcclusion()));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AquariumBlockEntity>> AQUARIUM_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("aquarium", () -> BlockEntityType.Builder.of(AquariumBlockEntity::new,
                    AQUARIUM.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BirdCageBlockEntity>> BIRD_CAGE_BLOCK_ENTITY =
            ModBlockEntities.BLOCK_ENTITIES.register("bird_cage", () -> BlockEntityType.Builder.of(BirdCageBlockEntity::new,
                    BIRD_CAGE.get()).build(null));

    private NurseryFurniture() {
    }

    public static void init() {
    }
}
