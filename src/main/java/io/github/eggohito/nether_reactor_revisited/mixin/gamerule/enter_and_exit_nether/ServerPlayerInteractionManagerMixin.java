package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.nether_reactor_revisited.block.BlockUsageContext;
import io.github.eggohito.nether_reactor_revisited.content.NRRAttachmentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    private void nrr$pushUsageContextOnBlockInteration(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir, @Local ItemUsageContext itemContext) {

        if (world instanceof ServerWorld serverWorld) {
            serverWorld
                .getAttachedOrCreate(NRRAttachmentTypes.BLOCK_USAGE_CONTEXT)
                .push(itemContext.getBlockPos().offset(itemContext.getSide()), BlockUsageContext.fromItemContext(itemContext));
        }

    }

}
