package io.github.eggohito.nether_reactor_revisited.reactor.core;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum CoreState implements StringRepresentable {

	NORMAL("normal"),
	ACTIVATED("activated"),
	DEACTIVATED("deactivated"),;

	final String name;

	CoreState(String name) {
		this.name = name;
	}

	@Override
	public @NonNull String getSerializedName() {
		return name;
	}

}
