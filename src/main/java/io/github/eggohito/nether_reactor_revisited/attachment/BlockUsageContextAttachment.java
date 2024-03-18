package io.github.eggohito.nether_reactor_revisited.attachment;

import io.github.eggohito.nether_reactor_revisited.block.BlockUsageContext;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BlockUsageContextAttachment {

    private final Map<BlockPos, Deque<BlockUsageContext>> cache;

    public BlockUsageContextAttachment() {
        this.cache = new WeakHashMap<>();
    }

    public void push(BlockPos pos, BlockUsageContext context) {
        this.cache.computeIfAbsent(pos, p -> new ArrayDeque<>()).push(context);
    }

    public BlockUsageContext pop(BlockPos pos) {

        Deque<BlockUsageContext> context;
        if ((context = cache.get(pos)) == null) {
            throw new NoSuchElementException("Block at " + pos.toShortString() + " does not have an attached usage context");
        }

        BlockUsageContext result = context.pop();
        if (context.isEmpty()) {
            this.cache.remove(pos);
        }

        return result;

    }

    public boolean hasContext(BlockPos pos) {
        return this.cache.containsKey(pos)
            && !this.cache.get(pos).isEmpty();
    }

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    public void clear() {
        this.cache.clear();
    }

}
