package works.hop.sample.gradle.model;

public interface AtomicVisitor {

    void visit(Atomic atomic, String key, Object value);

    void visit(Atomic atomic, Object comparable);
}
