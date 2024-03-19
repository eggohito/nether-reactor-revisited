package io.github.eggohito.nether_reactor_revisited.util;

import io.github.eggohito.nether_reactor_revisited.NetherReactorRevisited;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public enum ReactorForgeResult {

    FAIL("fail", Formatting.RED),
    SUCCESS("success", Formatting.GREEN),
    INAPPLICABLE(FAIL + "/inapplicable", Formatting.RED),
    MAX_USE_REACHED(FAIL + "/max_use_reached", Formatting.RED);

    private final String name;
    private final String translationKey;

    private final Formatting formatting;

    ReactorForgeResult(String name, Formatting formatting) {
        this.name = name;
        this.translationKey = Util.createTranslationKey("actions", NetherReactorRevisited.id("forge_item/" + name));
        this.formatting = formatting;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    @Override
    public String toString() {
        return name;
    }

}
