package io.github.eggohito.nether_reactor_revisited.reactor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum CoreState implements StringRepresentable {

	NORMAL("normal", ChatFormatting.GREEN),
	ACTIVATED("activated", ChatFormatting.RED),
	DEACTIVATED("deactivated", ChatFormatting.DARK_PURPLE);

	final String name;
	final Component tooltipComponent;

	CoreState(String name, ChatFormatting formatting) {
		this.name = name;
		this.tooltipComponent = Component.translatable("nether-reactor-revisited.reactor_core.state." + name).withStyle(formatting);
	}

	@Override
	public @NonNull String getSerializedName() {
		return name;
	}

	public Component getTooltipComponent() {
		return tooltipComponent;
	}

}
