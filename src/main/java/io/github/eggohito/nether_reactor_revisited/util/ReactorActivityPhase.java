package io.github.eggohito.nether_reactor_revisited.util;

import java.util.Locale;

public enum ReactorActivityPhase {

    NONE,
    ACTIVATING,
    STABLE,
    UNSTABLE,
    DEACTIVATING;

    public static ReactorActivityPhase fromString(String value) {
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
