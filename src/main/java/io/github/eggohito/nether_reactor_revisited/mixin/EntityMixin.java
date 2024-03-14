package io.github.eggohito.nether_reactor_revisited.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.nether_reactor_revisited.duck.BlockCollider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements BlockCollider {

    @Inject(method = "checkBlockCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V"))
    private void nrr$addBlockOnCollide(CallbackInfo ci, @Local BlockState state) {
        this.nrr$addCollidedBlock(state.getBlock());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nrr$clearCollidedBlocks(CallbackInfo ci) {
        this.nrr$getCollidedBlocks().clear();
    }

}
