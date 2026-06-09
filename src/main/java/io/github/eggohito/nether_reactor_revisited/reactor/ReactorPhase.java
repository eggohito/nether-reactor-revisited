package io.github.eggohito.nether_reactor_revisited.reactor;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public enum ReactorPhase implements StringRepresentable {

	NONE("none", ReactorPatterns.NORMAL),
	STABLE("stable", ReactorPatterns.ACTIVATED),
	UNSTABLE("unstable", ReactorPatterns.ACTIVATED),
	ACTIVATING("activating", ReactorPatterns.ACTIVATING),
	DEACTIVATING("deactivating", ReactorPatterns.DEACTIVATING);

	public static final Codec<ReactorPhase> CODEC = StringRepresentable.fromEnum(ReactorPhase::values);

	final String name;
	final BlockPattern pattern;

	ReactorPhase(String name, BlockPattern pattern) {
		this.name = name;
		this.pattern = pattern;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public BlockPattern getPattern() {
		return pattern;
	}

}
