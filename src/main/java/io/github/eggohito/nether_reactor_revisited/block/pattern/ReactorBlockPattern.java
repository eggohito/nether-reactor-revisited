package io.github.eggohito.nether_reactor_revisited.block.pattern;

import com.google.common.cache.LoadingCache;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReactorBlockPattern extends BlockPattern {

    public ReactorBlockPattern(Predicate<CachedBlockPosition>[][][] pattern) {
        super(pattern);
    }

    public Result testTransformSafely(WorldView world, BlockPos frontTopLeft, Direction forwards, Direction up) {

        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = makeCache(world, false);
        List<BlockPos> failedPos = new ArrayList<>();

        int width = this.getWidth();
        int height = this.getHeight();
        int depth = this.getDepth();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {

                    BlockPos translatedPos = translate(frontTopLeft, forwards, up, x, y, z);
                    CachedBlockPosition cachedBlock = loadingCache.getUnchecked(translatedPos);

                    if (!this.getPattern()[z][y][x].test(cachedBlock)) {
                        failedPos.add(translatedPos);
                    }

                }
            }
        }

        return new Result(frontTopLeft, forwards, up, loadingCache, failedPos, width, height, depth);

    }

    public static class Result extends BlockPattern.Result {

        protected final List<BlockPos> mismatches;

        public Result(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache, List<BlockPos> mismatches, int width, int height, int depth) {
            super(frontTopLeft, forwards, up, cache, width, height, depth);
            this.mismatches = mismatches;
        }

        public List<BlockPos> getMismatches() {
            return mismatches;
        }

        public boolean fail() {
            return !this.getMismatches().isEmpty();
        }

    }

    public static class Builder extends BlockPatternBuilder {

        private Builder() {
            super();
        }

        public static Builder start() {
            return new Builder();
        }

        @Override
        public Builder aisle(String... pattern) {
            return (Builder) super.aisle(pattern);
        }

        @Override
        public Builder where(char key, Predicate<CachedBlockPosition> predicate) {
            return (Builder) super.where(key, predicate);
        }

        @Override
        public ReactorBlockPattern build() {
            return new ReactorBlockPattern(this.bakePredicates());
        }

    }

}
