package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.nether_reactor_revisited.attachment.BlockUsageContextAttachment;
import io.github.eggohito.nether_reactor_revisited.block.BlockUsageContext;
import io.github.eggohito.nether_reactor_revisited.content.NRRAttachmentTypes;
import io.github.eggohito.nether_reactor_revisited.content.NRREnchantments;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.duck.InventoryHolder;
import io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether.Notifiable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {

    @ModifyReturnValue(method = "shouldLightPortalAt", at = @At("RETURN"))
    private static boolean nrr$canLightNetherPortals(boolean original, World world, BlockPos pos) {

        GameRules.Key<GameRules.BooleanRule> ruleKey = world.getRegistryKey() != World.NETHER
            ? NRRGameRules.CAN_ENTER_NETHER
            : NRRGameRules.CAN_EXIT_NETHER;
        BlockUsageContextAttachment blockContextAttachment = world instanceof ServerWorld serverWorld
            ? serverWorld.getAttached(NRRAttachmentTypes.BLOCK_USAGE_CONTEXT)
            : null;

        if (!original || blockContextAttachment == null) {
            return original;
        }

        return !nrr$shouldPrevent(ruleKey, blockContextAttachment, world, pos);

    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyExpressionValue(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/NetherPortal;getNewPortal(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;"))
    private Optional<NetherPortal> nrr$canLightNetherPortalsNaturally(Optional<NetherPortal> original, BlockState state, World world, BlockPos pos) {

        GameRules.Key<GameRules.BooleanRule> ruleKey = world.getRegistryKey() != World.NETHER
            ? NRRGameRules.CAN_ENTER_NETHER
            : NRRGameRules.CAN_EXIT_NETHER;
        BlockUsageContextAttachment blockContextAttachment = world instanceof ServerWorld serverWorld
            ? serverWorld.getAttached(NRRAttachmentTypes.BLOCK_USAGE_CONTEXT)
            : null;

        if (original.isEmpty() || blockContextAttachment == null) {
            return original;
        }

        return nrr$shouldPrevent(ruleKey, blockContextAttachment, world, pos)
            ? Optional.empty()
            : original;

    }

    @Unique
    private static boolean nrr$shouldPrevent(GameRules.Key<?> ruleKey, BlockUsageContextAttachment blockContextAttachment, World world, BlockPos pos) {

        if (!blockContextAttachment.hasContext(pos)) {
            return false;
        }

        BlockUsageContext blockContext = blockContextAttachment.pop(pos);
        if (NRRGameRules.getBooleanOrDefault(ruleKey, world, true) || NRREnchantments.hasHellLighter(blockContext.stack())) {
            return false;
        }

        PlayerEntity user = blockContext.user();
        if (user instanceof Notifiable notifiable && user instanceof InventoryHolder inventoryHolder) {

            notifiable.nrr$setNotified(true);
            inventoryHolder.nrr$setReloadInventory(true);

            user.sendMessage(Text
                .translatable("gamerule." + ruleKey + ".prevent_notification")
                .formatted(Formatting.RED), true);

        }

        return true;

    }

}
