/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map-wrapping class where null keys or null values can be disallowed (
 * {@link NullPointerException}s will be thrown)
 *
 * @author Arne Bittig
 * @param <K>
 *          Key type
 * @param <V>
 *          Value type
 * @date 20.02.2013
 */
public class NonNullMap<K, V> implements Map<K, V> {

  private final boolean allowNullKeys;

  private final boolean allowNullValues;

  private final Map<K, V> map;

  /**
   * Default constructor: wrap a {@link LinkedHashMap}, no null keys, no null
   * values
   */
  public NonNullMap() {
    this(false, false);
  }

  /**
   * Wrap {@link LinkedHashMap} with custom allowances. Note that if both
   * boolean values are true, the map should rather be used directly.
   * 
   * @param allowNullKeys
   *          Flag whether null keys are acceptable
   * @param allowNullValues
   *          Flag whether null values are acceptable
   */
  public NonNullMap(boolean allowNullKeys, boolean allowNullValues) {
    this(new LinkedHashMap<K, V>(), allowNullKeys, allowNullValues);
  }

  /**
   * Full constructor. Note that if both boolean values are true, the map should
   * rather be used directly.
   * 
   * @param map
   *          Map to wrap
   * @param allowNullKeys
   *          Flag whether null keys are acceptable
   * @param allowNullValues
   *          Flag whether null values are acceptable
   */
  public NonNullMap(Map<K, V> map, boolean allowNullKeys,
      boolean allowNullValues) {
    super();
    this.allowNullKeys = allowNullKeys;
    this.allowNullValues = allowNullValues;
    this.map = map;
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return map.get(key);
  }

  @Override
  public V put(K key, V value) {
    if (key == null && !allowNullKeys) {
      throw new NullPointerException("Null key with value " + value);
    }
    if (value == null && !allowNullValues) {
      throw new NullPointerException("Null value for key " + key);
    }
    return map.put(key, value);
  }

  @Override
  public V remove(Object key) {
    return map.remove(key);
  }

  /**
   * @param m
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    if (m.containsKey(null) && !allowNullKeys) {
      throw new NullPointerException("Null key with value " + m.get(null));
    }
    if (m.containsValue(null) && !allowNullValues) {
      throw new NullPointerException("Null value in " + m);
    }

    map.putAll(m);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<K> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<V> values() {
    return map.values();
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    return map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return map.equals(o);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public String toString() {
    return map.toString();
  }

}
