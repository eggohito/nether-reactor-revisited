package io.github.eggohito.nether_reactor_revisited.duck;

import net.minecraft.block.Block;

import java.util.HashSet;
import java.util.Set;

public interface BlockCollider {

    default Set<Block> nrr$getCollidedBlocks() {
        return new HashSet<>();
    }

    default void nrr$addCollidedBlock(Block block) {

    }

}
