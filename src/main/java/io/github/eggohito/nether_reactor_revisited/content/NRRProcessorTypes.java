package io.github.eggohito.nether_reactor_revisited.content;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.levelgen.BlockTransformProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public final class NRRProcessorTypes {

	public static final StructureProcessorType<BlockTransformProcessor> BLOCK_TRANSFORM = register("block_transform", BlockTransformProcessor.CODEC);

	public static void registerAll() {

	}

	private static <P extends StructureProcessor> StructureProcessorType<P> register(String path, MapCodec<P> codec) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, NetherReactorRevisited.id(path), () -> codec);
	}

}
