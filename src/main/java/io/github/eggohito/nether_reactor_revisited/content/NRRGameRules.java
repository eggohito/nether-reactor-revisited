package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.mixin.BooleanRuleAccessor;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class NRRGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> CAN_ENTER_NETHER = GameRuleRegistry.register("nether-reactor-revisited:canEnterNether",
        GameRules.Category.MISC, BooleanRuleAccessor.callCreate(true)
    );

    public static final GameRules.Key<GameRules.BooleanRule> CAN_EXIT_NETHER = GameRuleRegistry.register("nether-reactor-revisited:canExitNether",
        GameRules.Category.MISC, BooleanRuleAccessor.callCreate(true)
    );

    public static <T extends GameRules.Rule<T>> boolean getBooleanOrDefault(GameRules.Key<T> key, World world, boolean defaultValue) {
        T rule = world.getGameRules().get(key);
        return !(rule instanceof GameRules.BooleanRule booleanRule)
            ? defaultValue
            : booleanRule.get();
    }

    public static void registerAll() {

    }

}
