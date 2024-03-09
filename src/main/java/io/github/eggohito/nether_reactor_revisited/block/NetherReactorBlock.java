package io.github.eggohito.nether_reactor_revisited.block;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.pattern.ReactorBlockPattern;
import io.github.eggohito.nether_reactor_revisited.content.NRRBlockTags;
import io.github.eggohito.nether_reactor_revisited.state.property.TriStateProperty;
import io.github.eggohito.nether_reactor_revisited.util.DirectionUtil;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NetherReactorBlock extends Block implements PolymerTexturedBlock {

    public static final ReactorBlockPattern DEFAULT_STRUCTURE_PATTERN;
    public static final ReactorBlockPattern ACTIVATED_STRUCTURE_PATTERN;
    public static final ReactorBlockPattern DEACTIVATED_STRUCTURE_PATTERN;

    public static final TriStateProperty ACTIVATED;

    public static final BlockState DEFAULT_STATE;
    public static final BlockState ACTIVATED_STATE;
    public static final BlockState DEACTIVATED_STATE;

    public NetherReactorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
            .with(ACTIVATED, TriState.DEFAULT));
    }

    public NetherReactorBlock() {
        this(Settings.create()
            .sounds(BlockSoundGroup.METAL)
            .hardness(3.5f)
            .requiresTool());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    //  TODO: Simplify and segregate stuff from this method
    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

        if (hand == Hand.OFF_HAND) {
            return ActionResult.PASS;
        }

        MinecraftServer server = world.getServer();
        Text notificationText;

        if (world.isClient || server == null) {
            return ActionResult.CONSUME_PARTIAL;
        }

        Direction direction = DirectionUtil
            .getFacingHorizontal(player.getRotationVector())
            .getOpposite();
        BlockPos frontTopLeftPos = pos.offset(direction)
            .add(-direction.getOffsetZ(), 1, direction.getOffsetX());

        return switch (state.get(ACTIVATED)) {
            case DEFAULT -> {

                ReactorBlockPattern.Result patternResult = DEFAULT_STRUCTURE_PATTERN.testTransformSafely(world, frontTopLeftPos, direction.getOpposite(), Direction.UP);
                if (patternResult.fail()) {

                    BlockPos missingPos = patternResult.getMismatches().get(0);
                    Direction missingSide = DirectionUtil.getNullableDirectionFromPos(pos, missingPos);

                    if (missingSide == null) {
                        notificationText = Text
                            .translatable("actions.nether-reactor-revisited.activate.fail")
                            .styled(style -> style.withColor(Formatting.RED));
                    }

                    else {

                        Text sideText = switch (missingSide) {
                            case UP ->
                                Text.literal("upper");
                            case DOWN ->
                                Text.literal("lower");
                            case EAST ->
                                Text.literal("east");
                            case WEST ->
                                Text.literal("west");
                            case NORTH ->
                                Text.literal("north");
                            case SOUTH ->
                                Text.literal("south");
                        };

                        notificationText = Text
                            .translatable("actions.nether-reactor-revisited.activate.fail.incorrect_side", sideText)
                            .styled(style -> style.withColor(Formatting.RED));

                    }

                    player.sendMessage(notificationText, true);
                    yield ActionResult.CONSUME_PARTIAL;

                }

                else {

                    notificationText = Text
                        .translatable("actions.nether-reactor-revisited.activate.success", player.getName())
                        .styled(style -> style.withColor(Formatting.GREEN));

                    server.getPlayerManager().broadcast(notificationText, false);
                    world.setBlockState(pos, state.with(ACTIVATED, TriState.TRUE));

                    yield ActionResult.SUCCESS;

                }

            }
            case TRUE -> {
                world.setBlockState(pos, state.with(ACTIVATED, TriState.FALSE));
                yield ActionResult.SUCCESS;
            }
            case FALSE -> {

                ItemStack stackInHand = player.getStackInHand(hand);
                if (!stackInHand.isIn(ConventionalItemTags.DIAMONDS)) {

                    notificationText = Text
                        .translatable("actions.nether-reactor-revisited.reactivate.fail")
                        .styled(style -> style.withColor(Formatting.RED));

                    player.sendMessage(notificationText, true);
                    yield ActionResult.CONSUME_PARTIAL;

                }

                ReactorBlockPattern.Result patternResult = DEACTIVATED_STRUCTURE_PATTERN.testTransformSafely(world, frontTopLeftPos, direction.getOpposite(), Direction.UP);
                if (patternResult.fail()) {

                    BlockPos missingPos = patternResult.getMismatches().get(0);
                    Direction missingSide = DirectionUtil.getNullableDirectionFromPos(pos, missingPos);

                    if (missingSide == null) {
                        notificationText = Text
                            .translatable("actions.nether-reactor-revisited.reactivate.fail")
                            .styled(style -> style.withColor(Formatting.RED));
                    }

                    else {

                        Text sideText = switch (missingSide) {
                            case UP ->
                                Text.literal("upper");
                            case DOWN ->
                                Text.literal("lower");
                            case EAST ->
                                Text.literal("east");
                            case WEST ->
                                Text.literal("west");
                            case NORTH ->
                                Text.literal("north");
                            case SOUTH ->
                                Text.literal("south");
                        };

                        notificationText = Text
                            .translatable("actions.nether-reactor-revisited.reactivate.fail.incorrect_side", sideText)
                            .styled(style -> style.withColor(Formatting.RED));

                    }

                    player.sendMessage(notificationText, true);
                    yield ActionResult.CONSUME_PARTIAL;

                }

                else {

                    notificationText = Text
                        .translatable("actions.nether-reactor-revisited.reactivate.success", player.getName())
                        .styled(style -> style.withColor(Formatting.GREEN));
                    server.getPlayerManager().broadcast(notificationText, false);

                    stackInHand.decrement(1);
                    world.setBlockState(pos, state.with(ACTIVATED, TriState.TRUE));

                    yield ActionResult.SUCCESS;

                }

            }
        };
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return switch (state.get(ACTIVATED)) {
            case DEFAULT ->
                Blocks.LIGHT_BLUE_SHULKER_BOX;
            case TRUE ->
                Blocks.RED_SHULKER_BOX;
            case FALSE ->
                Blocks.BLACK_SHULKER_BOX;
        };
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, ServerPlayerEntity player) {

        if (!PolymerResourcePackUtils.hasMainPack(player) || PolymerBlockResourceUtils.getBlocksLeft(BlockModelType.FULL_BLOCK) == 0) {
            return PolymerTexturedBlock.super.getPolymerBlockState(state, player);
        }

        return switch (state.get(ACTIVATED)) {
            case DEFAULT ->
                DEFAULT_STATE;
            case TRUE ->
                ACTIVATED_STATE;
            case FALSE ->
                DEACTIVATED_STATE;
        };

    }

    static {

        DEFAULT_STRUCTURE_PATTERN = ReactorBlockPattern.Builder.start()
            .aisle("~#~", "#~#", "@#@")
            .aisle("###", "~ ~", "###")
            .aisle("~#~", "#~#", "@#@")
            .where('#', CachedBlockPosition.matchesBlockState(state -> state.isIn(NRRBlockTags.DEFAULT_PATTERN_BLOCKS)))
            .where('@', CachedBlockPosition.matchesBlockState(state -> state.isIn(NRRBlockTags.POWER_BLOCKS)))
            .where('~', CachedBlockPosition.matchesBlockState(AbstractBlockState::isAir))
            .build();
        ACTIVATED_STRUCTURE_PATTERN = ReactorBlockPattern.Builder.start()
            .aisle(" # ", "#~#", " # ")
            .aisle("###", "~ ~", "###")
            .aisle(" # ", "#~#", " # ")
            .where('#', CachedBlockPosition.matchesBlockState(state -> state.isIn(NRRBlockTags.ACTIVATED_PATTERN_BLOCKS)))
            .build();
        DEACTIVATED_STRUCTURE_PATTERN = ReactorBlockPattern.Builder.start()
            .aisle("~#~", "#~#", "@#@")
            .aisle("###", "~ ~", "###")
            .aisle("~#~", "#~#", "@#@")
            .where('#', CachedBlockPosition.matchesBlockState(state -> state.isIn(NRRBlockTags.DEACTIVATED_PATTERN_BLOCKS)))
            .where('@', CachedBlockPosition.matchesBlockState(state -> state.isIn(NRRBlockTags.POWER_BLOCKS)))
            .where('~', CachedBlockPosition.matchesBlockState(AbstractBlockState::isAir))
            .build();

        ACTIVATED = new TriStateProperty("activated");

        DEFAULT_STATE = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(NetherReactorRevisited.id("block/reactor_core/default")));
        ACTIVATED_STATE = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(NetherReactorRevisited.id("block/reactor_core/activated")));
        DEACTIVATED_STATE = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(NetherReactorRevisited.id("block/reactor_core/deactivated")));

    }

}
