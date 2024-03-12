package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.block.pattern.ReactorBlockPattern;
import io.github.eggohito.nether_reactor_revisited.util.DirectionUtil;
import io.github.eggohito.nether_reactor_revisited.util.ReactorTriggerAction;
import io.github.eggohito.nether_reactor_revisited.util.ReactorTriggerType;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public class NRRActions {

    public static final ReactorTriggerAction IS_REACTOR_LEVEL_WITH_PLAYER = (triggerType, state, world, pos, player, hand, hitResult) -> {

        if (player.getBlockY() == pos.down().getY()) {
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text
            .translatable("actions." + triggerType.getBaseTranslationKey() + ".fail.different_y_level")
            .setStyle(ReactorTriggerAction.ERROR_STYLE), true);
        return ActionResult.CONSUME_PARTIAL;

    };

    public static final ReactorTriggerAction ANOTHER_REACTOR_NEARBY = (triggerType, state, world, pos, player, hand, hitResult) -> {

        long nearbyReactorCores = 0;
        for (BlockPos nearbyPos : BlockPos.iterateOutwards(pos, 32, 32, 32)) {

            if (!(world.getBlockState(nearbyPos).getBlock() instanceof NetherReactorBlock) || nearbyReactorCores++ == 0) {
                continue;
            }

            player.sendMessage(Text
                .translatable("actions." + triggerType.getBaseTranslationKey() + ".fail.nearby_cores")
                .setStyle(ReactorTriggerAction.ERROR_STYLE), true);
            return ActionResult.CONSUME_PARTIAL;

        }

        return ActionResult.SUCCESS;

    };

    public static final ReactorTriggerAction HAS_REACTOR_STRUCTURE = new ReactorTriggerAction() {

        @Override
        public ActionResult accept(ReactorTriggerType triggerType, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

            Direction facingDirection = player.getHorizontalFacing();
            Direction oppositeFacingDirection = facingDirection.getOpposite();

            BlockPos frontTopLeftPos = pos
                .offset(oppositeFacingDirection)
                .add(-oppositeFacingDirection.getOffsetZ(), 1, oppositeFacingDirection.getOffsetX());
            ReactorBlockPattern.Result patternResult = triggerType
                .getStructurePattern()
                .testTransformSafely(world, frontTopLeftPos, facingDirection, Direction.UP);

            if (!patternResult.fail()) {
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

            player.sendMessage(Text
                .translatable("actions." + triggerType.getBaseTranslationKey() + ".fail.side_mismatch", mismatchSideText)
                .styled(style -> style.withColor(Formatting.RED)), true);
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

    };

    public static final ReactorTriggerAction PLAYERS_TOO_FAR_AWAY = (triggerType, state, world, pos, player, hand, hitResult) -> {

        Vec3d centerPos = pos.toCenterPos();
        int farAwayPlayers = world
            .getEntitiesByClass(PlayerEntity.class, new Box(pos).expand(12), _player -> _player.getPos().distanceTo(centerPos) > 6)
            .size();

        if (farAwayPlayers == 0) {
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text
            .translatable("actions." + triggerType.getBaseTranslationKey() + ".fail.players_too_far_away", farAwayPlayers)
            .setStyle(ReactorTriggerAction.ERROR_STYLE), true);
        return ActionResult.CONSUME_PARTIAL;

    };

    public static final ReactorTriggerAction CAN_REACTIVATE = (triggerType, state, world, pos, player, hand, hitResult) -> {

        if (triggerType == ReactorTriggerType.ACTIVATION) {
            return ActionResult.SUCCESS;
        }

        ItemStack stackInHand = player.getStackInHand(hand);
        if (stackInHand.isIn(ConventionalItemTags.DIAMONDS)) {

            if (!player.isCreative()) {
                stackInHand.decrement(1);
            }

            return ActionResult.SUCCESS;

        }

        player.sendMessage(Text
            .translatable("actions." + triggerType.getBaseTranslationKey() + ".fail.unmet_requirements")
            .setStyle(ReactorTriggerAction.ERROR_STYLE), true);

        return ActionResult.CONSUME_PARTIAL;

    };

}