package works.hop.sample.gradle.model;

import javax.swing.text.html.Option;
import java.util.*;

public class AtomicObject extends HashMap<String, Atomic> implements Atomic {

    private String basePath;
    private final Atomic parent;
    private final List<AtomicVisitor> decorators;

    public AtomicObject(Atomic parent) {
        this(parent, ".", new LinkedList<>());
    }

    public AtomicObject(Atomic parent, String basePath) {
        this(parent, basePath,  new LinkedList<>());
    }

    public AtomicObject(Atomic parent, String basePath, List<AtomicVisitor> decorators) {
        this.parent = parent;
        this.basePath = basePath;
        this.decorators = decorators;
    }

    public void addDecorator(AtomicVisitor decorator) {
        this.decorators.add(decorator);
    }

    @Override
    public String path() {
        String prefix = Optional.ofNullable(parent()).map(Atomic::path).orElse("");
        String suffix = Objects.requireNonNullElse(this.basePath, "");
        return prefix.length() > 1? String.format("%s.%s", prefix, suffix) : suffix;
    }

    @Override
    public Atomic parent() {
        return this.parent;
    }

    @Override
    public Atomic put(String key, Atomic value) {
        decorators.forEach(dec -> dec.visit(this, key, value));
        return super.put(key, value);
    }

    @Override
    public Atomic get(Object key) {
        decorators.forEach(dec -> dec.visit(this, key));
        return super.get(key);
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean isObject(String key) {
        return isObject() && get(key).isObject();
    }
}
