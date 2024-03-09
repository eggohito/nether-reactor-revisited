package io.github.eggohito.nether_reactor_revisited.state.property;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.state.property.Property;

import java.util.*;

public class TriStateProperty extends Property<TriState> {

    private final Set<TriState> values = ImmutableSet.copyOf(TriState.values());

    public TriStateProperty(String name) {
        super(name, TriState.class);
    }

    @Override
    public Collection<TriState> getValues() {
        return this.values;
    }

    @Override
    public String name(TriState value) {
        return value.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public Optional<TriState> parse(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "default" ->
                Optional.of(TriState.DEFAULT);
            case "true" ->
                Optional.of(TriState.TRUE);
            case "false" ->
                Optional.of(TriState.FALSE);
            default ->
                Optional.empty();
        };
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }

}
