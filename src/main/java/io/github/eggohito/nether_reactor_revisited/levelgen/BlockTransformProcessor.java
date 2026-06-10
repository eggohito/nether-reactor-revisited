package io.github.eggohito.nether_reactor_revisited.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.nether_reactor_revisited.content.NRRProcessorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class BlockTransformProcessor extends StructureProcessor {

	public static final MapCodec<BlockTransformProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("transformable_blocks").forGetter(p -> p.transformableBlocks),
		BlockState.CODEC.fieldOf("replacement_state").forGetter(p -> p.replacementState),
		Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(p -> p.chance)
	).apply(instance, BlockTransformProcessor::new));

	private final Optional<HolderSet<Block>> transformableBlocks;
	private final BlockState replacementState;
	private final float chance;

	private BlockTransformProcessor(Optional<HolderSet<Block>> transformableBlocks, BlockState replacementState, float chance) {
		this.transformableBlocks = transformableBlocks;
		this.replacementState = replacementState;
		this.chance = Mth.clamp(chance, 0.0F, 1.0F);
	}

	public BlockTransformProcessor(HolderSet<Block> transformableBlocks, BlockState replacementState, float chance) {
		this(Optional.of(transformableBlocks), replacementState, chance);
	}

	public BlockTransformProcessor(BlockState replacementState, float chance) {
		this(Optional.empty(), replacementState, chance);
	}

	public BlockTransformProcessor(float chance) {
		this(Optional.empty(), Blocks.AIR.defaultBlockState(), chance);
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return NRRProcessorTypes.BLOCK_TRANSFORM;
	}

	@Override
	public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings) {
		RandomSource random = settings.getRandom(processedBlockInfo.pos());
		return (this.transformableBlocks.isEmpty() || originalBlockInfo.state().is(this.transformableBlocks.get())) && !(random.nextFloat() <= this.chance)
			? new StructureTemplate.StructureBlockInfo(processedBlockInfo.pos(), this.replacementState, processedBlockInfo.nbt())
			: processedBlockInfo;
	}

}
