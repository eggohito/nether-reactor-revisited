package io.github.eggohito.nether_reactor_revisited.mixin.impl.core_tooltip;

import io.github.eggohito.nether_reactor_revisited.block.ReactorCoreBlock;
import io.github.eggohito.nether_reactor_revisited.reactor.ReactorPhase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BlockItemStateProperties.class)
public abstract class BlockItemStatePropertiesMixin {

	@Shadow
	@Nullable
	public abstract <T extends Comparable<T>> T get(Property<T> property);

	@Inject(method = "addToTooltip", at = @At("TAIL"))
	void appendCoreStateTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components, CallbackInfo ci) {

		ReactorPhase reactorPhase = this.get(ReactorCoreBlock.PHASE);

		if (reactorPhase != null) {
			consumer.accept(Component.translatable("item.nether-reactor-revisited.reactor_core.phase.tooltip", reactorPhase.getTooltipComponent().copy().withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.GRAY));
		}

	}

}
