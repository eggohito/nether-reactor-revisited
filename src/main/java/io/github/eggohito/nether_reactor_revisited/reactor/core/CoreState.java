package io.github.eggohito.nether_reactor_revisited.reactor.core;

import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import io.github.eggohito.nether_reactor_revisited.event.BlockInteractionPhase;
import io.github.eggohito.nether_reactor_revisited.event.CoreInteractionEvent;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorEvents;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorPhase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public enum CoreState implements StringRepresentable {

	NORMAL(
		"normal",
		ChatFormatting.GREEN,
		ReactorPhase.NORMAL,
		ReactorEvents.REACTOR_TOO_HIGH, ReactorEvents.REACTOR_TOO_LOW,
		ReactorEvents.CHECK_FOR_OTHER_NEARBY_REACTORS, ReactorEvents.CHECK_IF_NEARBY_PLAYERS_ARE_TOO_FAR,
		ReactorEvents.CHECK_IF_LEVEL_WITH_REACTOR, ReactorEvents.ACTIVATE
	),

	ACTIVATED(
		"activated",
		ChatFormatting.RED,
		ReactorPhase.UNSTABLE,
		ReactorEvents.CHECK_ELAPSED_ACTIVE_SECONDS
	),

	DEACTIVATED(
		"deactivated",
		ChatFormatting.DARK_PURPLE,
		ReactorPhase.DEACTIVATED,
		ReactorEvents.REACTOR_TOO_HIGH, ReactorEvents.REACTOR_TOO_LOW,
		ReactorEvents.CHECK_FOR_OTHER_NEARBY_REACTORS, ReactorEvents.CHECK_IF_LEVEL_WITH_REACTOR,
		ReactorEvents.CHECK_IF_NEARBY_PLAYERS_ARE_TOO_FAR, ReactorEvents.REACTIVATE
	);

	final String name;
	final Component tooltipComponent;

	final ReactorPhase phase;
	final CoreInteractionEvent[] events;

	CoreState(String name, ChatFormatting formatting, ReactorPhase phase, CoreInteractionEvent... events) {
		this.name = name;
		this.tooltipComponent = Component.translatable("nether-reactor-revisited.reactor_core.state." + name).withStyle(formatting);
		this.phase = phase;
		this.events = events;
	}

	@Override
	public @NonNull String getSerializedName() {
		return name;
	}

	public Component getTooltipComponent() {
		return tooltipComponent;
	}

	public ReactorPhase asPhase() {
		return phase;
	}

	public CoreInteractionEvent[] getEvents() {
		return events;
	}

	@Nullable
	public InteractionResult triggerEvent(Level level, BlockPos pos, BlockState state, ReactorCoreBlockEntity core, Player user, InteractionHand hand, BlockHitResult hitResult, BlockInteractionPhase interactionPhase) {

		for (var event : this.getEvents()) {

			var result = event.interact(level, pos, state, core, user, hand, hitResult, interactionPhase);

			if (result != InteractionResult.PASS) {
				return result;
			}

		}

		return null;

	}

}
