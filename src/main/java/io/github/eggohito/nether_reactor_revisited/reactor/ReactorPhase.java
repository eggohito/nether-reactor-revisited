package io.github.eggohito.nether_reactor_revisited.reactor;

import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import io.github.eggohito.nether_reactor_revisited.event.BlockInteractionPhase;
import io.github.eggohito.nether_reactor_revisited.event.CoreInteractionEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public enum ReactorPhase implements StringRepresentable, ReactorEvents {

	NORMAL(
		"normal",
		ReactorPatterns.NORMAL, ChatFormatting.GREEN,
		REACTOR_TOO_HIGH, REACTOR_TOO_LOW,
		CHECK_FOR_OTHER_NEARBY_REACTORS, CHECK_IF_NEARBY_PLAYERS_ARE_TOO_FAR,
		CHECK_IF_LEVEL_WITH_REACTOR, ACTIVATE
	),

	ACTIVATING(
		"activating",
		ReactorPatterns.ACTIVATING, ChatFormatting.RED
	),

	ACTIVATED_STABLE(
		"activated_stable",
		ReactorPatterns.ACTIVATED , ChatFormatting.RED,
		CHECK_ELAPSED_ACTIVE_SECONDS
	),

	ACTIVATED_UNSTABLE(
		"activated_unstable",
		ReactorPatterns.ACTIVATED, ChatFormatting.RED,
		CHECK_ELAPSED_ACTIVE_SECONDS
	),

	DEACTIVATING(
		"deactivating",
		ReactorPatterns.DEACTIVATING, ChatFormatting.DARK_PURPLE
	),

	DEACTIVATED(
		"deactivated",
		ReactorPatterns.DEACTIVATED, ChatFormatting.DARK_PURPLE,
		REACTOR_TOO_HIGH, REACTOR_TOO_LOW,
		CHECK_FOR_OTHER_NEARBY_REACTORS, CHECK_IF_LEVEL_WITH_REACTOR,
		CHECK_IF_NEARBY_PLAYERS_ARE_TOO_FAR, REACTIVATE
	);

	final String name;
	final BlockPattern pattern;

	final Component tooltipComponent;
	final CoreInteractionEvent[] events;

	ReactorPhase(String name, BlockPattern pattern, ChatFormatting tooltipFormatting, CoreInteractionEvent... events) {
		this.name = name;
		this.pattern = pattern;
		this.tooltipComponent = Component.translatable("nether-reactor-revisited.reactor_core.phase." + name).withStyle(tooltipFormatting);
		this.events = events;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public BlockPattern getPattern() {
		return pattern;
	}

	public Component getTooltipComponent() {
		return tooltipComponent;
	}

	public CoreInteractionEvent[] getEvents() {
		return events;
	}

	@Nullable
	public InteractionResult tryTrigger(Level level, BlockPos pos, BlockState state, ReactorCoreBlockEntity core, Player user, InteractionHand hand, BlockHitResult hitResult, BlockInteractionPhase phase) {

		for (var event : this.getEvents()) {

			var result = event.interact(level, pos, state, core, user, hand, hitResult, phase);

			if (result != InteractionResult.PASS) {
				return result;
			}

		}

		return null;

	}

	public boolean isActive() {
		return this == ACTIVATING
			|| this == ACTIVATED_STABLE
			|| this == ACTIVATED_UNSTABLE;
	}

}
