package io.github.eggohito.nether_reactor_revisited.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockPattern.class)
public interface BlockPatternAccessor {

	@Invoker
	static BlockPos callTranslateAndRotate(BlockPos frontTopLeft, Direction forwardsDirection, Direction upDirection, int right, int down, int forwards) {
		throw new AssertionError();
	}

}
