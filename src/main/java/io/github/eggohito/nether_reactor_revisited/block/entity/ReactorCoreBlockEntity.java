package io.github.eggohito.nether_reactor_revisited.block.entity;

import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockTags;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlocks;
import io.github.eggohito.nether_reactor_revisited.content.NRRGameRules;
import io.github.eggohito.nether_reactor_revisited.mixin.access.BlockPatternAccessor;
import io.github.eggohito.nether_reactor_revisited.mixin.access.BlockPatternMatchAccessor;
import io.github.eggohito.nether_reactor_revisited.mixin.access.StructureTemplateAccessor;
import io.github.eggohito.nether_reactor_revisited.mixin.access.StructureTemplatePaletteAccessor;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.core.CoreState;
import io.github.eggohito.nether_reactor_revisited.reactor.spawner.AggroSpawner;
import io.github.eggohito.nether_reactor_revisited.reactor.spawner.BasicItemSpawner;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ReactorCoreBlockEntity extends BlockEntity {

	private static final Identifier LOOT_TABLE_ID = NetherReactorRevisited.id("spire");
	private static final Identifier STRUCTURE_ID = NetherReactorRevisited.id("spire");

	private final AggroSpawner mobSpawner = new AggroSpawner()
		.entityId(EntityType.ZOMBIFIED_PIGLIN)
		.maxNearbyEntities(8)
		.maxSpawnDelay(80)
		.minSpawnDelay(20)
		.spawnRange(8);
	private final BasicItemSpawner itemSpawner = new BasicItemSpawner()
		.lootTable(LOOT_TABLE_ID)
		.maxNearbyEntities(32)
		.spawnRange(8);

	private final ThreadLocal<StructureTemplate> spireStructure = new ThreadLocal<>();
	private final ThreadLocal<StructureTemplate> degenSpireStructure = new ThreadLocal<>();

	private Status status = Status.NORMAL;
	private Vec3i dimensions = Vec3i.ZERO;
	private int step = 0;

	public ReactorCoreBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(NRRBlockEntities.REACTOR_CORE, worldPosition, blockState);
	}

	@Override
	public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveWithoutMetadata(registries);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		this.mobSpawner.save(output.child("mob_spawner"));
		this.itemSpawner.save(output.child("item_spawner"));
		output.store("status", Status.CODEC, this.status);
		output.store("dimensions", Vec3i.CODEC, this.dimensions);
		output.putInt("step", this.step);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		input.child("mob_spawner").ifPresent(child -> this.mobSpawner.load(this.getLevel(), this.getBlockPos(), child));
		input.child("item_spawner").ifPresent(this.itemSpawner::load);
		this.status = input.read("status", Status.CODEC).orElse(Status.NORMAL);
		this.dimensions = input.read("dimensions", Vec3i.CODEC).orElse(Vec3i.ZERO);
		this.step = input.getIntOr("step", 0);
	}

	@Override
	public void setChanged() {

		super.setChanged();

		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}

	}

	public Status getStatus() {
		return status;
	}

	public Vec3i getDimensions() {
		return dimensions;
	}

	public int getStep() {
		return step;
	}

	public void copyState(CoreState state) {
		this.changePhase(state.asPhase());
	}

	public void trigger() {

		if (!(this.getLevel() instanceof ServerLevel serverLevel) || !this.generateSpire(serverLevel)) {
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

	public void tickSpawners(ServerLevel level) {
		this.mobSpawner.serverTick(level, this.getBlockPos().below());
		this.itemSpawner.serverTick(level, this.getBlockPos().below());
	}

	public boolean generateSpire(ServerLevel level) {

		if (spireStructure.get() == null) {
			return false;
		}

		Vec3i size = spireStructure.get().getSize();
		BlockPos centeredPos = this.getBlockPos().offset(-size.getX() / 2, -2, -size.getZ() / 2);

		return spireStructure.get().placeInWorld(level, centeredPos, centeredPos, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_CLIENTS);

	}

	public void degenerateSpire(ServerLevel level) {

		if (degenSpireStructure.get() == null) {
			return;
		}

		Vec3i size = degenSpireStructure.get().getSize();
		BlockPos centeredPos = this.getBlockPos().offset(-size.getX() / 2, -2, -size.getZ() / 2);

		RandomSource random = level.getRandom();
		StructurePlaceSettings placeSettings = new StructurePlaceSettings();

		placeSettings.clearProcessors()
			.addProcessor(new BlockRotProcessor(0.25F))
			.setRandom(random);

		degenSpireStructure.get().placeInWorld(level, centeredPos, centeredPos, placeSettings, level.getRandom(), Block.UPDATE_CLIENTS);

	}

	protected boolean cacheTemplates(ServerLevel level) {

		StructureTemplateManager templateManager = level.getStructureManager();
		StructureTemplate template = templateManager.get(STRUCTURE_ID).orElse(null);

		if (template == null || Objects.equals(spireStructure.get(), template)) {
			return false;
		}

		StructureTemplate intermediateTemplate = new StructureTemplate();

		for (var originalPalette : ((StructureTemplateAccessor) template).getPalettes()) {

			List<StructureTemplate.StructureBlockInfo> newBlocks = new ObjectArrayList<>();

			for (var oldBlock : originalPalette.blocks()) {
				newBlocks.add(new StructureTemplate.StructureBlockInfo(oldBlock.pos(), Blocks.AIR.defaultBlockState(), null));
			}

			((StructureTemplateAccessor) intermediateTemplate).getPalettes().add(StructureTemplatePaletteAccessor.newPalette(newBlocks));

		}

		((StructureTemplateAccessor) intermediateTemplate).setSize(template.getSize());

		this.dimensions = template.getSize();
		this.spireStructure.set(template);
		this.degenSpireStructure.set(intermediateTemplate);

		return true;

	}

	protected void step() {
		this.step = Math.min(this.getStep() + 1, this.getStatus().pattern().getHeight());
	}

	protected void changePhase(ReactorPhase phase) {

		if (this.getLevel() == null) {
			return;
		}

		this.status = new Status(phase, this.getLevel().getGameTime());
		this.step = 0;

	}

	protected void replaceLayerWith(Level level, @NotNull BlockPattern.BlockPatternMatch match, Predicate<BlockInWorld> predicate, BlockState replacementState, int layerY) {
		replaceLayerWith(level, ((BlockPatternMatchAccessor) match).getCache(), match.getFrontTopLeft(), match.getForwards(), match.getUp(), predicate, replacementState, layerY);
	}

	protected void replaceLayerWith(Level level, LoadingCache<BlockPos, BlockInWorld> levelCache, BlockPos frontTopLeft, Direction forwards, Direction up, Predicate<BlockInWorld> predicate, BlockState replacementState, int layerY) {

		for (int x = 0; x < status.pattern().getWidth(); x++) {
			for (int z = 0; z < status.pattern().getDepth(); z++) {

				BlockPos pos = BlockPatternAccessor.callTranslateAndRotate(frontTopLeft, forwards, up, x, layerY, z);
				BlockInWorld matchedBlock = levelCache.getUnchecked(pos);

				if (predicate.test(matchedBlock)) {
					level.setBlock(matchedBlock.getPos(), replacementState, Block.UPDATE_CLIENTS);
				}

			}
		}

	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ReactorCoreBlockEntity entity) {

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		BlockPattern pattern = entity.getStatus().pattern();
		BlockPos frontTopLeft = pos.offset(pattern.getWidth() / 3, pattern.getHeight() / 3, pattern.getDepth() / 3);

		boolean changed = entity.cacheTemplates(serverLevel);
		long elapsedTicks = serverLevel.getGameTime() - entity.getStatus().since();

		switch (entity.getStatus().phase()) {
			case STABLE -> {

				boolean patternFailed = pattern.matches(serverLevel, frontTopLeft, Direction.WEST, Direction.UP) == null;
				boolean maxTimeReached = elapsedTicks >= serverLevel.getGameRules().get(NRRGameRules.STABLE_CORE_LIFETIME);

				if (patternFailed) {
					entity.changePhase(ReactorPhase.UNSTABLE);
				}

				else if (maxTimeReached) {
					entity.changePhase(ReactorPhase.DEACTIVATING);
				}

				else {
					entity.tickSpawners(serverLevel);
				}

				changed = patternFailed || maxTimeReached;

			}
			case UNSTABLE -> {

				if (elapsedTicks >= serverLevel.getGameRules().get(NRRGameRules.UNSTABLE_CORE_LIFETIME)) {
					serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
					serverLevel.explode(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, Level.ExplosionInteraction.BLOCK);
				}

			}
			case ACTIVATING -> {

				if (elapsedTicks % 20 == 0) {

					BlockPattern.BlockPatternMatch match = pattern.matches(serverLevel, frontTopLeft, Direction.WEST, Direction.UP);
					changed = true;

					if (match == null) {
						entity.changePhase(ReactorPhase.UNSTABLE);
					}

					else if (entity.step >= pattern.getHeight()) {

						entity.replaceLayerWith(
							serverLevel,
							match,
							BlockInWorld.hasState(matched -> matched.is(NRRBlockTags.POWER_BLOCKS)),
							NRRBlocks.GLOWING_OBSIDIAN.defaultBlockState(),
							pattern.getHeight() - 1
						);

						entity.changePhase(ReactorPhase.STABLE);

					}

					else {

						entity.replaceLayerWith(
							serverLevel,
							match,
							BlockInWorld.hasState(matched -> matched.is(NRRBlockTags.ACTIVATING_REACTOR_BLOCKS)),
							NRRBlocks.GLOWING_OBSIDIAN.defaultBlockState(),
							(pattern.getHeight() - 1) - entity.getStep()
						);

						entity.step();

					}

				}

			}
			case DEACTIVATING -> {

				if (elapsedTicks % 20 == 0) {

					if (state.getValue(ReactorCoreBlock.STATE) != CoreState.DEACTIVATED) {
						serverLevel.setBlock(pos, state.setValue(ReactorCoreBlock.STATE, CoreState.DEACTIVATED), Block.UPDATE_CLIENTS);
					}

					else {

						LoadingCache<BlockPos, BlockInWorld> levelCache = BlockPattern.createLevelCache(serverLevel, false);
						entity.replaceLayerWith(
							serverLevel,
							levelCache,
							frontTopLeft,
							Direction.WEST,
							Direction.UP,
							BlockInWorld.hasState(Predicate.not(_state -> _state.is(NRRBlocks.REACTOR_CORE))),
							Blocks.OBSIDIAN.defaultBlockState(),
							entity.getStep()
						);

						entity.step();
						changed = true;

						if (entity.getStep() >= pattern.getHeight()) {
							entity.degenerateSpire(serverLevel);
							entity.changePhase(ReactorPhase.DEACTIVATED);
						}

					}

				}

			}
			default -> {
				//  No-op
			}
		}

		if (changed) {
			entity.setChanged();
		}

	}

	public record Status(ReactorPhase phase, long since) {

		public static final Status NORMAL = new Status(ReactorPhase.NORMAL, 0L);

		public static final Codec<Status> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ReactorPhase.CODEC.fieldOf("phase").forGetter(Status::phase),
			Codec.LONG.fieldOf("since").forGetter(Status::since)
		).apply(instance, Status::new));

		public BlockPattern pattern() {
			return phase().getPattern();
		}

	}

}
