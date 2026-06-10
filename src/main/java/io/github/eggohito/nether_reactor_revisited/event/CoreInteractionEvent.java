package io.github.eggohito.nether_reactor_revisited.event;

import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface CoreInteractionEvent {
	InteractionResult interact(Level level, BlockPos pos, BlockState state, ReactorCoreBlockEntity core, Player user, InteractionHand hand, BlockHitResult hitResult, BlockInteractionPhase interactionPhase);
}
