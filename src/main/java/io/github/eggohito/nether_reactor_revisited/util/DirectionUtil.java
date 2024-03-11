package io.github.eggohito.nether_reactor_revisited.util;

import net.minecraft.util.math.*;

import java.util.*;
import java.util.stream.Collectors;

public class DirectionUtil {

    public static Direction getFacingHorizontal(Vec3d vector) {

        Direction result = Direction.NORTH;
        double previous = Double.MIN_VALUE;

        for (Direction direction : Direction.values()) {

            double current = vector.x * direction.getVector().getX() + vector.z * direction.getVector().getZ();
            if (current < previous) {
                continue;
            }

            previous = current;
            result = direction;

        }

        return result;

    }

    public static Set<Direction> getDirectionsFromPos(BlockPos first, BlockPos second) {
        BlockPos pos = second.subtract(first);
        return Arrays.stream(Direction.values())
            .filter(direction -> directionallyEqual(direction, pos))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static boolean directionallyEqual(Direction direction, Vec3i second) {

        //  Clamp the values from all axis of the vector from -1 to 1
        Vec3i directionVector = new Vec3i(
            MathHelper.clamp(second.getX(), -1, 1),
            MathHelper.clamp(second.getY(), -1, 1),
            MathHelper.clamp(second.getZ(), -1, 1)
        );

        return switch (direction.getAxis()) {
            case X ->
                directionVector.getX() == second.getX();
            case Y ->
                directionVector.getY() == second.getY();
            case Z ->
                directionVector.getZ() == second.getZ();
        };

    }

}
