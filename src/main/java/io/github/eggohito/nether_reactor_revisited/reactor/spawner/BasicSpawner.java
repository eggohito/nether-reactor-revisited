package io.github.eggohito.nether_reactor_revisited.reactor.spawner;

import io.github.eggohito.nether_reactor_revisited.mixin.access.BaseSpawnerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;

public abstract class BasicSpawner<S extends BasicSpawner<S>> extends BaseSpawner {

	@Override
	public void broadcastEvent(Level level, BlockPos pos, int id) {

	}

	protected abstract S getThis();

	public S entityId(EntityType<?> entityType) {

		SpawnData spawnData = new SpawnData();
		spawnData.getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());

		((BaseSpawnerAccessor) this).setNextSpawnData(spawnData);
		return getThis();

	}

	public S requiredPlayerRange(int requiredPlayerRange) {
		((BaseSpawnerAccessor) this).setRequiredPlayerRange(requiredPlayerRange);
		return getThis();
	}

	public S maxNearbyEntities(int maxNearbyEntities) {
		((BaseSpawnerAccessor) this).setMaxNearbyEntities(maxNearbyEntities);
		return getThis();
	}

	public S minSpawnDelay(int minSpawnDelay) {
		((BaseSpawnerAccessor) this).setMinSpawnDelay(minSpawnDelay);
		return getThis();
	}

	public S maxSpawnDelay(int maxSpawnDelay) {
		((BaseSpawnerAccessor) this).setMaxSpawnDelay(maxSpawnDelay);
		return getThis();
	}

	public S spawnDelay(int spawnDelay) {
		((BaseSpawnerAccessor) this).setSpawnDelay(spawnDelay);
		return getThis();
	}

	public S spawnCount(int spawnCount) {
		((BaseSpawnerAccessor) this).setSpawnCount(spawnCount);
		return getThis();
	}

	public S spawnRange(int spawnRange) {
		((BaseSpawnerAccessor) this).setSpawnRange(spawnRange);
		return getThis();
	}

}
