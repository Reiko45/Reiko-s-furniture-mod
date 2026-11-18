package com.reiko45.furniture.gameroom;

import static com.reiko45.furniture.core.ShapeUtil.box;
import static com.reiko45.furniture.core.ShapeUtil.boxes;

import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Game room section: pool table in every wood, gaming chairs in every color, an arcade cabinet and a dartboard. */
public final class GameRoomFurniture {
    private static final Room ROOM = Room.GAME_ROOM;

    /** Felt-topped table; joins with pool tables of the same wood into a bigger table. */
    public static final WoodSet<ConnectingTableBlock> POOL_TABLE = FurnitureRegistry.woodSet("pool_table", ROOM,
            wood -> new ConnectingTableBlock(wood.properties(), boxes(box(0, 12, 0, 16, 15, 16)), boxes(box(1, 0, 1, 4, 12, 4))));

    public static final ColorSet<SeatBlock> GAMING_CHAIR = FurnitureRegistry.colorSet("gaming_chair", ROOM,
            color -> new SeatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(color.getMapColor())
                    .strength(0.8F).noOcclusion(), boxes(box(2.5, 0, 2, 13.5, 9, 13), box(2.5, 9, 12, 13.5, 16, 14)), 9));

    /** Two blocks tall; switch it on to play. */
    public static final DeferredBlock<ArcadeCabinetBlock> ARCADE_CABINET = FurnitureRegistry.register("arcade_cabinet", ROOM,
            () -> new ArcadeCabinetBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F)
                    .sound(SoundType.METAL).noOcclusion().lightLevel(ArcadeCabinetBlock::screenLight),
                    boxes(box(1, 0, 1, 15, 16, 15)), boxes(box(1, 0, 2, 15, 16, 15))));

    /** Throw darts; arrows that hit it give a redstone signal. */
    public static final DeferredBlock<DartboardBlock> DARTBOARD = FurnitureRegistry.register("dartboard", ROOM,
            () -> new DartboardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.0F)
                    .sound(SoundType.WOOD).noOcclusion(), boxes(box(2, 2, 15, 14, 14, 16))));

    private GameRoomFurniture() {
    }

    public static void init() {
    }
}
