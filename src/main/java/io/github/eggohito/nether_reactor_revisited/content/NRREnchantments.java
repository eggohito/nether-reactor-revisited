package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.enchantment.HellLighterEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Supplier;

public class NRREnchantments {

    public static final HellLighterEnchantment HELL_LIGHTER = register("hell_lighter", HellLighterEnchantment::new);

    public static boolean hasHellLighter(ItemStack stack) {
        return EnchantmentHelper.getLevel(HELL_LIGHTER, stack) > 0;
    }

    public static boolean canApplyHellLighter(ItemStack stack) {
        return canApply(HELL_LIGHTER, stack);
    }

    public static boolean canApply(Enchantment enchantment, ItemStack stack) {
        return enchantment.isAcceptableItem(stack)
            && EnchantmentHelper.isCompatible(EnchantmentHelper.get(stack).keySet(), enchantment);
    }

    public static void registerAll() {

    }

    public static <T extends Enchantment> T register(String name, Supplier<T> enchantmentSupplier) {
        return Registry.register(Registries.ENCHANTMENT, NetherReactorRevisited.id(name), enchantmentSupplier.get());
    }

}
