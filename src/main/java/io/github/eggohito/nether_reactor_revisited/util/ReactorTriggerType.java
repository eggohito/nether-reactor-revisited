package io.github.eggohito.nether_reactor_revisited.util;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.block.entity.NetherReactorBlockEntity;
import io.github.eggohito.nether_reactor_revisited.block.pattern.ReactorBlockPattern;
import io.github.eggohito.nether_reactor_revisited.content.NRRActions;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public enum ReactorTriggerType {

    NORMAL(NetherReactorBlock.DEFAULT_STRUCTURE_PATTERN, "normal",
        NRRActions.HAS_REACTOR_STRUCTURE, NRRActions.IS_REACTOR_LEVEL_WITH_PLAYER, NRRActions.PLAYERS_TOO_FAR_AWAY,
        NRRActions.ANOTHER_REACTOR_NEARBY),
    ACTIVATED(NetherReactorBlock.ACTIVATED_STRUCTURE_PATTERN, "activated",
        NRRActions.QUERY_ACTIVE_REACTOR_TIME),
    DEACTIVATED(NetherReactorBlock.DEACTIVATED_STRUCTURE_PATTERN, "deactivated",
        NRRActions.HAS_REACTOR_STRUCTURE, NRRActions.FORGE_HELL_LIGHTER, NRRActions.CAN_REACTIVATE,
        NRRActions.IS_REACTOR_LEVEL_WITH_PLAYER, NRRActions.PLAYERS_TOO_FAR_AWAY, NRRActions.ANOTHER_REACTOR_NEARBY);

    public static ReactorTriggerType fromTriState(TriState triState) {
        return switch (triState) {
            case DEFAULT ->
                NORMAL;
            case TRUE ->
                ACTIVATED;
            case FALSE ->
                DEACTIVATED;
        };
    }

    final List<ReactorTriggerAction> actions;

    final ReactorBlockPattern structurePattern;
    final String baseTranslationKey;

    ReactorTriggerType(ReactorBlockPattern structurePattern, String type, ReactorTriggerAction... actions) {
        this.actions = Arrays.asList(actions);
        this.structurePattern = structurePattern;
        this.baseTranslationKey = Util.createTranslationKey("trigger", NetherReactorRevisited.id(type));
    }

    public List<ReactorTriggerAction> getActions() {
        return actions;
    }

    public ReactorBlockPattern getStructurePattern() {
        return structurePattern;
    }

    public String getBaseTranslationKey() {
        return baseTranslationKey;
    }

    public final ActionResult trigger(NetherReactorBlockEntity netherReactor, BlockState state, ServerWorld world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

        for (ReactorTriggerAction action : this.getActions()) {

            ActionResult result = action.accept(this, netherReactor, state, world, pos, player, hand, hitResult);

            if (!result.shouldIncrementStat()) {
                return result;
            }

        }

        netherReactor.activate();
        world.getServer().getPlayerManager().broadcast(Text
            .translatable(this.getBaseTranslationKey() + ".success", player.getName())
            .setStyle(ReactorTriggerAction.SUCCESS_STYLE), false);

        return ActionResult.SUCCESS;

    }

}
