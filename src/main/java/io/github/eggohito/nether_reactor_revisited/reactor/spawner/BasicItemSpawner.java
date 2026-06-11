package io.github.eggohito.nether_reactor_revisited.reactor.spawner;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BasicItemSpawner {

	private ResourceKey<LootTable> lootTable;
	private Long activeSince;

	private int requiredPlayerRange = 16;
	private int maxNearbyEntities = 16;
	private int spawnDelay = 20;

	private int spawnCount = 4;
	private int spawnRange = 4;

	public BasicItemSpawner lootTable(@NotNull ResourceKey<LootTable> lootTable) {
		this.lootTable = lootTable;
		return this;
	}

	public BasicItemSpawner lootTable(@NotNull Identifier lootTable) {
		return this.lootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTable));
	}

	public BasicItemSpawner requiredPlayerRange(int requiredPlayerRange) {
		this.requiredPlayerRange = Math.max(requiredPlayerRange, 1);
		return this;
	}

	public BasicItemSpawner maxNearbyEntities(int maxNearbyEntities) {
		this.maxNearbyEntities = Math.max(maxNearbyEntities, 0);
		return this;
	}

	public BasicItemSpawner spawnDelay(int spawnDelay) {
		this.spawnDelay = Math.max(spawnDelay, 1);
		return this;
	}

	public BasicItemSpawner spawnCount(int spawnCount) {
		this.spawnCount = Math.max(spawnCount, 1);
		return this;
	}

	public BasicItemSpawner spawnRange(int spawnRange) {
		this.spawnRange = Math.max(spawnRange, 1);
		return this;
	}

	public void serverTick(ServerLevel level, BlockPos pos) {

		if (!level.isSpawnerBlockEnabled() || !this.hasPlayerNearby(level, pos)) {
			this.reset();
		}

		else {

			if (activeSince == null) {
				this.activeSince = level.getGameTime();
			}

			RandomSource random = level.getRandom();
			long elapsedTicks = level.getGameTime() - activeSince;

			if (elapsedTicks % spawnDelay != 0) {
				return;
			}

			for (int i = 0; i < spawnCount; i++) {

				Vec3 spawnPos = pos.getBottomCenter().add(
					(random.nextDouble() - random.nextDouble()) * this.spawnRange,
					random.nextInt(3) - 1,
					(random.nextDouble() - random.nextDouble()) * this.spawnRange
				);

				for (var item : this.getItemsToDrop(level, spawnPos)) {

					if (this.hasOtherItemsNearby(level, spawnPos)) {
						continue;
					}

					ItemEntity entity = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), item);
					entity.snapTo(spawnPos, random.nextFloat() * 360.0F, 0.0F);
					entity.setDefaultPickUpDelay();

					level.addFreshEntity(entity);

				}

			}

		}

	}

	public void reset() {
		this.activeSince = null;
	}

	public void save(ValueOutput output) {
		output.storeNullable("loot_table", LootTable.KEY_CODEC, this.lootTable);
		output.storeNullable("active_since", Codec.LONG, this.activeSince);
		output.putInt("required_player_range", this.requiredPlayerRange);
		output.putInt("spawn_delay", this.spawnDelay);
		output.putInt("spawn_count", this.spawnCount);
		output.putInt("spawn_range", this.spawnRange);
	}

	public void load(ValueInput input) {
		this.lootTable = input.read("loot_table", LootTable.KEY_CODEC).orElse(null);
		this.activeSince = input.read("active_since", Codec.LONG).orElse(null);
		this.requiredPlayerRange = input.getIntOr("required_player_range", 16);
		this.spawnDelay = input.getIntOr("spawn_delay", 20);
		this.spawnCount = input.getIntOr("spawn_count", 4);
		this.spawnRange = input.getIntOr("spawn_range", 4);
	}

	private List<ItemStack> getItemsToDrop(ServerLevel level, Vec3 pos) {

		LootParams lootParams = new LootParams.Builder(level)
			.withParameter(LootContextParams.ORIGIN, pos)
			.create(LootContextParamSets.CHEST);

		return level.getServer().reloadableRegistries()
			.getLootTable(this.lootTable)
			.getRandomItems(lootParams, level.getRandom());

	}

	private boolean hasOtherItemsNearby(Level level, Vec3 pos) {

		AABB spawnBox = AABB.unitCubeFromLowerCorner(pos).inflate(this.spawnRange);
		int nearby = level.getEntities(EntityTypeTest.forClass(ItemEntity.class), spawnBox, EntitySelector.NO_SPECTATORS).size();

		return nearby >= maxNearbyEntities;

	}

	private boolean hasPlayerNearby(Level level, BlockPos pos) {
		Vec3 centerPos = pos.getCenter();
		return level.hasNearbyAlivePlayer(centerPos.x(), centerPos.y(), centerPos.z(), this.requiredPlayerRange);
	}

}
