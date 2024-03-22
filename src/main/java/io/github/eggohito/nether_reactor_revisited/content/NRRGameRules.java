package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.mixin.BooleanRuleAccessor;
import io.github.eggohito.nether_reactor_revisited.mixin.IntRuleAccessor;
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

    public static final GameRules.Key<GameRules.IntRule> MAX_FORGE_USES = GameRuleRegistry.register("nether-reactor-revisited:maxForgeUses",
        GameRules.Category.MISC, IntRuleAccessor.callCreate(3)
    );

    public static final GameRules.Key<GameRules.IntRule> ACTIVITY_DURATION = GameRuleRegistry.register("nether-reactor-revisited:activityDuration",
        GameRules.Category.MISC, IntRuleAccessor.callCreate(45)
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
