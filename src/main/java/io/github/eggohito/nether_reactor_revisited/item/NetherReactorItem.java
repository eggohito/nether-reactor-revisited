package io.github.eggohito.nether_reactor_revisited.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.block.NetherReactorBlock;
import io.github.eggohito.nether_reactor_revisited.state.property.TriStateProperty;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class NetherReactorItem extends BlockItem implements PolymerItem {

    public static final PolymerModelData DEFAULT_STATE_MODEL;
    public static final PolymerModelData ACTIVATED_STATE_MODEL;
    public static final PolymerModelData DEACTIVATED_STATE_MODEL;

    public NetherReactorItem(NetherReactorBlock block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        super.appendTooltip(stack, world, tooltip, context);

        Optional<TriState> state = getReactorState(stack);
        if (state.isEmpty()) {
            return;
        }

        Text reactorState = switch (state.get()) {
            case DEFAULT -> Text
                .translatable("states.nether-reactor-revisited.reactor_core.default")
                .styled(style -> style.withColor(Formatting.GREEN));
            case TRUE -> Text
                .translatable("states.nether-reactor-revisited.reactor_core.activated")
                .styled(style -> style.withColor(Formatting.RED));
            case FALSE -> Text
                .translatable("states.nether-reactor-revisited.reactor_core.deactivated")
                .styled(style -> style.withColor(Formatting.DARK_PURPLE));
        };

        tooltip.add(Text.translatable("item.nether-reactor-revisited.reactor_core.state", reactorState)
            .styled(style -> style
                .withColor(Formatting.GRAY)
                .withItalic(true)));

    }

    @Override
    public Item getPolymerItem(ItemStack stack, @Nullable ServerPlayerEntity player) {
        return getReactorStateModel(stack)
            .map(PolymerModelData::item)
            .orElse(DEFAULT_STATE_MODEL.item());
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {

        ItemStack stack = PolymerItem.super.getPolymerItemStack(itemStack, context, player);
        if (!PolymerResourcePackUtils.hasMainPack(player)) {
            stack.addEnchantment(Enchantments.INFINITY, 0);
        }

        return stack;

    }

    @Override
    public int getPolymerCustomModelData(ItemStack stack, @Nullable ServerPlayerEntity player) {
        return getReactorStateModel(stack)
            .map(PolymerModelData::value)
            .orElse(DEFAULT_STATE_MODEL.value());
    }

    public static Optional<PolymerModelData> getReactorStateModel(ItemStack stack) {
        return getReactorState(stack).flatMap(triState -> switch (triState) {
            case DEFAULT ->
                Optional.of(DEFAULT_STATE_MODEL);
            case TRUE ->
                Optional.of(ACTIVATED_STATE_MODEL);
            case FALSE ->
                Optional.of(DEACTIVATED_STATE_MODEL);
        });
    }

    public static Optional<TriState> getReactorState(ItemStack stack) {

        if (!(stack.getItem() instanceof NetherReactorItem reactorItem)) {
            return Optional.empty();
        }

        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null) {
            return Optional.of(TriState.DEFAULT);
        }

        NbtCompound stateNbt = stackNbt.getCompound("BlockStateTag");
        StateManager<Block, BlockState> stateManager = reactorItem.getBlock().getStateManager();

        for (String stateKey : stateNbt.getKeys()) {

            Property<?> stateProperty = stateManager.getProperty(stateKey);
            if (!(stateProperty instanceof TriStateProperty triStateProperty)) {
                continue;
            }

            Optional<TriState> triState = triStateProperty.parse(stateNbt.getString(stateKey));
            if (triState.isPresent()) {
                return triState;
            }

        }

        return Optional.of(TriState.DEFAULT);

    }

    static {
        DEFAULT_STATE_MODEL = PolymerResourcePackUtils.requestModel(Items.LIGHT_BLUE_SHULKER_BOX,
            NetherReactorRevisited.id("item/reactor_core/default"));
        ACTIVATED_STATE_MODEL = PolymerResourcePackUtils.requestModel(Items.RED_SHULKER_BOX,
            NetherReactorRevisited.id("item/reactor_core/activated"));
        DEACTIVATED_STATE_MODEL = PolymerResourcePackUtils.requestModel(Items.BLACK_SHULKER_BOX,
            NetherReactorRevisited.id("item/reactor_core/deactivated"));
    }

}
