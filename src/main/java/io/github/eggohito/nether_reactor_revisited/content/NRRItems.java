package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.item.ReactorCoreItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class NRRItems {

	public static final Item REACTOR_CORE = register(
		"reactor_core",
		properties -> new ReactorCoreItem(NRRBlocks.REACTOR_CORE, properties),
		new Item.Properties()
	);

	public static void registerAll() {

	}

	private static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties properties) {
		return Items.registerItem(ResourceKey.create(Registries.ITEM, NetherReactorRevisited.id(path)), factory, properties);
	}

}
