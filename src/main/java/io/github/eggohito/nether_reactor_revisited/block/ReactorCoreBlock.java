package io.github.eggohito.nether_reactor_revisited.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.event.BlockInteractionPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ReactorCoreBlock extends BaseEntityBlock {

	public static final MapCodec<ReactorCoreBlock> CODEC = simpleCodec(ReactorCoreBlock::new);
	public static final Property<ReactorPhase> PHASE = EnumProperty.create("phase", ReactorPhase.class);

	public ReactorCoreBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(PHASE, ReactorPhase.NORMAL));
	}

	@Override
	protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PHASE);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

		if (level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore) {

			var result = reactorCore.phase().tryTrigger(level, pos, state, reactorCore, player, hand, hitResult, BlockInteractionPhase.WITH_ITEM);

			if (result != null) {
				return result;
			}

		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;

	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

		if (level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore) {

			var result = reactorCore.phase().tryTrigger(level, pos, state, reactorCore, player, InteractionHand.MAIN_HAND, hitResult, BlockInteractionPhase.WITHOUT_ITEM);

			if (result != null) {
				return result;
			}

		}

		return InteractionResult.PASS;

	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new ReactorCoreBlockEntity(worldPosition, blockState);
	}

	@Override
	protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {

		if (!(level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore)) {
			return;
		}

		switch (reactorCore.phase()) {
			case ACTIVATING, ACTIVATED_STABLE, DEACTIVATING ->
				reactorCore.degenerateSpire(level);
			case ACTIVATED_UNSTABLE -> {

				if (!Objects.equals(state, level.getBlockState(pos))) {
					level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, Level.ExplosionInteraction.BLOCK);
				}

			}
		}

	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, NRRBlockEntities.REACTOR_CORE, ReactorCoreBlockEntity::serverTick);
	}

}
