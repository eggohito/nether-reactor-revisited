package io.github.eggohito.nether_reactor_revisited.util;

import net.minecraft.util.math.*;

import java.util.*;
import java.util.stream.Collectors;

public class DirectionUtil {

    public static Set<Direction> getDirectionsFromPos(BlockPos first, BlockPos second) {
        BlockPos pos = second.subtract(first);
        return Arrays.stream(Direction.values())
            .filter(direction -> directionallyEqual(direction, pos))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static boolean directionallyEqual(Direction direction, Vec3i pos) {

        //  Clamp the values from all axis of the vector from -1 to 1
        Vec3i directionVector = direction.getVector();
        Vec3i normalizedPos = new Vec3i(
            MathHelper.clamp(pos.getX(), -1, 1),
            MathHelper.clamp(pos.getY(), -1, 1),
            MathHelper.clamp(pos.getZ(), -1, 1)
        );

        return switch (direction.getAxis()) {
            case X ->
                directionVector.getX() == normalizedPos.getX();
            case Y ->
                directionVector.getY() == normalizedPos.getY();
            case Z ->
                directionVector.getZ() == normalizedPos.getZ();
        };

    }

}
