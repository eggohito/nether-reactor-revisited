package io.github.eggohito.nether_reactor_revisited;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherReactorRevisited implements ModInitializer {

	public static final String MOD_NAMESPACE = "nether-reactor-revisited";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	@Override
	public void onInitialize() {

		LOGGER.info("Loaded Nether Reactor: Revisited!");

	}

}