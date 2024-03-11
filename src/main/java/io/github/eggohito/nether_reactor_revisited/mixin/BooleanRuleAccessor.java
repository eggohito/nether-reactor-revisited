package io.github.eggohito.nether_reactor_revisited.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanRule.class)
public interface BooleanRuleAccessor {

    @Invoker
    static GameRules.Type<GameRules.BooleanRule> callCreate(boolean initialValue) {
        throw new AssertionError();
    }

}
