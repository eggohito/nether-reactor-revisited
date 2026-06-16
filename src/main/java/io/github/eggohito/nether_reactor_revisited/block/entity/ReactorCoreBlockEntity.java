package io.github.eggohito.nether_reactor_revisited.block.entity;

import com.google.common.cache.LoadingCache;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
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

	private final ThreadLocal<StructureTemplate> spireStructure = new ThreadLocal<>();
	private final ThreadLocal<StructureTemplate> degenSpireStructure = new ThreadLocal<>();

	private final AggroSpawner mobSpawner = new AggroSpawner()
		.entityId(EntityType.ZOMBIFIED_PIGLIN)
		.maxNearbyEntities(8)
		.maxSpawnDelay(60)
		.minSpawnDelay(20)
		.spawnRange(8)
		.spawnCount(2);
	private final BasicItemSpawner itemSpawner = new BasicItemSpawner()
		.lootTable(LOOT_TABLE_ID)
		.maxNearbyEntities(64)
		.spawnRange(8);

	private Vec3i structureDimensions = Vec3i.ZERO;
	private long lastChangeGameTime = 0L;
	private int steps = 0;

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
		output.store("structure_dimensions", Vec3i.CODEC, this.structureDimensions);
		output.putLong("last_change_game_time", this.lastChangeGameTime);
		output.putInt("steps", this.steps);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		input.child("mob_spawner").ifPresent(child -> this.mobSpawner.load(this.getLevel(), this.getBlockPos(), child));
		input.child("item_spawner").ifPresent(this.itemSpawner::load);
		this.structureDimensions = input.read("structure_dimensions", Vec3i.CODEC).orElse(Vec3i.ZERO);
		this.lastChangeGameTime = input.getLongOr("last_change_game_time", 0L);
		this.steps = input.getIntOr("steps", 0);
	}

	@Override
	public void setChanged() {

		super.setChanged();

		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}

	}

	public ReactorPhase phase() {
		return this.getBlockState().getValue(ReactorCoreBlock.PHASE);
	}

	public Vec3i structureDimensions() {
		return structureDimensions;
	}

	public long lastChangeGameTime() {
		return lastChangeGameTime;
	}

	public int steps() {
		return steps;
	}

	public void trigger() {

		if (!(this.getLevel() instanceof ServerLevel serverLevel) || !this.generateSpire(serverLevel)) {
			return;
		}

		this.changePhase(ReactorPhase.ACTIVATING);

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

		RandomSource random = level.getRandom();
		BlockPos centeredPos = this.getBlockPos().offset(-this.structureDimensions().getX() / 2, -2, -this.structureDimensions().getZ() / 2);

		StructurePlaceSettings placeSettings = new StructurePlaceSettings().clearProcessors()
			.addProcessor(new ProtectedBlockProcessor(NRRBlockTags.SPIRE_CANNOT_REPLACE))
			.setRandom(random);

		return spireStructure.get().placeInWorld(level, centeredPos, centeredPos, placeSettings, random, Block.UPDATE_CLIENTS);

	}

	public void degenerateSpire(ServerLevel level) {

		if (degenSpireStructure.get() == null) {
			return;
		}

		RandomSource random = level.getRandom();
		BlockPos centeredPos = this.getBlockPos().offset(-this.structureDimensions().getX() / 2, -2, -this.structureDimensions().getZ() / 2);

		StructurePlaceSettings placeSettings = new StructurePlaceSettings().clearProcessors()
			.addProcessor(new BlockRotProcessor(0.25F))
			.addProcessor(new ProtectedBlockProcessor(NRRBlockTags.SPIRE_CANNOT_REPLACE))
			.setRandom(random);

		degenSpireStructure.get().placeInWorld(level, centeredPos, centeredPos, placeSettings, random, Block.UPDATE_CLIENTS);

	}

	protected boolean cacheStructures(ServerLevel level) {

		StructureTemplate structure = level.getStructureManager().get(STRUCTURE_ID).orElse(null);
		StructureTemplate intermediate = new StructureTemplate();

		if (Objects.equals(spireStructure.get(), structure)) {
			return false;
		}

		else if (structure != null) {

			for (var palette : ((StructureTemplateAccessor) structure).getPalettes()) {

				List<StructureTemplate.StructureBlockInfo> convertedBlocks = new ObjectArrayList<>();

				for (var block : palette.blocks()) {
					convertedBlocks.add(new StructureTemplate.StructureBlockInfo(block.pos(), Blocks.AIR.defaultBlockState(), null));
				}

				((StructureTemplateAccessor) intermediate).getPalettes().add(StructureTemplatePaletteAccessor.newPalette(convertedBlocks));

			}

			((StructureTemplateAccessor) intermediate).setSize(structure.getSize());

			this.spireStructure.set(structure);
			this.degenSpireStructure.set(intermediate);

			this.structureDimensions = structure.getSize();

		}

		else {

			this.spireStructure.remove();
			this.degenSpireStructure.remove();

			this.structureDimensions = Vec3i.ZERO;

		}

		return true;

	}

	protected void step() {
		this.steps = Math.min(this.steps() + 1, this.phase().getPattern().getHeight());
	}

	protected void changePhase(ReactorPhase phase) {

		if (this.getLevel() == null) {
			return;
		}

		this.getLevel().setBlock(this.getBlockPos(), this.getBlockState().setValue(ReactorCoreBlock.PHASE, phase), Block.UPDATE_CLIENTS);
		this.lastChangeGameTime = this.getLevel().getGameTime();
		this.steps = 0;

	}

	protected void replaceLayerWith(Level level, @NotNull BlockPattern.BlockPatternMatch match, Predicate<BlockInWorld> predicate, BlockState replacementState, int layerY) {
		replaceLayerWith(level, ((BlockPatternMatchAccessor) match).getCache(), match.getFrontTopLeft(), match.getForwards(), match.getUp(), predicate, replacementState, layerY);
	}

	protected void replaceLayerWith(Level level, LoadingCache<BlockPos, BlockInWorld> levelCache, BlockPos frontTopLeft, Direction forwards, Direction up, Predicate<BlockInWorld> predicate, BlockState replacementState, int layerY) {

		for (int x = 0; x < phase().getPattern().getWidth(); x++) {
			for (int z = 0; z < phase().getPattern().getDepth(); z++) {

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

		BlockPattern pattern = entity.phase().getPattern();
		BlockPos frontTopLeft = pos.offset(pattern.getWidth() / 3, pattern.getHeight() / 3, pattern.getDepth() / 3);

		boolean changed = entity.cacheStructures(serverLevel);
		long elapsedTicks = serverLevel.getGameTime() - entity.lastChangeGameTime();

		switch (entity.phase()) {
			case ACTIVATED_STABLE -> {

				boolean patternFailed = pattern.matches(serverLevel, frontTopLeft, Direction.WEST, Direction.UP) == null;
				boolean maxTimeReached = elapsedTicks >= serverLevel.getGameRules().get(NRRGameRules.STABLE_LIFETIME);

				if (patternFailed) {
					entity.changePhase(ReactorPhase.ACTIVATED_UNSTABLE);
				}

				else if (maxTimeReached) {
					entity.changePhase(ReactorPhase.DEACTIVATING);
				}

				else {
					entity.tickSpawners(serverLevel);
				}

				changed = patternFailed || maxTimeReached;

			}
			case ACTIVATED_UNSTABLE -> {

				if (elapsedTicks >= serverLevel.getGameRules().get(NRRGameRules.UNSTABLE_LIFETIME)) {
					serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
					serverLevel.explode(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, Level.ExplosionInteraction.BLOCK);
				}

			}
			case ACTIVATING -> {

				if (elapsedTicks % 20 == 0) {

					BlockPattern.BlockPatternMatch match = pattern.matches(serverLevel, frontTopLeft, Direction.WEST, Direction.UP);
					changed = true;

					if (match == null) {
						entity.changePhase(ReactorPhase.ACTIVATED_UNSTABLE);
					}

					else if (entity.steps() >= pattern.getHeight()) {

						entity.replaceLayerWith(
							serverLevel,
							match,
							BlockInWorld.hasState(matched -> matched.is(NRRBlockTags.POWER_BLOCKS)),
							NRRBlocks.GLOWING_OBSIDIAN.defaultBlockState(),
							pattern.getHeight() - 1
						);

						entity.changePhase(ReactorPhase.ACTIVATED_STABLE);

					}

					else {

						entity.replaceLayerWith(
							serverLevel,
							match,
							BlockInWorld.hasState(matched -> matched.is(NRRBlockTags.ACTIVATING_REACTOR_BLOCKS)),
							NRRBlocks.GLOWING_OBSIDIAN.defaultBlockState(),
							(pattern.getHeight() - 1) - entity.steps()
						);

						entity.step();

					}

				}

			}
			case DEACTIVATING -> {

				if (elapsedTicks % 20 == 0) {

					LoadingCache<BlockPos, BlockInWorld> levelCache = BlockPattern.createLevelCache(serverLevel, false);
					entity.replaceLayerWith(
						serverLevel,
						levelCache,
						frontTopLeft,
						Direction.WEST,
						Direction.UP,
						BlockInWorld.hasState(Predicate.not(_state -> _state.is(NRRBlocks.REACTOR_CORE))),
						Blocks.OBSIDIAN.defaultBlockState(),
						entity.steps()
					);

					entity.step();
					changed = true;

					if (entity.steps() >= pattern.getHeight()) {
						entity.degenerateSpire(serverLevel);
						entity.changePhase(ReactorPhase.DEACTIVATED);
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

}
