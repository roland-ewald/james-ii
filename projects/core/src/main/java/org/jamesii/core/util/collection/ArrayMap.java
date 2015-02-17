/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Map, using a {@link List} as internal data structure, i.e. entries are kept
 * in their order of insertion and multiple equal keys are allowed.
 * 
 * @author Stefan Leye
 * @author Arne Bittig
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
  private final ArraySet<Entry<K, V>> entrySet;

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

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
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
    Entry<K, V> entry = new AbstractMap.SimpleEntry<>(key, value);
    entrySet.add(entry);
    return null;
  }

  /**
   * Wrapper class for an {@link java.util.ArrayList}. Used by the
   * {@link org.jamesii.core.util.collection.ArrayMap} as internal set.
   * 
   * @author Stefan Leye
   * 
   * @param <E>
   *          type of the elements
   */
  private static class ArraySet<E> implements Set<E>, Serializable {

    /**
     * The serialization ID.
     */
    private static final long serialVersionUID = 8116956618683144254L;

    /**
     * The internal list.
     */
    private List<E> set;

    /**
     * Construct ArraySet with an initial capacity equal to the default
     * ArrayList initial capacity.
     */
    public ArraySet() {
      set = new ArrayList<>();
    }

    /**
     * Construct ArraySet with given initial capacity.
     * 
     * @param initialCapacity
     *          Initial capacity
     */
    public ArraySet(int initialCapacity) {
      set = new ArrayList<>(initialCapacity);
    }

    @Override
    public boolean add(E e) {
      return set.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
      return set.addAll(c);
    }

    @Override
    public void clear() {
      set = new ArrayList<>();
    }

    @Override
    public boolean contains(Object o) {
      return set.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return set.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
      return set.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
      return set.iterator();
    }

    @Override
    public boolean remove(Object o) {
      return set.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      return set.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return set.retainAll(c);
    }

    @Override
    public int size() {
      return set.size();
    }

    @Override
    public Object[] toArray() {
      return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return set.toArray(a);
    }

  }

}
