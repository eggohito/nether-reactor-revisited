package io.github.eggohito.nether_reactor_revisited.block.entity;

import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.content.NRREnchantments;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.reactor.ActivityPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.ForgeResult;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class NetherReactorBlockEntity extends BlockEntity implements Clearable {

    private ActivityPhase activityPhase = ActivityPhase.NONE;

    private long activeTicks;
    private int forgeUses;

    public NetherReactorBlockEntity(BlockPos pos, BlockState state) {
        super(NRRBlockEntities.REACTOR_CORE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("activity_phase", activityPhase.toString());
        nbt.putLong("active_ticks", activeTicks);
        nbt.putInt("forge_uses", forgeUses);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activityPhase = ActivityPhase.fromString(nbt.getString("activity_phase"));
        this.activeTicks = nbt.getLong("active_ticks");
        this.forgeUses = nbt.getInt("forge_uses");
    }

    @Override
    public void clear() {
        this.activityPhase = ActivityPhase.NONE;
        this.activeTicks = 0;
        this.forgeUses = 0;
    }

    public ActivityPhase getActivityPhase() {
        return activityPhase;
    }

    public long getActiveTicks() {
        return activeTicks;
    }

    public int getForgeUses() {
        return forgeUses;
    }

    public void activate() {

        if (!(this.world instanceof ServerWorld serverWorld)) {
            return;
        }

        this.activityPhase = ActivityPhase.ACTIVATING;
        this.activeTicks = 0;
        this.forgeUses = 0;

        long timeOfDay = serverWorld.getTimeOfDay();
        long dayTime = timeOfDay % 24000;

        serverWorld.setBlockState(this.getPos(), this.getCachedState().withIfExists(NetherReactorBlock.ACTIVATED, TriState.TRUE));
        serverWorld.setTimeOfDay(timeOfDay + (18000 - dayTime));

        this.markDirty();

    }

    public ForgeResult forgeItem(ItemStack stack, Predicate<ItemStack> applicableCondition) {

        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return ForgeResult.FAIL;
        }

        if (!applicableCondition.test(stack)) {
            return ForgeResult.INAPPLICABLE;
        }

        if (this.forgeUses >= serverWorld.getGameRules().getInt(NRRGameRules.MAX_FORGE_USES)) {
            return ForgeResult.MAX_USE_REACHED;
        }

        stack.addEnchantment(NRREnchantments.HELL_LIGHTER, 1);
        this.forgeUses++;

        this.markDirty();
        return ForgeResult.SUCCESS;

    }

    public static void tick(World world, BlockPos pos, BlockState state, NetherReactorBlockEntity netherReactor) {

        if (!(world instanceof ServerWorld serverWorld) || netherReactor.activityPhase == ActivityPhase.NONE) {
            return;
        }

        long activeSeconds = ++netherReactor.activeTicks / 20;
        if (activeSeconds > 0 && activeSeconds % Math.max(1, serverWorld.getGameRules().getInt(NRRGameRules.ACTIVITY_DURATION)) == 0) {
            world.setBlockState(pos, state.withIfExists(NetherReactorBlock.ACTIVATED, TriState.FALSE));
            netherReactor.clear();
        }

        netherReactor.markDirty();

    }

}
