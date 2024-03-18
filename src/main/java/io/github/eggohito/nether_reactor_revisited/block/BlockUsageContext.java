package io.github.eggohito.nether_reactor_revisited.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record BlockUsageContext(World world, ItemStack stack, @Nullable PlayerEntity user) {

    public static BlockUsageContext fromItemContext(ItemUsageContext itemContext) {
        return new BlockUsageContext(itemContext.getWorld(), itemContext.getStack(), itemContext.getPlayer());
    }

}
