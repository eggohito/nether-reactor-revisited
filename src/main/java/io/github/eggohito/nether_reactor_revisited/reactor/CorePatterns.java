package io.github.eggohito.nether_reactor_revisited.reactor;

import io.github.eggohito.nether_reactor_revisited.content.NRRBlocks;
import io.github.eggohito.nether_reactor_revisited.util.FailableBlockPattern;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public final class CorePatterns {

	public static final FailableBlockPattern NORMAL = FailableBlockPattern.builder()
		.aisle("~#~", "#~#", "@#@")
		.aisle("###", "~ ~", "###")
		.aisle("~#~", "#~#", "@#@")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(Blocks.COBBLESTONE)))
		.where('@', BlockInWorld.hasState(state -> state.is(Blocks.GOLD_BLOCK)))
		.build();

	public static final FailableBlockPattern ACTIVATED = FailableBlockPattern.builder()
		.aisle(" # ", "#~#", " # ")
		.aisle("###", "~ ~", "###")
		.aisle(" # ", "#~#", " # ")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(NRRBlocks.GLOWING_OBSIDIAN)))
		.build();

	public static final FailableBlockPattern DEACTIVATED = FailableBlockPattern.builder()
		.aisle("~#~", "#~#", "@#@")
		.aisle("###", "~ ~", "###")
		.aisle("~#~", "#~#", "@#@")
		.where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
		.where('#', BlockInWorld.hasState(state -> state.is(Blocks.OBSIDIAN)))
		.where('@', BlockInWorld.hasState(state -> state.is(Blocks.GOLD_BLOCK)))
		.build();

}
