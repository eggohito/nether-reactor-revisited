package io.github.eggohito.nether_reactor_revisited.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.nether_reactor_revisited.block.entity.ReactorCoreBlockEntity;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockEntities;
import io.github.eggohito.nether_reactor_revisited.event.BlockInteractionPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.core.CoreState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.enchantment.*;
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

public class ReactorCoreBlock extends BaseEntityBlock {

	public static final MapCodec<ReactorCoreBlock> CODEC = simpleCodec(ReactorCoreBlock::new);
	public static final Property<CoreState> STATE = EnumProperty.create("state", CoreState.class);

	public ReactorCoreBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(STATE, CoreState.NORMAL));
	}

	@Override
	protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

		if (level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore) {

			var result = state.getValue(STATE).triggerEvent(level, pos, state, reactorCore, player, hand, hitResult, BlockInteractionPhase.WITH_ITEM);

			if (result != null) {
				return result;
			}

		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;

	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

		if (level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore) {

			var result = state.getValue(STATE).triggerEvent(level, pos, state, reactorCore, player, InteractionHand.MAIN_HAND, hitResult, BlockInteractionPhase.WITHOUT_ITEM);

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
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {

		if (level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore) {

			TypedEntityData<BlockEntityType<?>> blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);

			if (blockEntityData != null && !blockEntityData.contains("in_phase")) {
				reactorCore.copyState(state.getValue(STATE));
				reactorCore.setChanged();
			}

		}

	}

	@Override
	protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {

		if (state.getValue(STATE) != CoreState.ACTIVATED || !(level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity reactorCore) || reactorCore.getStatus().phase() != ReactorPhase.UNSTABLE) {
			return;
		}

		Registry<Enchantment> enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		Holder<Enchantment> silkTouch = enchantments.get(Enchantments.SILK_TOUCH).orElse(null);

		if (silkTouch == null || tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(silkTouch) <= 0) {
			level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, Level.ExplosionInteraction.BLOCK);
		}

	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, NRRBlockEntities.REACTOR_CORE, ReactorCoreBlockEntity::serverTick);
	}

}
