package io.github.eggohito.nether_reactor_revisited;

import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlocks;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.content.NRRItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherReactorRevisited implements ModInitializer {

	public static final String MOD_NAMESPACE = "nether-reactor-revisited";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	@Override
	public void onInitialize() {

		LOGGER.info("Nether Reactor: Revisited is initializing.");

		NRRBlocks.registerAll();
		NRRItems.registerAll();
		NRRBlockEntities.registerAll();
		NRRGameRules.registerAll();

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_NAMESPACE, path);
	}

}