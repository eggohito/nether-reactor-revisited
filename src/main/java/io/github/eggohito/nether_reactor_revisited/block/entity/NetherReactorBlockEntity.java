package io.github.eggohito.nether_reactor_revisited.block.entity;

import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.content.NRREnchantments;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.util.ReactorForgeResult;
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

    private long lastActiveTime;
    private int forgeUses;

    public NetherReactorBlockEntity(BlockPos pos, BlockState state) {
        super(NRRBlockEntities.REACTOR_CORE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("last_active_time", lastActiveTime);
        nbt.putInt("forge_uses", forgeUses);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastActiveTime = nbt.getLong("last_active_time");
        this.forgeUses = nbt.getInt("forge_uses");
    }

    @Override
    public void clear() {
        this.lastActiveTime = 0;
        this.forgeUses = 0;
    }

    public void activate() {

        if (!(this.world instanceof ServerWorld serverWorld)) {
            return;
        }

        this.lastActiveTime = serverWorld.getTime();
        this.forgeUses = 0;

        long timeOfDay = serverWorld.getTimeOfDay();
        long dayTime = timeOfDay % 24000;

        serverWorld.setBlockState(this.getPos(), this.getCachedState().withIfExists(NetherReactorBlock.ACTIVATED, TriState.TRUE));
        serverWorld.setTimeOfDay(timeOfDay + (18000 - dayTime));

        this.markDirty();

    }

    public ReactorForgeResult forgeItem(ItemStack stack, Predicate<ItemStack> applicableCondition) {

        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return ReactorForgeResult.FAIL;
        }

        if (!applicableCondition.test(stack)) {
            return ReactorForgeResult.INAPPLICABLE;
        }

        if (this.forgeUses >= NRRGameRules.getIntOrDefault(NRRGameRules.MAX_FORGE_USES, serverWorld, 3)) {
            return ReactorForgeResult.MAX_USE_REACHED;
        }

        stack.addEnchantment(NRREnchantments.HELL_LIGHTER, 1);
        this.forgeUses++;

        this.markDirty();
        return ReactorForgeResult.SUCCESS;

    }

    public static ReactorForgeResult forgeItem(World world, BlockPos pos, ItemStack stack, Predicate<ItemStack> applicableCondition) {
        return world.getBlockEntity(pos) instanceof NetherReactorBlockEntity netherReactor
            ? netherReactor.forgeItem(stack, applicableCondition)
            : ReactorForgeResult.FAIL;
    }

    public static void tick(World world, BlockPos pos, BlockState state, NetherReactorBlockEntity blockEntity) {

        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        //  TODO: Implement activity phases

    }

}
