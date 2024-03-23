package io.github.eggohito.nether_reactor_revisited.reactor;

import java.util.Locale;

public enum ActivityPhase {

    NONE,
    ACTIVATING,
    STABLE,
    UNSTABLE,
    DEACTIVATING;

    public static ActivityPhase fromString(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "activating" ->
                ACTIVATING;
            case "stable" ->
                STABLE;
            case "unstable" ->
                UNSTABLE;
            case "deactivating" ->
                DEACTIVATING;
            default ->
                NONE;
        };
    }

}
