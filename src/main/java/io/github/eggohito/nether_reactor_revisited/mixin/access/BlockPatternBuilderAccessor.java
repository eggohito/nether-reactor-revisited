package io.github.eggohito.nether_reactor_revisited.mixin.access;

import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

@Mixin(BlockPatternBuilder.class)
public interface BlockPatternBuilderAccessor {

	@Invoker
	Predicate<BlockInWorld>[][][] callCreatePattern();

}
