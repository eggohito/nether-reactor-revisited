package io.github.eggohito.nether_reactor_revisited.reactor;

import io.github.eggohito.nether_reactor_revisited.content.NRRBlocks;
import net.minecraft.world.level.block.Blocks;
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
		.where('#', BlockInWorld.hasState(state -> state.is(Blocks.COBBLESTONE)))
		.where('@', BlockInWorld.hasState(state -> state.is(Blocks.GOLD_BLOCK)))
		.build();

	public static final BlockPattern ACTIVATING = BlockPatternBuilder.start()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlocks.GLOWING_OBSIDIAN) || state.is(Blocks.COBBLESTONE) || state.is(Blocks.OBSIDIAN)))
		.build();

	public static final BlockPattern ACTIVATED = BlockPatternBuilder.start()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlocks.GLOWING_OBSIDIAN)))
		.build();

	public static final BlockPattern DEACTIVATING = BlockPatternBuilder.start()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlocks.GLOWING_OBSIDIAN) || state.is(Blocks.OBSIDIAN)))
		.build();

	public static final BlockPattern DEACTIVATED = BlockPatternBuilder.start()
		.aisle("~#~", "#~#", "@#@")
		.aisle("###", "~ ~", "###")
		.aisle("~#~", "#~#", "@#@")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(Blocks.OBSIDIAN)))
		.where('@', BlockInWorld.hasState(state -> state.is(Blocks.GOLD_BLOCK)))
		.build();

}
