package io.github.eggohito.nether_reactor_revisited.reactor;

import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.event.BlockInteractionPhase;
import io.github.eggohito.nether_reactor_revisited.event.CoreInteractionEvent;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface ReactorEvents {

	CoreInteractionEvent REACTOR_TOO_HIGH = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if ((interactionPhase != BlockInteractionPhase.WITH_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.DEACTIVATING) && (interactionPhase != BlockInteractionPhase.WITHOUT_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.NORMAL)) {
			return InteractionResult.PASS;
		}

		else if (pos.getY() + core.structureDimensions().getY() >= level.getMaxY()) {
			user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.too_high").withStyle(ChatFormatting.RED));
			return InteractionResult.CONSUME;
		}

		else {
			return InteractionResult.PASS;
		}

	};

	CoreInteractionEvent REACTOR_TOO_LOW = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if ((interactionPhase != BlockInteractionPhase.WITH_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.DEACTIVATING) && (interactionPhase != BlockInteractionPhase.WITHOUT_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.NORMAL)) {
			return InteractionResult.PASS;
		}

		else if (pos.getY() <= level.getMinY() + 16) {
			user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.too_low").withStyle(ChatFormatting.RED));
			return InteractionResult.CONSUME;
		}

		else {
			return InteractionResult.PASS;
		}

	};

	CoreInteractionEvent CHECK_IF_LEVEL_WITH_REACTOR = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if ((interactionPhase != BlockInteractionPhase.WITH_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.DEACTIVATING) && (interactionPhase != BlockInteractionPhase.WITHOUT_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.NORMAL)) {
			return InteractionResult.PASS;
		}

		else if (user.getBlockY() == pos.below().getY()) {
			return InteractionResult.PASS;
		}

		else {
			user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.not_level_with_reactor").withStyle(ChatFormatting.RED));
			return InteractionResult.CONSUME;
		}

	};

	CoreInteractionEvent CHECK_FOR_OTHER_NEARBY_REACTORS = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if ((interactionPhase != BlockInteractionPhase.WITH_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.DEACTIVATING) && (interactionPhase != BlockInteractionPhase.WITHOUT_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.NORMAL)) {
			return InteractionResult.PASS;
		}

		Iterable<BlockPos> withinManhattan = BlockPos.withinManhattan(pos, 32, 32, 32);
		long nearbyReactors = 0;

		for (var nearbyPos : withinManhattan) {

			if (level.hasChunkAt(nearbyPos) && level.getBlockState(nearbyPos).getBlock() instanceof ReactorCoreBlock) {
				nearbyReactors++;
			}

		}

		if (nearbyReactors > 1) {
			user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.nearby_cores_found", nearbyReactors - 1).withStyle(ChatFormatting.RED));
			return InteractionResult.CONSUME;
		}

		else {
			return InteractionResult.PASS;
		}

	};

	CoreInteractionEvent CHECK_IF_NEARBY_PLAYERS_ARE_TOO_FAR = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if ((interactionPhase != BlockInteractionPhase.WITH_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.DEACTIVATING) && (interactionPhase != BlockInteractionPhase.WITHOUT_ITEM || state.getValue(ReactorCoreBlock.PHASE) != ReactorPhase.NORMAL)) {
			return InteractionResult.PASS;
		}

		else {

			Vec3 centerPos = pos.getCenter();
			int nearbyPlayersThatAreTooFar = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(12), EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(player -> player.position().distanceTo(centerPos) > 6)).size();

			if (nearbyPlayersThatAreTooFar > 0) {
				user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.nearby_players_too_far", nearbyPlayersThatAreTooFar).withStyle(ChatFormatting.RED));
				return InteractionResult.CONSUME;
			}

			else {
				return InteractionResult.PASS;
			}

		}

	};

	CoreInteractionEvent CHECK_ELAPSED_ACTIVE_SECONDS = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		ReactorPhase phase = core.phase();
		long elapsedSeconds = (level.getGameTime() - core.lastChangeGameTime()) / 20;

		if (phase == ReactorPhase.ACTIVATED_UNSTABLE || phase == ReactorPhase.ACTIVATED_STABLE) {

			if (level instanceof ServerLevel serverLevel) {

				if (phase == ReactorPhase.ACTIVATED_UNSTABLE) {
					user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.elapsed_active_seconds.unstable", (serverLevel.getGameRules().get(NRRGameRules.UNSTABLE_CORE_LIFETIME) / 20) - elapsedSeconds).withStyle(ChatFormatting.RED));
				}

				else {
					user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.elapsed_active_seconds.stable", elapsedSeconds).withStyle(ChatFormatting.RED));
				}

			}

			return InteractionResult.CONSUME;

		}

		return InteractionResult.PASS;

	};

	CoreInteractionEvent ACTIVATE = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if (interactionPhase != BlockInteractionPhase.WITHOUT_ITEM || core.phase() != ReactorPhase.NORMAL) {
			return InteractionResult.PASS;
		}

		else if (hasCorrectStructure(level, pos, core, user)) {

			if (level instanceof ServerLevel serverLevel) {
				serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("event.nether-reactor-revisited.activate.success", user.getName()).withStyle(ChatFormatting.GREEN), false);
				core.trigger();
			}

			return InteractionResult.SUCCESS;

		}

		else {
			user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.activate.fail").withStyle(ChatFormatting.RED));
			return InteractionResult.CONSUME;
		}

	};

	CoreInteractionEvent REACTIVATE = (level, pos, state, core, user, hand, hitResult, interactionPhase) -> {

		if (interactionPhase != BlockInteractionPhase.WITH_ITEM || core.phase() != ReactorPhase.DEACTIVATED) {
			return InteractionResult.PASS;
		}

		else if (hasCorrectStructure(level, pos, core, user)) {

			ItemStack item = user.getItemInHand(hand);
			MinecraftServer server = level.getServer();

			if (item.is(ConventionalItemTags.DIAMOND_GEMS)) {

				if (server != null) {

					server.getPlayerList().broadcastSystemMessage(Component.translatable("event.nether-reactor-revisited.reactivate.success", user.getName()).withStyle(ChatFormatting.GREEN), false);

					item.consume(1, user);
					core.trigger();

				}

				return InteractionResult.SUCCESS;

			}

			else {
				user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.reactivate.fail.missing_item").withStyle(ChatFormatting.RED));
				return InteractionResult.CONSUME;
			}

		}

		else {
			user.sendOverlayMessage(Component.translatable("event.nether-reactor-revisited.reactivate.fail.incorrect_pattern").withStyle(ChatFormatting.RED));
			return InteractionResult.CONSUME;
		}

	};

	private static boolean hasCorrectStructure(Level level, BlockPos pos, ReactorCoreBlockEntity core, Player user) {

		Direction facing = user.getDirection();
		Direction oppositeFacing = facing.getOpposite();

		BlockPos frontTopLeftPos = pos.relative(oppositeFacing).offset(-oppositeFacing.getStepZ(), 1, oppositeFacing.getStepX());
		return core.phase().getPattern().matches(level, frontTopLeftPos, facing, Direction.UP) != null;

	}

}
