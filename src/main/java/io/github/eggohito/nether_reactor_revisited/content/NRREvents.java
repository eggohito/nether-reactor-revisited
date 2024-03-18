package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.attachment.BlockUsageContextAttachment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class NRREvents {

    public static void registerAll() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {

            BlockUsageContextAttachment blockContextAttachment = world.getAttached(NRRAttachmentTypes.BLOCK_USAGE_CONTEXT);

            if (blockContextAttachment != null && !blockContextAttachment.isEmpty()) {
                blockContextAttachment.clear();
            }

        });
    }

}
