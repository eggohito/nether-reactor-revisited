package io.github.eggohito.nether_reactor_revisited.block;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.state.property.TriStateProperty;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.World;

public class NetherReactorBlock extends Block implements PolymerTexturedBlock {

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

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

        if (hand == Hand.OFF_HAND) {
            return ActionResult.PASS;
        }

        MinecraftServer server = world.getServer();
        Text notificationText;

        return switch (state.get(ACTIVATED)) {
            case DEFAULT -> {

                if (server != null) {
                    notificationText = Text
                        .translatable("actions.nether-reactor-revisited.activate.success", player.getName())
                        .styled(style -> style.withColor(Formatting.GREEN));
                    server.getPlayerManager().broadcast(notificationText, false);
                }

                world.setBlockState(pos, state.with(ACTIVATED, TriState.TRUE));
                yield ActionResult.SUCCESS;

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

                if (server != null) {
                    notificationText = Text
                        .translatable("actions.nether-reactor-revisited.reactivate.success", player.getName())
                        .styled(style -> style.withColor(Formatting.GREEN));
                    server.getPlayerManager().broadcast(notificationText, false);
                }

                stackInHand.decrement(1);
                world.setBlockState(pos, state.with(ACTIVATED, TriState.TRUE));

                yield ActionResult.SUCCESS;

            }
        };
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return switch (state.get(ACTIVATED)) {
            case DEFAULT ->
                Blocks.DIAMOND_BLOCK;
            case TRUE ->
                Blocks.REDSTONE_BLOCK;
            case FALSE ->
                Blocks.OBSIDIAN;
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

        ACTIVATED = new TriStateProperty("activated");

        DEFAULT_STATE = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(NetherReactorRevisited.id("block/reactor_core/default")));
        ACTIVATED_STATE = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(NetherReactorRevisited.id("block/reactor_core/activated")));
        DEACTIVATED_STATE = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(NetherReactorRevisited.id("block/reactor_core/deactivated")));

    }

}
