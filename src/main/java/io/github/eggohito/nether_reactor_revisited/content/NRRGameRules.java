package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class NRRGameRules {

	public static final GameRule<Integer> STABLE_CORE_LIFETIME = GameRuleBuilder.forInteger(900)
		.category(GameRuleCategory.MISC)
		.minValue(0)
		.buildAndRegister(NetherReactorRevisited.id("stable_core_lifetime"));

	public static final GameRule<Integer> UNSTABLE_CORE_LIFETIME = GameRuleBuilder.forInteger(60)
		.category(GameRuleCategory.MISC)
		.minValue(0)
		.buildAndRegister(NetherReactorRevisited.id("unstable_core_lifetime"));

	public static void registerAll() {

	}

}
