package io.github.eggohito.nether_reactor_revisited.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public static Direction getNullableDirectionFromPos(BlockPos first, BlockPos second) {

        BlockPos pos = first.subtract(second);
        Direction result = null;

        double previous = Double.MIN_VALUE;
        for (Direction direction : Direction.values()) {

            double current = pos.getX() * direction.getVector().getX() + pos.getY() * direction.getVector().getY() + pos.getZ() * direction.getVector().getZ();
            if (current < previous) {
                continue;
            }

            previous = current;
            result = direction;

        }

        return result;

    }

}
