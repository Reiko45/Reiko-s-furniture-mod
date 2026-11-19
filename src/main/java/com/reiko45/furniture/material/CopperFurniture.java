package com.reiko45.furniture.material;

import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.SeatBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Copper furniture ages like copper blocks (unaffected, exposed, weathered, oxidized). Honeycomb waxes
 * it so it stays as it is; an axe scrapes off a stage or the wax. The steps are given to NeoForge's
 * oxidizable and waxable data maps, which vanilla copper uses too.
 */
public final class CopperFurniture {
    private CopperFurniture() {
    }

    /** Ages on random ticks while there is a next stage (unwaxed and not yet oxidized). */
    static boolean ages(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    /** A copper table lamp. It gives less light as it ages, like a copper bulb. */
    public static class Lamp extends SwitchableFurnitureBlock implements WeatheringCopper {
        private final CopperStage stage;

        public Lamp(Properties properties, VoxelShape shape, CopperStage stage) {
            super(properties, shape);
            this.stage = stage;
        }

        @Override
        public WeatherState getAge() {
            return stage.age();
        }

        @Override
        protected boolean isRandomlyTicking(BlockState state) {
            return ages(state);
        }

        @Override
        protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            changeOverTime(state, level, pos, random);
        }
    }

    public static class Table extends ConnectingTableBlock implements WeatheringCopper {
        private final CopperStage stage;

        public Table(Properties properties, VoxelShape top, VoxelShape leg, CopperStage stage) {
            super(properties, top, leg);
            this.stage = stage;
        }

        @Override
        public WeatherState getAge() {
            return stage.age();
        }

        @Override
        protected boolean isRandomlyTicking(BlockState state) {
            return ages(state);
        }

        @Override
        protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            changeOverTime(state, level, pos, random);
        }
    }

    public static class Chair extends SeatBlock implements WeatheringCopper {
        private final CopperStage stage;

        public Chair(Properties properties, VoxelShape shape, double seatHeight, CopperStage stage) {
            super(properties, shape, seatHeight);
            this.stage = stage;
        }

        @Override
        public WeatherState getAge() {
            return stage.age();
        }

        @Override
        protected boolean isRandomlyTicking(BlockState state) {
            return ages(state);
        }

        @Override
        protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            changeOverTime(state, level, pos, random);
        }
    }
}
