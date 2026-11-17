package com.reiko45.furniture.core;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Helpers for building and rotating block shapes. */
public final class ShapeUtil {
    private ShapeUtil() {
    }

    /** Joins boxes given in pixels (0-16), like {@link Block#box}. Each box is {x1, y1, z1, x2, y2, z2}. */
    public static VoxelShape boxes(double[]... boxes) {
        VoxelShape shape = Shapes.empty();
        for (double[] b : boxes) {
            shape = Shapes.or(shape, Block.box(b[0], b[1], b[2], b[3], b[4], b[5]));
        }
        return shape.optimize();
    }

    public static double[] box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new double[] {x1, y1, z1, x2, y2, z2};
    }

    /**
     * Takes a shape whose front faces north and returns it rotated for every horizontal direction.
     * This matches the blockstate rotation used by the models (north = 0, east = 90, south = 180, west = 270).
     */
    public static Map<Direction, VoxelShape> horizontal(VoxelShape north) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        VoxelShape current = north;
        for (Direction dir : new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
            map.put(dir, current);
            current = rotateClockwise(current);
        }
        return map;
    }

    /** Rotates a shape 90 degrees clockwise when seen from above (north becomes east). */
    public static VoxelShape rotateClockwise(VoxelShape shape) {
        VoxelShape[] result = {Shapes.empty()};
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                result[0] = Shapes.or(result[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
        return result[0].optimize();
    }
}
