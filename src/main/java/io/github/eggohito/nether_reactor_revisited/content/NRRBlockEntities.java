package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class NRRBlockEntities {

	public static final BlockEntityType<ReactorCoreBlockEntity> REACTOR_CORE = register(
		"reactor_core",
		FabricBlockEntityTypeBuilder.create(ReactorCoreBlockEntity::new, NRRBlocks.REACTOR_CORE)
	);

	public static void registerAll() {

	}

	private static <BE extends BlockEntity> BlockEntityType<BE> register(String path, FabricBlockEntityTypeBuilder<BE> builder) {

		Identifier id = NetherReactorRevisited.id(path);
		Util.fetchChoiceType(References.BLOCK_ENTITY, id.toString());

		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, builder.build());

	}

}
