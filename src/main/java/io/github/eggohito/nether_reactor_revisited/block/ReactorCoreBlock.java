package io.github.eggohito.nether_reactor_revisited.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.nether_reactor_revisited.reactor.CoreState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}

}
