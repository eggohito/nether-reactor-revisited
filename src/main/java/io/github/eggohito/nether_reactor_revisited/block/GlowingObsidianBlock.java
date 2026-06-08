package io.github.eggohito.nether_reactor_revisited.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class GlowingObsidianBlock extends Block {

	public static final MapCodec<GlowingObsidianBlock> CODEC = simpleCodec(GlowingObsidianBlock::new);

	private static final double PARTICLE_CHANCE = 0.3;
	private static final double OFFSET = 0.5625;

	public GlowingObsidianBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected @NonNull MapCodec<? extends Block> codec() {
		return CODEC;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

		if (random.nextDouble() >= PARTICLE_CHANCE) {
			return;
		}

		for (var direction : Direction.values()) {

			Direction.Axis axis = direction.getAxis();
			BlockPos sidePos = pos.relative(direction);

			if (level.getBlockState(sidePos).isSolidRender()) {
				continue;
			}

			double xVariance = axis == Direction.Axis.X ? 0.5 + OFFSET * direction.getStepX() : random.nextDouble();
			double yVariance = axis == Direction.Axis.Y ? 0.5 + OFFSET * direction.getStepY() : random.nextDouble();
			double zVariance = axis == Direction.Axis.Z ? 0.5 + OFFSET * direction.getStepZ() : random.nextDouble();

			float red = (random.nextFloat() * 0.2F + 0.8F) * (random.nextFloat() * 0.4F + 0.6F);
			DustParticleOptions particle = new DustParticleOptions(ARGB.colorFromFloat(1.0F, red, 0.0F, 0.0F), 1.2F);

			level.addParticle(particle, particle.getType().getOverrideLimiter(), false, pos.getX() + xVariance, pos.getY() + yVariance, pos.getZ() + zVariance, 0.0, 0.0, 0.0);

		}

	}

}
