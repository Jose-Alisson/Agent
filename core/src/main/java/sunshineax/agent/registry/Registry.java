package sunshineax.agent.registry;

public interface Registry<K,V> {

    void register(K key, V value);
    void unregister(K key);
}
