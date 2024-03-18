package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.nether_reactor_revisited.content.NRREnchantments;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether.Notifiable;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {

    @ModifyExpressionValue(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canUsePortals()Z"))
    private boolean nrr$canEnterNetherPortals(boolean original, BlockState state, World world, BlockPos pos, Entity entity) {

        if (world.isClient || !original || !(entity instanceof PlayerEntity player)) {
            return original;
        }

        GameRules.Key<GameRules.BooleanRule> ruleKey = world.getRegistryKey() != World.NETHER
            ? NRRGameRules.CAN_ENTER_NETHER
            : NRRGameRules.CAN_EXIT_NETHER;

        if (NRRGameRules.getBooleanOrDefault(ruleKey, world, true) || player.getInventory().containsAny(NRREnchantments::hasHellLighter)) {
            return true;
        }

        if (player instanceof Notifiable notifiable && !notifiable.nrr$wasNotified()) {
            notifiable.nrr$setNotified(true);
            player.sendMessage(Text
                .translatable("gamerule." + ruleKey + ".prevent_notification")
                .formatted(Formatting.RED), true);
        }

        return false;

    }

}
