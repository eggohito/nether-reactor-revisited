package io.github.eggohito.nether_reactor_revisited.util;

import com.google.common.cache.LoadingCache;
import io.github.eggohito.nether_reactor_revisited.mixin.access.BlockPatternBuilderAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FailableBlockPattern extends BlockPattern {

	public FailableBlockPattern(Predicate<BlockInWorld>[][][] pattern) {
		super(pattern);
	}

	@Override
	public @Nullable Result matches(LevelReader level, BlockPos origin, Direction forwards, Direction up) {

		LoadingCache<BlockPos, BlockInWorld> cache = createLevelCache(level, false);
		List<BlockPos> failedPositions = new ObjectArrayList<>();

		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				for (int z = 0; z < this.getDepth(); z++) {

					BlockPos position = translateAndRotate(origin, forwards, up, x, y, z);
					BlockInWorld blockInWorld = cache.getUnchecked(position);

					if (!this.getPattern()[z][y][x].test(blockInWorld)) {
						failedPositions.add(position);
					}

				}
			}
		}

		return new Result(origin, forwards, up, cache, failedPositions, this.getWidth(), this.getHeight(), this.getDepth());

	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Result extends BlockPatternMatch {

		private final List<BlockPos> failedPositions;

		public Result(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, BlockInWorld> cache, List<BlockPos> failedPositions, int width, int height, int depth) {
			super(frontTopLeft, forwards, up, cache, width, height, depth);
			this.failedPositions = failedPositions;
		}

		public List<BlockPos> getFailedPositions() {
			return failedPositions;
		}

		public boolean failed() {
			return !this.getFailedPositions().isEmpty();
		}

	}

	public static class Builder extends BlockPatternBuilder {

		@Override
		public Builder aisle(String... aisle) {
			super.aisle(aisle);
			return this;
		}

		@Override
		public Builder where(char character, Predicate<@Nullable BlockInWorld> predicate) {
			super.where(character, predicate);
			return this;
		}

		@Override
		public FailableBlockPattern build() {
			return new FailableBlockPattern(((BlockPatternBuilderAccessor) this).callCreatePattern());
		}
	}

}
