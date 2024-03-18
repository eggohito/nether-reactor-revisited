package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.nether_reactor_revisited.block.BlockUsageContext;
import io.github.eggohito.nether_reactor_revisited.content.NRRAttachmentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

    @Shadow @Final public static DirectionProperty FACING;

    @Inject(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/dispenser/DispenserBehavior;dispense(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private void nrr$pushUsageContextOnDispense(ServerWorld world, BlockState state, BlockPos pos, CallbackInfo ci, @Local ItemStack stack) {
        Direction facing = state.get(FACING);
        world
            .getAttachedOrCreate(NRRAttachmentTypes.BLOCK_USAGE_CONTEXT)
            .push(pos.offset(facing), new BlockUsageContext(world, stack, null));
    }

}
