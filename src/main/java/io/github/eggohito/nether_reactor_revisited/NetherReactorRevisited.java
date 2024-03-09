package io.github.eggohito.nether_reactor_revisited;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlocks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherReactorRevisited implements ModInitializer {

	public static final String MOD_NAMESPACE = "nether-reactor-revisited";
    public static final Logger LOGGER = LoggerFactory.getLogger("Nether Reactor: Revisited");

	@Override
	public void onInitialize() {

		NRRBlocks.registerAll();

		PolymerResourcePackUtils.addModAssets(MOD_NAMESPACE);
		PolymerResourcePackUtils.markAsRequired();

		LOGGER.info("Loaded Nether Reactor: Revisited!");

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_NAMESPACE, path);
	}

}