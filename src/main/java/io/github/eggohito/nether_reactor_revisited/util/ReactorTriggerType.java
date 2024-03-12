package io.github.eggohito.nether_reactor_revisited.util;

import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.block.entity.NetherReactorBlockEntity;
import io.github.eggohito.nether_reactor_revisited.block.pattern.ReactorBlockPattern;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Set;

public enum ReactorTriggerType {

    ACTIVATION(NetherReactorBlock.DEFAULT_STRUCTURE_PATTERN, "nether-reactor-revisited.activate"),
    REACTIVATION(NetherReactorBlock.DEACTIVATED_STRUCTURE_PATTERN, "nether-reactor-revisited.reactivate");

    final ReactorBlockPattern structurePattern;
    final String baseTranslationKey;

    ReactorTriggerType(ReactorBlockPattern structurePattern, String baseTranslationKey) {
        this.structurePattern = structurePattern;
        this.baseTranslationKey = baseTranslationKey;
    }

    public ActionResult trigger(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

        MinecraftServer server = world.getServer();
        Text notificationText;

        if (world.isClient || server == null || !(world.getBlockEntity(pos) instanceof NetherReactorBlockEntity netherReactor)) {
            return ActionResult.CONSUME_PARTIAL;
        }

        Direction facingDirection = player.getHorizontalFacing();
        Direction oppositeFacingDirection = facingDirection
            .getOpposite();
        BlockPos frontTopLeftPos = pos
            .offset(oppositeFacingDirection)
            .add(-oppositeFacingDirection.getOffsetZ(), 1, oppositeFacingDirection.getOffsetX());

        ReactorBlockPattern.Result patternResult = structurePattern.testTransformSafely(player.getWorld(), frontTopLeftPos, facingDirection, Direction.UP);
        if (!patternResult.fail()) {

            notificationText = Text
                .translatable("actions." + baseTranslationKey + ".success", player.getName())
                .styled(style -> style.withColor(Formatting.GREEN));
            server.getPlayerManager().broadcast(notificationText, false);

            netherReactor.activate();
            world.setBlockState(pos, state.withIfExists(NetherReactorBlock.ACTIVATED, TriState.TRUE));

            return ActionResult.SUCCESS;

        }

        MutableText mismatchSideText = Text.empty()
            .styled(style -> style.withColor(Formatting.YELLOW));
        Text separator = Text.empty();

        int maxIterations = patternResult.getMismatches().size();
        int iterations = 0;

        for (BlockPos mismatchPos : patternResult.getMismatches()) {

            ++iterations;
            Text subMismatchSideText = getMismatchSideText(pos, mismatchPos);

            mismatchSideText
                .append(separator)
                .append(subMismatchSideText);
            separator = (iterations < maxIterations - 1
                ? Text.literal(", ")
                : Text.literal(" and "))
                    .styled(style -> style.withColor(Formatting.RED));

        }

        notificationText = Text
            .translatable("actions." + baseTranslationKey + ".fail.side_mismatch", mismatchSideText)
            .styled(style -> style.withColor(Formatting.RED));

        player.sendMessage(notificationText, true);
        return ActionResult.CONSUME_PARTIAL;

    }

    private Text getMismatchSideText(BlockPos originPos, BlockPos mismatchPos) {

        Set<Direction> mismatchSides = DirectionUtil.getDirectionsFromPos(originPos, mismatchPos);

        MutableText mismatchSideText = Text.empty()
            .styled(style -> style.withColor(Formatting.YELLOW));
        Text separator = Text.empty();

        int maxIterations = mismatchSides.size();
        int iterations = 0;

        for (Direction mismatchSide : mismatchSides) {

            ++iterations;
            String mismatchSideString = switch (mismatchSide) {
                case UP ->
                    "top";
                case DOWN ->
                    "bottom";
                default ->
                    mismatchSide.toString();
            };

            mismatchSideText
                .append(separator)
                .append(Text.literal(mismatchSideString));

            separator = maxIterations == 2 || maxIterations == 3
                ? Text.literal("-")
                    .styled(style -> style.withColor(Formatting.YELLOW))
                : (iterations < maxIterations - 1
                    ? Text.literal(", ")
                    : Text.literal(" and "))
                        .styled(style -> style.withColor(Formatting.RED));

        }

        return mismatchSideText;

    }

}
