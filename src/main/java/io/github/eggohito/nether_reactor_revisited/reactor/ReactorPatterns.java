package io.github.eggohito.nether_reactor_revisited.reactor;

import io.github.eggohito.nether_reactor_revisited.content.NRRBlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;

public final class ReactorPatterns {

	public static final BlockPattern NORMAL = BlockPatternBuilder.start()
		.aisle("~#~", "#~#", "@#@")
		.aisle("###", "~ ~", "###")
		.aisle("~#~", "#~#", "@#@")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlockTags.NORMAL_REACTOR_BLOCKS)))
		.where('@', BlockInWorld.hasState(state -> state.is(NRRBlockTags.POWER_BLOCKS)))
		.build();

	public static final BlockPattern ACTIVATING = BlockPatternBuilder.start()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlockTags.ACTIVATING_REACTOR_BLOCKS)))
		.build();

	public static final BlockPattern ACTIVATED = BlockPatternBuilder.start()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlockTags.ACTIVATED_REACTOR_BLOCKS)))
		.build();

	public static final BlockPattern DEACTIVATING = BlockPatternBuilder.start()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlockTags.DEACTIVATING_REACTOR_BLOCKS)))
		.build();

	public static final BlockPattern DEACTIVATED = BlockPatternBuilder.start()
		.aisle("~#~", "#~#", "@#@")
		.aisle("###", "~ ~", "###")
		.aisle("~#~", "#~#", "@#@")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlockTags.DEACTIVATED_REACTOR_BLOCKS)))
		.where('@', BlockInWorld.hasState(state -> state.is(NRRBlockTags.POWER_BLOCKS)))
		.build();

}
