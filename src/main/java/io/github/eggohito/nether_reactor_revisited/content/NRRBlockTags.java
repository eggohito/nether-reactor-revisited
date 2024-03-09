package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class NRRBlockTags {

    public static final TagKey<Block> DEFAULT_PATTERN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, NetherReactorRevisited.id("pattern_blocks/default"));
    public static final TagKey<Block> ACTIVATED_PATTERN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, NetherReactorRevisited.id("pattern_blocks/activated"));
    public static final TagKey<Block> DEACTIVATED_PATTERN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, NetherReactorRevisited.id("pattern_blocks/deactivated"));

    public static final TagKey<Block> POWER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, NetherReactorRevisited.id("power_blocks"));

}
