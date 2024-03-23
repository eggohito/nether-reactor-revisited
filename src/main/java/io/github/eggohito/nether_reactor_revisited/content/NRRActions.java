package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.block.entity.NetherReactorBlockEntity;
import io.github.eggohito.nether_reactor_revisited.block.pattern.ReactorBlockPattern;
import io.github.eggohito.nether_reactor_revisited.reactor.ActivityPhase;
import io.github.eggohito.nether_reactor_revisited.reactor.ForgeResult;
import io.github.eggohito.nether_reactor_revisited.reactor.TriggerAction;
import io.github.eggohito.nether_reactor_revisited.reactor.TriggerType;
import io.github.eggohito.nether_reactor_revisited.util.*;
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

    public static final TriggerAction IS_REACTOR_LEVEL_WITH_PLAYER = (triggerType, netherReactor, state, world, pos, player, hand, hitResult) -> {

        if (player.getBlockY() == pos.down().getY()) {
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text
            .translatable(triggerType.getBaseTranslationKey() + ".fail.different_y_level")
            .setStyle(TriggerAction.ERROR_STYLE), true);
        return ActionResult.FAIL;

    };

    public static final TriggerAction ANOTHER_REACTOR_NEARBY = (triggerType, netherReactor, state, world, pos, player, hand, hitResult) -> {

        long nearbyReactorCores = 0;
        for (BlockPos nearbyPos : BlockPos.iterateOutwards(pos, 32, 32, 32)) {

            if (!(world.getBlockState(nearbyPos).getBlock() instanceof NetherReactorBlock) || nearbyReactorCores++ == 0) {
                continue;
            }

            player.sendMessage(Text
                .translatable(triggerType.getBaseTranslationKey() + ".fail.nearby_cores")
                .setStyle(TriggerAction.ERROR_STYLE), true);
            return ActionResult.FAIL;

        }

        return ActionResult.SUCCESS;

    };

    public static final TriggerAction HAS_REACTOR_STRUCTURE = new TriggerAction() {

        @Override
        public ActionResult accept(TriggerType triggerType, NetherReactorBlockEntity netherReactor, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {

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
                .translatable(triggerType.getBaseTranslationKey() + ".fail.side_mismatch", mismatchSideText)
                .styled(style -> style.withColor(Formatting.RED)), true);
            return ActionResult.FAIL;

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

    public static final TriggerAction PLAYERS_TOO_FAR_AWAY = (triggerType, netherReactor, state, world, pos, player, hand, hitResult) -> {

        Vec3d centerPos = pos.toCenterPos();
        int farAwayPlayers = world
            .getEntitiesByClass(PlayerEntity.class, new Box(pos).expand(12), _player -> _player.getPos().distanceTo(centerPos) > 6)
            .size();

        if (farAwayPlayers == 0) {
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text
            .translatable(triggerType.getBaseTranslationKey() + ".fail.players_too_far_away", farAwayPlayers)
            .setStyle(TriggerAction.ERROR_STYLE), true);
        return ActionResult.FAIL;

    };

    public static final TriggerAction CAN_REACTIVATE = (triggerType, netherReactor, state, world, pos, player, hand, hitResult) -> {

        if (triggerType == TriggerType.NORMAL) {
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
            .translatable(triggerType.getBaseTranslationKey() + ".fail.unmet_requirements")
            .setStyle(TriggerAction.ERROR_STYLE), true);

        return ActionResult.FAIL;

    };

    public static final TriggerAction FORGE_HELL_LIGHTER = (triggerType, netherReactor, state, world, pos, player, hand, hitResult) -> {

        if (triggerType != TriggerType.DEACTIVATED) {
            return ActionResult.PASS;
        }

        ItemStack stackInHand = player.getStackInHand(hand);
        ForgeResult result = netherReactor.forgeItem(stackInHand, NRREnchantments::canApplyHellLighter);

        if (result == ForgeResult.INAPPLICABLE) {
            return ActionResult.PASS;
        }

        player.sendMessage(Text
            .translatable(result.getTranslationKey(), stackInHand.getName())
            .formatted(result.getFormatting()), true);

        return ActionResult.FAIL;

    };

    public static final TriggerAction QUERY_ACTIVE_REACTOR_TIME = (triggerType, netherReactor, state, world, pos, player, hand, hitResult) -> {

        ActivityPhase activityPhase = netherReactor.getActivityPhase();
        if (activityPhase == ActivityPhase.NONE) {
            return ActionResult.FAIL;
        }

        String suffix = activityPhase == ActivityPhase.UNSTABLE
            ? ".query.remaining_unstable_time"
            : ".query.elapsed_active_time";

        player.sendMessage(Text
            .translatable(triggerType.getBaseTranslationKey() + suffix, netherReactor.getActiveTicks() / 20)
            .formatted(Formatting.YELLOW), true);

        return ActionResult.PASS;

    };

}
