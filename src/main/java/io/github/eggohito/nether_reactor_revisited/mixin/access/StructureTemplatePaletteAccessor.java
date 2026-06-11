package io.github.eggohito.nether_reactor_revisited.mixin.access;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(StructureTemplate.Palette.class)
public interface StructureTemplatePaletteAccessor {

	@Invoker("<init>")
	static StructureTemplate.Palette newPalette(List<StructureTemplate.StructureBlockInfo> blocks) {
		throw new AssertionError();
	}

}
