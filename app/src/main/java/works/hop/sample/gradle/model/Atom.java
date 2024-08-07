package works.hop.sample.gradle.model;

import java.util.Optional;

public class Atom implements Atomic {
    private final Atomic parent;
    private Comparable<?> value;

    public Atom(Atomic parent) {
        this.parent = parent;
    }

    public Atom(Atomic parent, Comparable<?> value) {
        this.parent = parent;
        this.value = value;
    }

    @Override
    public String path() {
        return Optional.ofNullable(parent()).map(Atomic::path).orElse("");
    }

    @Override
    public Atomic parent() {
        return this.parent;
    }

    @Override
    public <T extends Comparable<T>> T getValue(String field) {
        return (T) this.value;
    }

    @Override
    public <T extends Comparable<T>> void setValue(String field, T value) {
        this.value = value;
    }
}
