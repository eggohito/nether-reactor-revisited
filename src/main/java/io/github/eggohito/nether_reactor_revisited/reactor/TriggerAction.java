package io.github.eggohito.nether_reactor_revisited.reactor;

import io.github.eggohito.nether_reactor_revisited.block.entity.NetherReactorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface TriggerAction {

    Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
    Style SUCCESS_STYLE = Style.EMPTY.withColor(Formatting.GREEN);

    ActionResult accept(TriggerType triggerType, NetherReactorBlockEntity netherReactor, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult);

}
