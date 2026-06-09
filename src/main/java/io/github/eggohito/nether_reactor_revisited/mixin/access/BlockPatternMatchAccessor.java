package io.github.eggohito.nether_reactor_revisited.mixin.access;

import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockPattern.BlockPatternMatch.class)
public interface BlockPatternMatchAccessor {

	@Accessor
	LoadingCache<BlockPos, BlockInWorld> getCache();

}
