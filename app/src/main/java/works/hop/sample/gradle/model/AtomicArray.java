package works.hop.sample.gradle.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AtomicArray extends LinkedList<Atomic> implements Atomic{

    private String basePath;
    private Atomic parent;
    private final List<AtomicVisitor> decorators;

    public AtomicArray(Atomic parent) {
        this(parent, ".", new LinkedList<>());
    }

    public AtomicArray(Atomic parent, String basePath) {
        this(parent, basePath, new LinkedList<>());
    }

    public AtomicArray(Atomic parent, String basePath, List<AtomicVisitor> decorators) {
        this.parent = parent;
        this.basePath = basePath;
        this.decorators = decorators;
    }

    public void addDecorator(AtomicVisitor decorator){
        this.decorators.add(decorator);
    }

    @Override
    public String path() {
        String prefix = Optional.ofNullable(parent()).map(Atomic::path).orElse("");
        String suffix = Objects.requireNonNullElse(this.basePath, "");
        return prefix.length() > 1? String.format("%s.%s[]", prefix, suffix) : suffix;
    }

    @Override
    public Atomic parent() {
        return this.parent;
    }

    @Override
    public boolean add(Atomic value) {
        decorators.forEach(dec -> dec.visit(this, value));
        return super.add(value);
    }

    @Override
    public Atomic get(int index) {
        decorators.forEach(dec -> dec.visit(this, index));
        return super.get(index);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isArray(int index) {
        return isArray() && get(index).isArray();
    }
}
