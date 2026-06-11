package io.github.eggohito.nether_reactor_revisited.mixin.impl.aggro_spawner;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.nether_reactor_revisited.reactor.spawner.AggroSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

	@Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
	void setAggroToNearestPlayer(ServerLevel level, BlockPos pos, CallbackInfo ci, @Local(name = "entity") Entity entity) {

		if ((BaseSpawner) (Object) this instanceof AggroSpawner && entity instanceof NeutralMob neutralMob) {
			neutralMob.setTarget(level.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 8.0, true));
		}

	}

}
