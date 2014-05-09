package map;

/**
 * @author Lars Mortensen
 */
public interface KeyObserver<K,V> {
  void dataChanged(K key, V newValue);
}
