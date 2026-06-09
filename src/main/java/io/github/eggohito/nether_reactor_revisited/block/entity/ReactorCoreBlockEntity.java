package io.github.eggohito.nether_reactor_revisited.block.entity;

import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlocks;
import io.github.eggohito.nether_reactor_revisited.mixin.access.BlockPatternAccessor;
import io.github.eggohito.nether_reactor_revisited.mixin.access.BlockPatternMatchAccessor;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.core.CoreState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ReactorCoreBlockEntity extends BlockEntity {

	private InPhase inPhase = InPhase.NONE;
	private int phaseLevel = 0;

	public ReactorCoreBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(NRRBlockEntities.REACTOR_CORE, worldPosition, blockState);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		output.store("in_phase", InPhase.CODEC, this.inPhase);
		output.putInt("phase_level", this.phaseLevel);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		this.inPhase = input.read("in_phase", InPhase.CODEC).orElse(InPhase.NONE);
		this.phaseLevel = input.getIntOr("phase_level", 0);
	}

	public void copyState(CoreState state) {
		this.changePhase(state.asPhase());
	}

	public void trigger() {

		if (!(this.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}

		this.changePhase(ReactorPhase.ACTIVATING);
		serverLevel.setBlock(this.getBlockPos(), this.getBlockState().setValue(ReactorCoreBlock.STATE, CoreState.ACTIVATED), Block.UPDATE_CLIENTS);

		ServerClockManager clockManager = serverLevel.clockManager();
		serverLevel.dimensionType()
			.defaultClock()
			.ifPresent(clock -> clockManager.moveToTimeMarker(clock, ClockTimeMarkers.MIDNIGHT));

		this.setChanged();

	}

	protected void changePhase(ReactorPhase phase) {

		if (!(this.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}

		this.inPhase = new InPhase(phase, serverLevel.getGameTime());
		this.phaseLevel = 0;

	}

	protected void replaceLayerWith(Level level, @NotNull BlockPattern.BlockPatternMatch match, Predicate<BlockInWorld> predicate, BlockState replacementState, int layerY) {
		replaceLayerWith(level, ((BlockPatternMatchAccessor) match).getCache(), match.getFrontTopLeft(), match.getForwards(), match.getUp(), predicate, replacementState, layerY);
	}

	protected void replaceLayerWith(Level level, LoadingCache<BlockPos, BlockInWorld> levelCache, BlockPos frontTopLeft, Direction forwards, Direction up, Predicate<BlockInWorld> predicate, BlockState replacementState, int layerY) {

		for (int x = 0; x < inPhase.pattern().getWidth(); x++) {
			for (int z = 0; z < inPhase.pattern().getDepth(); z++) {

				BlockPos pos = BlockPatternAccessor.callTranslateAndRotate(frontTopLeft, forwards, up, x, layerY, z);
				BlockInWorld matchedBlock = levelCache.getUnchecked(pos);

				if (predicate.test(matchedBlock)) {
					level.setBlock(matchedBlock.getPos(), replacementState, Block.UPDATE_CLIENTS);
				}

			}
		}

	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ReactorCoreBlockEntity entity) {

		BlockPattern pattern = entity.inPhase.pattern();
		BlockPos frontTopLeft = pos.offset(pattern.getWidth() / 3, pattern.getHeight() / 3, pattern.getDepth() / 3);

		boolean changed = false;
		long elapsedTicks = level.getGameTime() - entity.inPhase.since();

		switch (entity.inPhase.name()) {
			case STABLE -> {

				boolean patternFailed = pattern.matches(level, frontTopLeft, Direction.WEST, Direction.UP) == null;
				boolean maxTimeReached = elapsedTicks >= /* 900 */ 100;

				if (patternFailed) {
					entity.changePhase(ReactorPhase.UNSTABLE);
				}

				else if (maxTimeReached) {
					entity.changePhase(ReactorPhase.DEACTIVATING);
				}

				changed = patternFailed || maxTimeReached;

			}
			case UNSTABLE -> {

				if (elapsedTicks >= /* 100 */ 20) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
					level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, Level.ExplosionInteraction.BLOCK);
				}

			}
			case ACTIVATING -> {

				if (elapsedTicks % 20 == 0) {

					BlockPattern.BlockPatternMatch match = pattern.matches(level, frontTopLeft, Direction.WEST, Direction.UP);
					changed = match == null;

					if (match == null) {
						entity.changePhase(ReactorPhase.UNSTABLE);
					}

					else if (entity.phaseLevel >= pattern.getHeight()) {

						entity.replaceLayerWith(
							level,
							match,
							BlockInWorld.hasState(matched -> matched.is(Blocks.GOLD_BLOCK)),
							NRRBlocks.GLOWING_OBSIDIAN.defaultBlockState(),
							pattern.getHeight() - 1
						);

						entity.changePhase(ReactorPhase.STABLE);

					}

					else {

						entity.replaceLayerWith(
							level,
							match,
							BlockInWorld.hasState(matched -> matched.is(Blocks.COBBLESTONE) || matched.is(Blocks.OBSIDIAN)),
							NRRBlocks.GLOWING_OBSIDIAN.defaultBlockState(),
							(pattern.getHeight() - 1) - entity.phaseLevel
						);

						entity.phaseLevel = Math.min(entity.phaseLevel + 1, pattern.getHeight());
						changed = true;

					}

				}

			}
			case DEACTIVATING -> {

				if (elapsedTicks % 20 == 0) {

					if (state.getValue(ReactorCoreBlock.STATE) != CoreState.DEACTIVATED) {
						level.setBlock(pos, state.setValue(ReactorCoreBlock.STATE, CoreState.DEACTIVATED), Block.UPDATE_CLIENTS);
					}

					else {

						LoadingCache<BlockPos, BlockInWorld> levelCache = BlockPattern.createLevelCache(level, false);
						entity.replaceLayerWith(
							level,
							levelCache,
							frontTopLeft,
							Direction.WEST,
							Direction.UP,
							BlockInWorld.hasState(Predicate.not(_state -> _state.is(NRRBlocks.REACTOR_CORE))),
							Blocks.OBSIDIAN.defaultBlockState(),
							entity.phaseLevel
						);

						entity.phaseLevel = Math.min(entity.phaseLevel + 1, pattern.getHeight());
						changed = true;

						if (entity.phaseLevel >= pattern.getHeight()) {
							entity.changePhase(ReactorPhase.NONE);
						}

					}

				}

			}
			default -> {
				//  No-op; null or something else
			}
		}

		if (changed) {
			entity.setChanged();
		}

	}

	public record InPhase(ReactorPhase name, long since) {

		public static final InPhase NONE = new InPhase(ReactorPhase.NONE, 0L);

		public static final Codec<InPhase> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ReactorPhase.CODEC.fieldOf("name").forGetter(InPhase::name),
			Codec.LONG.fieldOf("since").forGetter(InPhase::since)
		).apply(instance, InPhase::new));

		public BlockPattern pattern() {
			return name().getPattern();
		}

	}

}
