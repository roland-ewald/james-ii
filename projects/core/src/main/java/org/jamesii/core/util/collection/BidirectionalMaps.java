/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Stefan Rybacki
 * 
 */
public final class BidirectionalMaps {
  private BidirectionalMaps() {
  }

  public static <K, V> IBidirectionalMap<K, V> synchronizedMap(
      final IBidirectionalMap<K, V> map) {
    return new SynchronizedBidirectionalMap<>(map);
  }

  private static final class SynchronizedBidirectionalMap<K, V> implements
      IBidirectionalMap<K, V> {

    private IBidirectionalMap<K, V> map;

    public SynchronizedBidirectionalMap(IBidirectionalMap<K, V> map) {
      this.map = map;
    }

    @Override
    public synchronized int size() {
      return map.size();
    }

    @Override
    public synchronized boolean isEmpty() {
      return map.isEmpty();
    }

    @Override
    public synchronized boolean containsKey(Object key) {
      return map.containsKey(key);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
      return map.containsValue(value);
    }

    @Override
    public synchronized V get(Object key) {
      return map.get(key);
    }

    @Override
    public synchronized V put(K key, V value) {
      return map.put(key, value);
    }

    @Override
    public synchronized V remove(Object key) {
      return map.remove(key);
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
      map.putAll(m);
    }

    @Override
    public synchronized void clear() {
      map.clear();
    }

    @Override
    public synchronized Set<K> keySet() {
      return map.keySet();
    }

    @Override
    public synchronized Collection<V> values() {
      return map.values();
    }

    @Override
    public synchronized Set<java.util.Map.Entry<K, V>> entrySet() {
      return map.entrySet();
    }

    @Override
    public synchronized K getKey(Object value) {
      return map.getKey(value);
    }

  }
}
