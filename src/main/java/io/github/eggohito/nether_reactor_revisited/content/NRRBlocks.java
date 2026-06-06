package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class NRRBlocks {

	public static final Block REACTOR_CORE = register(
		"reactor_core",
		ReactorCoreBlock::new,
		BlockBehaviour.Properties.of()
			.destroyTime(3.5F)
			.sound(SoundType.METAL)
			.requiresCorrectToolForDrops()
	);

	public static void registerAll() {

	}

	private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		return Blocks.register(ResourceKey.create(Registries.BLOCK, NetherReactorRevisited.id(path)), factory, properties);
	}

}
