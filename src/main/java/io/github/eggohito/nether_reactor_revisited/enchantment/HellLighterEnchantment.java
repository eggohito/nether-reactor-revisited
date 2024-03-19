package io.github.eggohito.nether_reactor_revisited.enchantment;

import eu.pb4.polymer.core.api.other.PolymerEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;

public class HellLighterEnchantment extends Enchantment implements PolymerEnchantment {
    
    public HellLighterEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.UNBREAKING;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    //  TODO: Also check if the item stack is in an item tag to account for items
    //        that functions similarly to a flint and steel item, but doesn't extend it
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof FlintAndSteelItem;
    }

    @Override
    public boolean isCursed() {
        return true;
    }

}
