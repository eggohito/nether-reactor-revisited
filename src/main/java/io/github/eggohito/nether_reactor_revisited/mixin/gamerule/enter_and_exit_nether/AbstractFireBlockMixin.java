package io.github.eggohito.nether_reactor_revisited.mixin.gamerule.enter_and_exit_nether;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.duck.InventoryHolder;
import io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether.Notifiable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {

    @Shadow
    private static boolean shouldLightPortalAt(World world, BlockPos pos, Direction direction) {
        throw new AssertionError();
    }

    @ModifyExpressionValue(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;canPlaceAt(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private static boolean nrr$canLightPortalOnNormalPlacement(boolean original, World world, BlockPos pos, Direction direction) {

        if (!original) {
            return false;
        }

        shouldLightPortalAt(world, pos, direction);
        return true;

    }

    @ModifyReturnValue(method = "shouldLightPortalAt", at = @At(value = "RETURN", ordinal = 2))
    private static boolean nrr$canLightNetherPortals(boolean original, World world, BlockPos pos) {

        Pair<String, Boolean> ruleState = world.getRegistryKey() != World.NETHER
            ? Pair.of("canEnterNether", NRRGameRules.getRuleOrEmpty(NRRGameRules.CAN_ENTER_NETHER, world)
                .map(GameRules.BooleanRule::get)
                .orElse(true))
            : Pair.of("canExitNether", NRRGameRules.getRuleOrEmpty(NRRGameRules.CAN_EXIT_NETHER, world)
                .map(GameRules.BooleanRule::get)
                .orElse(true));

        if (!original || ruleState.getRight()) {
            return original;
        }

        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, new Box(pos).expand(16), Notifiable.TO_BE_NOTIFIED);
        for (PlayerEntity player : players) {

            if (!(player instanceof Notifiable notifiable) || !(player instanceof InventoryHolder inventoryHolder)) {
                continue;
            }

            notifiable.nrr$setNotified(true);
            inventoryHolder.nrr$setReloadInventory(true);

            player.sendMessage(Text
                .translatable("gamerule.nether-reactor-revisited:" + ruleState.getLeft() + ".prevent_notification")
                .styled(style -> style.withColor(Formatting.RED)), true);

        }

        return false;

    }

    @WrapOperation(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/NetherPortal;getNewPortal(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;"))
    private Optional<NetherPortal> nrr$canLightNetherPortalsNaturally(WorldAccess worldAccess, BlockPos pos, Direction.Axis axis, Operation<Optional<NetherPortal>> original, BlockState state, World world) {

        GameRules gameRules = world.getGameRules();
        boolean result = world.getRegistryKey() != World.NETHER
            ? gameRules.getBoolean(NRRGameRules.CAN_ENTER_NETHER)
            : gameRules.getBoolean(NRRGameRules.CAN_EXIT_NETHER);

        return result
            ? original.call(worldAccess, pos, axis)
            : Optional.empty();

    }

}
