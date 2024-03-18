package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import eu.pb4.polymer.core.api.utils.PolymerUtils;
import io.github.eggohito.nether_reactor_revisited.duck.InventoryHolder;
import io.github.eggohito.nether_reactor_revisited.duck.BlockCollider;
import io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether.Notifiable;
import net.minecraft.block.Block;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements Attackable, Notifiable, BlockCollider, InventoryHolder {

    private PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private final Set<Block> nrr$collidedBlocks = new HashSet<>();

    @Unique
    private boolean nrr$notified = false;

    @Unique
    private boolean nrr$reloadInventory = false;

    @Override
    public Set<Block> nrr$getCollidedBlocks() {
        return nrr$collidedBlocks;
    }

    @Override
    public void nrr$addCollidedBlock(Block block) {
        this.nrr$getCollidedBlocks().add(block);
    }

    @Override
    public boolean nrr$wasNotified() {
        return nrr$notified;
    }

    @Override
    public void nrr$setNotified(boolean notified) {
        this.nrr$notified = notified;
    }

    @Override
    public boolean nrr$shouldReloadInventory() {
        return nrr$reloadInventory;
    }

    @Override
    public void nrr$setReloadInventory(boolean reloadInventory) {
        this.nrr$reloadInventory = reloadInventory;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nrr$resetNotified(CallbackInfo ci) {

        if (!((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        if (this.nrr$wasNotified() && this.nrr$getCollidedBlocks().stream().noneMatch(block -> block instanceof NetherPortalBlock)) {
            this.nrr$setNotified(false);
        }

        if (this.nrr$shouldReloadInventory()) {
            PolymerUtils.reloadInventory(serverPlayer);
            this.nrr$setReloadInventory(false);
        }

    }

}
