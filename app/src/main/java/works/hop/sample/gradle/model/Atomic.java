package works.hop.sample.gradle.model;

public interface Atomic {

    default <T extends Comparable<T>> T getValue(String field){
        // get from Atom
        return null;
    }

    default <T extends Comparable<T>> void setValue(String field, T value){
        // set value in Atom
    }

    default Atomic parent(){
        return null;
    }

    default String path(){
        return null;
    }

    default int index(){
        return -1;
    }

    default boolean isArray(){
        return false;
    }

    default boolean isArray(int index) {
        return false;
    }

    default boolean isObject(){
        return false;
    }

    default boolean isObject(String key) {
        return false;
    }

    static Atomic parse(String jsonFile){
        return null;
    }
}
