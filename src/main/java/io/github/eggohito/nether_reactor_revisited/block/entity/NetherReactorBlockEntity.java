package io.github.eggohito.nether_reactor_revisited.block.entity;

import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherReactorBlockEntity extends BlockEntity {

    private long lastActiveTime;

    public NetherReactorBlockEntity(BlockPos pos, BlockState state) {
        super(NRRBlockEntities.REACTOR_CORE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putLong("last_active_time", lastActiveTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.lastActiveTime = nbt.getLong("last_active_time");
    }

    public void activate() {

        if (!(this.world instanceof ServerWorld serverWorld)) {
            return;
        }

        this.lastActiveTime = serverWorld.getTime();

        long timeOfDay = serverWorld.getTimeOfDay();
        long dayTime = timeOfDay % 24000;

        serverWorld.setTimeOfDay(timeOfDay + (18000 - dayTime));

    }

    public static void tick(World world, BlockPos pos, BlockState state, NetherReactorBlockEntity blockEntity) {

        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

    }

}
