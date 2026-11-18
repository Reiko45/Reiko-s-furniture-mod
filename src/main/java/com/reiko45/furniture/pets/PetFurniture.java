package com.reiko45.furniture.pets;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.TallFurnitureBlock;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/** Pet corner section: pet beds and feeding bowls in every color, and cat trees in every wood. */
public final class PetFurniture {
    private static final Room ROOM = Room.PET_CORNER;

    /** Cats sit on it; tamed wolves lie down in it. */
    public static final ColorSet<PetBedBlock> PET_BED = FurnitureRegistry.colorSet("pet_bed", ROOM,
            color -> new PetBedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.5F).noOcclusion(), boxes(box(1, 0, 1, 15, 5, 15))));

    /** Two blocks tall; cats like to sit on the top platform. */
    public static final WoodSet<TallFurnitureBlock> CAT_TREE = FurnitureRegistry.woodSet("cat_tree", ROOM,
            wood -> new TallFurnitureBlock(wood.properties(), boxes(box(0, 0, 0, 16, 2, 16), box(6, 2, 6, 10, 16, 10),
                    box(1, 10, 1, 8, 12, 8)), boxes(box(6, 0, 6, 10, 8, 10), box(1, 8, 1, 15, 12, 15))));

    /** Holds meat or fish that hurt tamed pets eat to heal. */
    public static final ColorSet<FeedingBowlBlock> FEEDING_BOWL = FurnitureRegistry.colorSet("feeding_bowl", ROOM,
            color -> new FeedingBowlBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_TERRACOTTA).mapColor(color.getMapColor())
                    .strength(0.5F).noOcclusion(), boxes(box(3, 0, 3, 13, 3, 13))));

    private PetFurniture() {
    }

    public static void init() {
    }

    /** Gives tamed wolves the "rest on a pet bed" goal. Registered on the game event bus. */
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Wolf wolf) {
            wolf.goalSelector.addGoal(7, new RestOnPetBedGoal(wolf));
        }
    }
}
