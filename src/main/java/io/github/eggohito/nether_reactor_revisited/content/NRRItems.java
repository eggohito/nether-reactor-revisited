package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class NRRItems {

	public static final Item REACTOR_CORE = Items.registerBlock(
		NRRBlocks.REACTOR_CORE,
		BlockItem::new,
		new Item.Properties()
	);

	public static final Item GLOWING_OBSIDIAN = Items.registerBlock(
		NRRBlocks.GLOWING_OBSIDIAN,
		BlockItem::new,
		new Item.Properties()
	);

	public static void registerAll() {

	}

	private static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties properties) {
		return Items.registerItem(ResourceKey.create(Registries.ITEM, NetherReactorRevisited.id(path)), factory, properties);
	}

}
