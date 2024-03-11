package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.mixin.BooleanRuleAccessor;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Optional;

public class NRRGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> CAN_ENTER_NETHER = GameRuleRegistry.register("nether-reactor-revisited:canEnterNether",
        GameRules.Category.MISC, BooleanRuleAccessor.callCreate(true)
    );

    public static final GameRules.Key<GameRules.BooleanRule> CAN_EXIT_NETHER = GameRuleRegistry.register("nether-reactor-revisited:canExitNether",
        GameRules.Category.MISC, BooleanRuleAccessor.callCreate(true)
    );

    public static <T extends GameRules.Rule<T>> Optional<T> getRuleOrEmpty(GameRules.Key<T> key, World world) {
        return Optional.ofNullable(world.getGameRules().get(key));
    }

    public static void registerAll() {

    }

}
