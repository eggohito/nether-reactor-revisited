package io.github.eggohito.nether_reactor_revisited.item;

import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import io.github.eggohito.nether_reactor_revisited.reactor.core.CoreState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ReactorCoreItem extends BlockItem {

	public ReactorCoreItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {

		if (!(itemStack.getItem() instanceof ReactorCoreItem) || !itemStack.has(DataComponents.BLOCK_STATE) || !display.shows(DataComponents.BLOCK_STATE)) {
			return;
		}

		BlockItemStateProperties stateProperties = Objects.requireNonNull(itemStack.get(DataComponents.BLOCK_STATE));
		CoreState coreState = stateProperties.get(ReactorCoreBlock.STATE);

		if (coreState == null) {
			return;
		}

		Component stateComponent = switch (coreState) {
			case NORMAL ->
				Component.translatable("states.nether-reactor-revisited.reactor_core.normal").withStyle(style -> style.withColor(ChatFormatting.GREEN));
			case ACTIVATED ->
				Component.translatable("states.nether-reactor-revisited.reactor_core.activated").withStyle(style -> style.withColor(ChatFormatting.RED));
			case DEACTIVATED ->
				Component.translatable("states.nether-reactor-revisited.reactor_core.deactivated").withStyle(style -> style.withColor(ChatFormatting.DARK_PURPLE));
		};

		builder.accept(Component.translatable("item.nether-reactor-revisited.reactor_core.state", stateComponent).withStyle(style -> style.withColor(ChatFormatting.GRAY)));

	}

}
