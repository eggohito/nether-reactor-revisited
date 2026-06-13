package io.github.eggohito.nether_reactor_revisited.content;

import com.mojang.serialization.Codec;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;

public final class NRRAttachments {

	public static final AttachmentType<Long> LAST_REACTOR_INTERRUPTED_GAME_TIME = AttachmentRegistry.create(
		NetherReactorRevisited.id("last_reactor_interrupted_game_time"),
		builder -> builder.persistent(Codec.LONG)
	);

	public static void registerAll() {
		ServerTickEvents.END_LEVEL_TICK.register(LAST_REACTOR_INTERRUPTED_GAME_TIME.identifier(), NRRAttachments::handleReactorInterruption);
	}

	private static void handleReactorInterruption(ServerLevel level) {

		if (!level.hasAttached(LAST_REACTOR_INTERRUPTED_GAME_TIME)) {
			return;
		}

		Timeline dayTimeline = level.registryAccess().get(Timelines.OVERWORLD_DAY)
			.map(Holder.Reference::value)
			.orElse(null);

		if (dayTimeline != null && dayTimeline.periodTicks().isPresent()) {

			ServerClockManager clockManager = level.clockManager();
			Holder<WorldClock> clock = dayTimeline.clock();

			long dayPeriod = dayTimeline.periodTicks().get();
			long dayCycleFrozenFor = level.getGameRules().get(NRRGameRules.DAY_CYCLE_FROZEN_FOR);

			if (dayCycleFrozenFor > 0 && level.getGameTime() % (dayPeriod * dayCycleFrozenFor) != 0) {
				return;
			}

			level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("event.nether-reactor-revisited.restore_day_cycle").withStyle(ChatFormatting.GREEN), false);
			clockManager.setPaused(clock, false);

		}

		level.removeAttached(LAST_REACTOR_INTERRUPTED_GAME_TIME);

	}

}
