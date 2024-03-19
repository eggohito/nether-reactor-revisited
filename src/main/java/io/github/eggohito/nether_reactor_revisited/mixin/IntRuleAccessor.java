package io.github.eggohito.nether_reactor_revisited.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.IntRule.class)
public interface IntRuleAccessor {

    @Invoker
    static GameRules.Type<GameRules.IntRule> callCreate(int initialValue) {
        throw new AssertionError();
    }

}
