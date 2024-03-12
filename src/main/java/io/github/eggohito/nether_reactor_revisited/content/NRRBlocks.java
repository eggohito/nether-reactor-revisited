package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.item.NetherReactorItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

public class NRRBlocks {

    public static final NetherReactorBlock REACTOR_CORE = registerWithItem("reactor_core",
        NetherReactorBlock::new,
        block -> new NetherReactorItem(block, new Item.Settings())
    );

    public static void registerAll() {

    }

    public static <T extends Block> T register(String name, Supplier<T> blockSupplier) {
        return Registry.register(Registries.BLOCK, NetherReactorRevisited.id(name), blockSupplier.get());
    }

    public static <T extends Block, I extends BlockItem> T registerWithItem(String name, Supplier<T> blockSupplier, Function<T, I> itemFactory) {

        T registeredBlock = register(name, blockSupplier);
        Registry.register(Registries.ITEM, NetherReactorRevisited.id(name), itemFactory.apply(registeredBlock));

        return registeredBlock;

    }

}
