package io.github.eggohito.nether_reactor_revisited.mixin.access;

import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {

	@Accessor
	void setRequiredPlayerRange(int requiredPlayerRange);

	@Accessor
	void setMaxNearbyEntities(int maxNearbyEntities);

	@Accessor
	void setMinSpawnDelay(int minSpawnDelay);

	@Accessor
	void setMaxSpawnDelay(int maxSpawnDelay);

	@Accessor
	void setSpawnDelay(int spawnDelay);

	@Accessor
	void setSpawnCount(int spawnCount);

	@Accessor
	void setSpawnRange(int spawnRange);

	@Accessor
	void setNextSpawnData(SpawnData spawnData);

}
