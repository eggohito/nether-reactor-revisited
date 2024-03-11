package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether.Notifiable;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {

    @ModifyExpressionValue(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canUsePortals()Z"))
    private boolean nrr$canEnterNetherPortals(boolean original, BlockState state, World world, BlockPos pos, Entity entity) {

        if (!original || world.isClient || !(entity instanceof PlayerEntity playerEntity)) {
            return original;
        }

        Pair<String, Boolean> ruleState = world.getRegistryKey() != World.NETHER
            ? Pair.of("canEnterNether", world.getGameRules().getBoolean(NRRGameRules.CAN_ENTER_NETHER))
            : Pair.of("canExitNether", world.getGameRules().getBoolean(NRRGameRules.CAN_EXIT_NETHER));

        if (ruleState.getRight()) {
            return true;
        }

        if (playerEntity instanceof Notifiable notifiable && !notifiable.nrr$wasNotified()) {
            notifiable.nrr$setNotified(true);
            playerEntity.sendMessage(Text
                .translatable("gamerule.nether-reactor-revisited:" + ruleState.getLeft() + ".prevent_notification")
                .styled(style -> style.withColor(Formatting.RED)), true);
        }

        return false;

    }

}
