package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class NRRBlockTags {

	public static final TagKey<Block> ACTIVATED_REACTOR_BLOCKS = create("reactor/activated");
	public static final TagKey<Block> ACTIVATING_REACTOR_BLOCKS = create("reactor/activating");
	public static final TagKey<Block> DEACTIVATED_REACTOR_BLOCKS = create("reactor/deactivated");
	public static final TagKey<Block> DEACTIVATING_REACTOR_BLOCKS = create("reactor/deactivating");
	public static final TagKey<Block> NORMAL_REACTOR_BLOCKS = create("reactor/normal");
	public static final TagKey<Block> POWER_BLOCKS = create("reactor/power_blocks");

	private static TagKey<Block> create(String path) {
		return TagKey.create(Registries.BLOCK, NetherReactorRevisited.id(path));
	}

}
