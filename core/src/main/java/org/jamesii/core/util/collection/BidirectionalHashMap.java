/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Class {@link BidirectionalHashMap} provides a {@link IBidirectionalMap}
 * implementation based on {@link HashMap}s. Beware the bijective requirement of
 * this map's contract. This map is synchronized.
 * 
 * @param <K>
 *          the key's type
 * @param <V>
 *          the value's type
 * @author Stefan Rybacki
 */
public class BidirectionalHashMap<K, V> implements IBidirectionalMap<K, V>,
    Serializable {

  /** Serialization ID. */
  private static final long serialVersionUID = -5589403535478191960L;

  /** The reverse map. */
  private final Map<V, K> reverseMap = new HashMap<>();

  /** The internal map. */
  private final Map<K, V> internalMap = new HashMap<>();

  /**
   * Instantiates a new bidirectional hash map. The map has the requirement,
   * that values are unique. That means there can be no two keys that identify
   * the same value.
   */
  public BidirectionalHashMap() {
  }

  @Override
  public synchronized void clear() {
    internalMap.clear();
    reverseMap.clear();
  }

  @Override
  public synchronized boolean containsValue(Object value) {
    return reverseMap.containsKey(value);
  }

  @Override
  public synchronized K getKey(Object value) {
    return reverseMap.get(value);
  }

  @Override
  public synchronized V put(K key, V value) {
    if (reverseMap.containsKey(value) && !reverseMap.get(value).equals(key)) {
      throw new IllegalArgumentException(
          "The specified value is already identified by another key! This map only allows unique values as well as unique keys!");
    }

    V v = internalMap.put(key, value);
    reverseMap.put(value, key);
    return v;
  }

  @Override
  public synchronized V remove(Object key) {
    if (containsKey(key)) {
      V remove = internalMap.remove(key);
      reverseMap.remove(remove);
      return remove;
    }

    return null;
  }

  @Override
  public int size() {
    return internalMap.size();
  }

  @Override
  public boolean isEmpty() {
    return internalMap.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return internalMap.containsKey(key);
  }

  @Override
  public V get(Object key) {
    return internalMap.get(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (java.util.Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

  @Override
  public Set<K> keySet() {
    return internalMap.keySet();
  }

  @Override
  public Collection<V> values() {
    return internalMap.values();
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    return internalMap.entrySet();
  }

  @Override
  public String toString() {
    return internalMap.toString();
  }

}
