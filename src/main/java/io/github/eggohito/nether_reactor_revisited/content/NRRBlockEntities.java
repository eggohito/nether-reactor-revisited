package io.github.eggohito.nether_reactor_revisited.content;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.entity.NetherReactorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiFunction;

public class NRRBlockEntities {

    public static final BlockEntityType<NetherReactorBlockEntity> REACTOR_CORE = register("reactor_core",
        NetherReactorBlockEntity::new,
        NRRBlocks.REACTOR_CORE
    );

    public static void registerAll() {

    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {

        BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder
            .create(factory::apply, blocks)
            .build();

        Registry.register(Registries.BLOCK_ENTITY_TYPE, NetherReactorRevisited.id(name), blockEntityType);
        PolymerBlockUtils.registerBlockEntity(blockEntityType);

        return blockEntityType;

    }

}
