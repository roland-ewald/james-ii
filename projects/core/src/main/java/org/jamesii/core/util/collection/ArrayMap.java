/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * A Map, using an {@link org.jamesii.core.util.collection.ArraySet} as internal
 * data structure, i.e. entries are kept in their order of insertion and
 * multiple equal keys are allowed.
 * 
 * @author Stefan Leye
 * 
 * @param <K>
 *          type of the key
 * @param <V>
 *          type of the value
 */
public class ArrayMap<K, V> extends AbstractMap<K, V> implements Serializable {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -7813146766386624197L;

  /**
   * The set representing the internal structure of the map.
   */
  private ArraySet<Entry<K, V>> entrySet;

  /**
   * Construct ArrayMap with the default initial capacity (of ArrayList).
   */
  public ArrayMap() {
    entrySet = new ArraySet<>();
  }

  /**
   * Construct ArrayMap with given initial capacity
   * 
   * @param initialCapacity
   *          Initial capacity
   * @author Arne Bittig
   */
  public ArrayMap(int initialCapacity) {
    entrySet = new ArraySet<>(initialCapacity);
  }

  /**
   * Entry class for the use in an ArrayMap.
   * 
   * @author Stefan Leye
   * 
   * @param <K>
   *          the type of the key
   * @param <V>
   *          the type of the value
   */
  @SuppressWarnings({ "hiding", "unchecked" })
  private class ArrayEntry<K, V> implements Map.Entry<K, V>, Serializable {

    /**
     * The serialization ID.
     */
    private static final long serialVersionUID = 5738152339106867773L;

    /**
     * Array representing the entry.
     */
    private Object[] entry = new Object[2];

    /**
     * Constructor, setting key and value.
     * 
     * @param key
     *          the key
     * @param value
     *          the value
     */
    public ArrayEntry(K key, V value) {
      entry[0] = key;
      entry[1] = value;
    }

    @Override
    public K getKey() {
      return (K) entry[0];
    }

    @Override
    public V getValue() {
      return (V) entry[1];
    }

    @Override
    public V setValue(V value) {
      V old = getValue();
      entry[1] = value;
      return old;
    }

  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    return entrySet;
  }

  /**
   * Adds a new entry to the map. Warning! a similar key is not replaced,
   * therefore the old (returned) value is null.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   * @return null
   */
  @Override
  public V put(K key, V value) {
    Entry<K, V> entry = new ArrayEntry<>(key, value);
    entrySet.add(entry);
    return null;
  }
}
