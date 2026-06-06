package io.github.eggohito.nether_reactor_revisited.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.nether_reactor_revisited.reactor.core.CoreState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class ReactorCoreBlock extends Block {

	public static final MapCodec<ReactorCoreBlock> CODEC = simpleCodec(ReactorCoreBlock::new);
	public static final Property<CoreState> STATE = EnumProperty.create("state", CoreState.class);

	public ReactorCoreBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(STATE, CoreState.NORMAL));
	}

	@Override
	protected @NonNull MapCodec<? extends Block> codec() {
		return CODEC;
	}

	@Override
	protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
		return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

}
