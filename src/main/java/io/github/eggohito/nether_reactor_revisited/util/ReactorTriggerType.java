package io.github.eggohito.nether_reactor_revisited.util;

import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.block.entity.NetherReactorBlockEntity;
import io.github.eggohito.nether_reactor_revisited.block.pattern.ReactorBlockPattern;
import io.github.eggohito.nether_reactor_revisited.content.NRRActions;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum ReactorTriggerType {

    ACTIVATION(NetherReactorBlock.DEFAULT_STRUCTURE_PATTERN, "nether-reactor-revisited.activate",
        NRRActions.HAS_REACTOR_STRUCTURE, NRRActions.IS_REACTOR_LEVEL_WITH_PLAYER, NRRActions.PLAYERS_TOO_FAR_AWAY,
        NRRActions.ANOTHER_REACTOR_NEARBY),
    REACTIVATION(NetherReactorBlock.DEACTIVATED_STRUCTURE_PATTERN, "nether-reactor-revisited.reactivate",
        NRRActions.CAN_REACTIVATE, NRRActions.HAS_REACTOR_STRUCTURE, NRRActions.IS_REACTOR_LEVEL_WITH_PLAYER,
        NRRActions.PLAYERS_TOO_FAR_AWAY, NRRActions.ANOTHER_REACTOR_NEARBY);

    final ReactorTriggerAction[] actions;
    final ReactorBlockPattern structurePattern;
    final String baseTranslationKey;

    ReactorTriggerType(ReactorBlockPattern structurePattern, String baseTranslationKey, ReactorTriggerAction... actions) {
        this.actions = actions;
        this.structurePattern = structurePattern;
        this.baseTranslationKey = baseTranslationKey;
    }

    public ReactorTriggerAction[] getActions() {
        return actions;
    }

    public ReactorBlockPattern getStructurePattern() {
        return structurePattern;
    }

    public String getBaseTranslationKey() {
        return baseTranslationKey;
    }

    public ActionResult trigger(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

        MinecraftServer server = world.getServer();
        if (server == null || !(world.getBlockEntity(pos) instanceof NetherReactorBlockEntity netherReactor)) {
            return ActionResult.CONSUME_PARTIAL;
        }

        for (ReactorTriggerAction action : this.getActions()) {

            ActionResult result = action.accept(this, state, world, pos, player, hand, hitResult);

            if (!result.shouldIncrementStat()) {
                return result;
            }

        }

        server.getPlayerManager().broadcast(Text
            .translatable("actions." + baseTranslationKey + ".success", player.getName())
            .setStyle(ReactorTriggerAction.SUCCESS_STYLE), false);

        netherReactor.activate();
        world.setBlockState(pos, state.withIfExists(NetherReactorBlock.ACTIVATED, TriState.TRUE));

        return ActionResult.SUCCESS;

    }

}
