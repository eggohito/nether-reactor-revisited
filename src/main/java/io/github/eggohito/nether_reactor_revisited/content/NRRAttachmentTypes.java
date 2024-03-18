package io.github.eggohito.nether_reactor_revisited.content;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import io.github.eggohito.nether_reactor_revisited.attachment.BlockUsageContextAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class NRRAttachmentTypes {

    public static final AttachmentType<BlockUsageContextAttachment> BLOCK_USAGE_CONTEXT = AttachmentRegistry.createDefaulted(
        NetherReactorRevisited.id("block_usage_context"),
        BlockUsageContextAttachment::new
    );

    public static void registerAll() {

    }

}
