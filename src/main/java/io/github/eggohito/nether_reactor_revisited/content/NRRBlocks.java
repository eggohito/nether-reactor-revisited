package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.GlowingObsidianBlock;
import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public final class NRRBlocks {

	public static final Block REACTOR_CORE = register(
		"reactor_core",
		ReactorCoreBlock::new,
		BlockBehaviour.Properties.of()
			.strength(3.0F)
			.lightLevel(state -> state.getValue(ReactorCoreBlock.PHASE).isActive() ? 2 : 0)
			.sound(SoundType.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block GLOWING_OBSIDIAN = register(
		"glowing_obsidian",
		GlowingObsidianBlock::new,
		BlockBehaviour.Properties.of()
			.strength(35.0F, 1200.0F)
			.lightLevel(ignored -> 12)
			.mapColor(MapColor.COLOR_BLACK)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
	);

	public static void registerAll() {

	}

	private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		return Blocks.register(ResourceKey.create(Registries.BLOCK, NetherReactorRevisited.id(path)), factory, properties);
	}

}
