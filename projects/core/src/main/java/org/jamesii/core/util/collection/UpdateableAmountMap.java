/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Map of keys and amount values (Integers). Can be used to represent
 * multiset-like structures (a.k.a. bags), but with read access through the
 * {@link Map} interface. (James II has an {@link IMultiSet}, but that offers no
 * direct access to the amount of each member in the set and manipulation of
 * amounts only in steps of one (via {@link Set#add(Object)} and
 * {@link Set#remove(Object)}).
 * 
 * This implementation automatically removes keys with associated amount of 0
 * and throws an error if the amount becomes less than that (unless another
 * lower bound is specified by using the appropriate constructor). Updates of
 * absent (e.g. removed) keys are treated as if the respective key is associated
 * with value 0. Direct modification of values of keys already present are not
 * allowed (i.e. put(key) throws and error if key is present). Uses a HashMap
 * internally, unless otherwise specified in the appropriate constructor.
 * 
 * @author Arne Bittig
 * 
 * @param <K>
 *          Key type
 */
public class UpdateableAmountMap<K> implements IUpdateableMap<K, Integer>,
    Serializable {

  /** Serialization ID */
  private static final long serialVersionUID = -5239145027929222447L;

  private Map<K, Integer> internalMap;

  private final int lowerBound;

  private final int upperBound;

  /**
   * Default constructor (use a {@link LinkedHashMap} internally, minimum amount
   * 0, default rounding for {@link #split(double)} operation)
   */
  public UpdateableAmountMap() {
    this(new LinkedHashMap<K, Integer>(), 0);
  }

  /**
   * Wrapped {@link LinkedHashMap} with given intial capacity, using specified
   * amount boundaries
   * 
   * @param initialCapacity
   *          Initial map capacity
   * @param bounds
   *          Lower and upper amount bound (optional; if 0 or 1 value given:
   *          upper bound = MAXINT, if no values given: lower bound = 0)
   * @throws IllegalArgumentException
   *           if more than two boundary values are given
   */
  public UpdateableAmountMap(int initialCapacity, int... bounds) {
    this(new LinkedHashMap<K, Integer>(initialCapacity), bounds);
  }

  /**
   * Factory method for wrapping a given map, i.e. use it directly as internal
   * data in an {@link UpdateableAmountMap}. If the parameter already is an
   * {@link UpdateableAmountMap}, its internal map is re-wrapped (useful for
   * setting new lower and upper amount bounds).
   * 
   * NO check is performed whether the map contains any values outside the
   * desired bounds!
   * 
   * @param mapToWrap
   *          Map to use internally
   * @param bounds
   *          Lower and upper amount bound (optional; if 0 or 1 value given:
   *          upper bound = MAXINT, if no values given: lower bound = 0)
   * @return {@link UpdateableAmountMap}
   * @throws IllegalArgumentException
   *           if more than two boundary values are given
   */
  public static <K> UpdateableAmountMap<K> wrap(Map<K, Integer> mapToWrap,
      int... bounds) {
    if (mapToWrap instanceof UpdateableAmountMap<?>) {
      return new UpdateableAmountMap<>(
          ((UpdateableAmountMap<K>) mapToWrap).internalMap, bounds);
    }
    return new UpdateableAmountMap<>(mapToWrap, bounds);
  }

  /**
   * Full constructor using an already established map directly.
   * 
   * @param mapToUse
   *          Map to use internally (may be empty or filled; NOT copied)
   * @param bounds
   *          Optional boundaries for values - if not given: [0,MAXINT], if only
   *          one value: [value,MAXINT], else: [1st value, 2nd value]
   */
  private UpdateableAmountMap(Map<K, Integer> mapToUse, int... bounds) {
    if (bounds.length > 2) {
      throw new IllegalArgumentException("Only lower and upper bound may be"
          + "specified. Cannot deal with value " + bounds[2] + " and onwards.");
    }
    if (mapToUse instanceof UpdateableAmountMap) {
      this.internalMap = ((UpdateableAmountMap<K>) mapToUse).internalMap;
    } else {
      this.internalMap = mapToUse;
    }
    this.lowerBound = bounds.length > 0 ? bounds[0] : 0;
    this.upperBound = bounds.length > 1 ? bounds[1] : Integer.MAX_VALUE;

  }

  /**
   * Copy constructor - tries to use an internal map of the same type as the
   * parameter (displays a warning message and uses a LinkeHashMap if that
   * fails). Uses default values for lower and upper bound [0,MAXINT] unless the
   * parameter is an {@link UpdateableAmountMap} itself (then copies its
   * values).
   * 
   * @param mapToCopy
   */
  public UpdateableAmountMap(Map<K, Integer> mapToCopy) {
    Map<K, Integer> intMapToCopy;
    if (mapToCopy instanceof UpdateableAmountMap) {
      UpdateableAmountMap<K> mtc = (UpdateableAmountMap<K>) mapToCopy;
      intMapToCopy = mtc.internalMap;
      this.lowerBound = mtc.lowerBound;
      this.upperBound = mtc.upperBound;
    } else {
      intMapToCopy = mapToCopy;
      this.lowerBound = 0;
      this.upperBound = Integer.MAX_VALUE;
    }

    this.internalMap = tryToCopyMap(intMapToCopy);
    if (this.internalMap == null) {
      SimSystem.report(Level.WARNING, "Cannot determine how to create"
          + " new instance of " + intMapToCopy.getClass().getName()
          + ". Using a HashMap instead.");
      this.internalMap = new LinkedHashMap<>(intMapToCopy);
    }
  }

  /**
   * Try to copy given map, i.e. create an instance of the same map class with
   * the same entries, by
   * <ul>
   * <li>trying to reflectively create a new map (see
   * {@link #tryToCopyMapReflectively(Map)}, and if this fails,
   * <li>check whether map to copy implements {@link Cloneable} and invoking
   * clone() reflectively
   * <li>giving up if all of the above fails, returning null.
   * </ul>
   * 
   * @param mapToCopy
   *          Map to be copied
   * @return Copy of given map, or null if no suitable way to copy found
   */
  private static <K, V> Map<K, V> tryToCopyMap(Map<K, V> mapToCopy) {
    Map<K, V> mapCopy = tryToCopyMapReflectively(mapToCopy);
    if (mapCopy != null) {
      return mapCopy;
    }
    if (mapToCopy instanceof Cloneable) {
      try {
        Method clone = mapToCopy.getClass().getDeclaredMethod("clone");
        return (Map<K, V>) clone.invoke(mapToCopy);
      } catch (ReflectiveOperationException e) { /* no recovery */
      }
    }
    // ...give up ;-)
    return null;

  }

  /**
   * Try to copy given map, i.e. create an instance of the same map class with
   * the same entries, by
   * <ul>
   * <li>looking for a constructor taking a map as parameter and returning a map
   * created by this copy constructor
   * <li>looking for a parameterless constructor, invoking it and then using
   * {@link Map#putAll(Map)}, returning the result
   * <li>giving up if all of the above fails, returning null.
   * </ul>
   * 
   * @param mapToCopy
   *          Map to be copied
   * @return Copy of given map, or null if no suitable way to copy found
   */

  private static <K, V> Map<K, V> tryToCopyMapReflectively(Map<K, V> mapToCopy) {
    try { // ...to construct new Map using its type's copy constructor
      @SuppressWarnings("rawtypes")
      Constructor<? extends Map> mapCtr =
          mapToCopy.getClass().getConstructor(Map.class);
      return mapCtr.newInstance(mapToCopy);
    } catch (ReflectiveOperationException e) { /* see below */
    }
    try { // ...to construct new Map using default c'tor and putAll
      Map<K, V> rv = mapToCopy.getClass().newInstance();
      rv.putAll(mapToCopy);
      return rv;
    } catch (ReflectiveOperationException e) { /* no recovery */
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws IllegalArgumentException
   *           if value after update is not within previously specified
   *           boundaries
   */
  @Override
  public Integer update(K key, Integer updateByValue) {
    Integer cval = internalMap.get(key);
    if (cval == null) {
      cval = updateByValue;
    } else {
      cval += updateByValue;
    }
    if (cval == 0) {
      internalMap.remove(key);
    } else if (cval >= lowerBound && cval <= upperBound) {
      internalMap.put(key, cval);
    } else {
      throw new IllegalArgumentException("Amount of " + key + " less than "
          + lowerBound + " after update by " + updateByValue + " in "
          + this.internalMap);
    }
    return cval;
  }

  @Override
  public void updateAll(Map<K, Integer> updateValueMap) {
    for (Map.Entry<K, Integer> updE : updateValueMap.entrySet()) {
      this.update(updE.getKey(), updE.getValue());
    }
  }

  @Override
  public Integer add(K key) {
    return update(key, 1);
  }

  @Override
  public IUpdateableMap<K, Integer> split(double frac) {
    return split(frac, null);
  }

  @Override
  public IUpdateableMap<K, Integer> split(double frac, IRandom rand) {
    Map<K, Integer> newUAMap = getSplitOffPart(internalMap, frac, rand);
    // after previous loop to avoid concurrent modification exception:
    for (Map.Entry<K, Integer> newMapEntry : newUAMap.entrySet()) {
      this.update(newMapEntry.getKey(), -newMapEntry.getValue());
    }

    return new UpdateableAmountMap<>(newUAMap, lowerBound, upperBound);
  }

  /**
   * Decrease all integer values in a map by a certain fraction, returning a map
   * with the "part taken away" (same as {@link IUpdateableMap}
   * {@link #split(double, IRandom)}, but applicable to any KeyType->Integer
   * map. Marginal parts, i.e. the fractional parts in the multiplication of
   * each integer value with the given fraction, are discarded if no random
   * number generator is given (i.e. the split-off part is on average smaller
   * than if rounding were used). If a random number generator is given, if a
   * uniform random number from [0,1) does not exceed the fractional part, the
   * respective split-off is increased by one.
   * 
   * @param map
   *          Map to split a part off (will be modified!)
   * @param frac
   *          size of the part to split off
   * @param rand
   *          Random number generator for fractional parts (null for floor)
   * @return Map with the split-off part (original map is modified, too!)
   */
  public static <K> Map<K, Integer> split(Map<K, Integer> map, Double frac,
      IRandom rand) {
    Map<K, Integer> newUAMap = getSplitOffPart(map, frac, rand);
    // after previous loop to avoid concurrent modification exception:
    if (map instanceof IUpdateableMap) {
      for (Map.Entry<K, Integer> newMapEntry : newUAMap.entrySet()) {
        ((IUpdateableMap<K, Integer>) map).update(newMapEntry.getKey(),
            -newMapEntry.getValue());
      }
      return new UpdateableAmountMap<>(newUAMap, 0);
    } else {
      for (Map.Entry<K, Integer> newMapEntry : newUAMap.entrySet()) {
        map.put(newMapEntry.getKey(), map.get(newMapEntry.getKey())
            - newMapEntry.getValue());
      }
      return newUAMap;
    }

  }

  /**
   * Split off part of the "map" (see {@link #split(Map, Double, IRandom)},
   * {@link #split(Double)} and {@link #split(Double, IRandom)}) without no
   * change to the original map (yet) -- outsourced to avoid code duplication
   * 
   * @param map
   * @param frac
   * @param rand
   * @return
   */
  private static <K> Map<K, Integer> getSplitOffPart(Map<K, Integer> map,
      Double frac, IRandom rand) {
    Map<K, Integer> newUAMap = new LinkedHashMap<>();
    for (Map.Entry<K, Integer> intMapEntry : map.entrySet()) {
      // calculate share of original state
      Double newFrac = frac * intMapEntry.getValue();
      Integer newAmount = newFrac.intValue();
      // deal with marginal entity (i.e. rounding or random determination)
      if (rand != null && rand.nextDouble() <= newFrac - newAmount) {
        newAmount++;
      }
      if (newAmount > 0) {
        newUAMap.put(intMapEntry.getKey(), newAmount);
      }
    }
    return newUAMap;
  }

  // Below: delegate methods, partially UnsupportedOperationException()s

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
  public boolean containsValue(Object value) {
    return internalMap.containsValue(value);
  }

  @Override
  public Integer get(Object key) {
    return internalMap.get(key);
  }

  /**
   * {@inheritDoc}
   * 
   * @see #update(Object, Integer)
   * @throws IllegalArgumentException
   *           if key is already present
   */
  @Override
  public Integer put(K key, Integer value) {
    if (this.containsKey(key)) {
      throw new IllegalArgumentException("Key " + key
          + " already present. Only update is allowed.");
    }
    this.update(key, value); // checks value range
    return null; // consistency with Map.put: "previous" value
  }

  @Override
  public Integer remove(Object key) {
    if (!internalMap.containsKey(key)) {
      return null;
    }
    throw new UnsupportedOperationException("Keys are automatically"
        + " removed from UpdateableAmountMap if value becomes 0.");
  }

  @Override
  public void putAll(Map<? extends K, ? extends Integer> m) {
    for (java.util.Map.Entry<? extends K, ? extends Integer> e : m.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

  @Override
  public void clear() {
    internalMap.clear();
  }

  @Override
  public Set<K> keySet() {
    return internalMap.keySet();
  }

  @Override
  public Collection<Integer> values() {
    return internalMap.values();
  }

  @Override
  public Set<java.util.Map.Entry<K, Integer>> entrySet() {
    return internalMap.entrySet();
  }

  /**
   * {@inheritDoc} Note that if m1 and m2 are both {@link UpdateableAmountMap} s
   * with different lower or upper bound, they are not still equal, even
   * although they will not behave the same way in all subsequent operations.
   */
  @Override
  public boolean equals(Object obj) {
    return internalMap.equals(obj);
  }

  @Override
  public int hashCode() {
    return internalMap.hashCode();
  }

  @Override
  public String toString() {
    return internalMap.toString();
  }

  /**
   * Copy of a key -> integer value map where each key is associated with the
   * negation of its value in the parameter map. Copying is attempted via
   * reflectively creating a new instance of the same class as the parameter
   * (via copy constructor and no-args constructor and putAll), if that fails
   * via cloning. If all this fails, a {@link LinkedHashMap} is used.
   * 
   * @param map
   *          Map to copy with opposite value
   * @return key -> -original integer value map
   */
  public static <K> Map<K, Integer> negativeCopy(Map<K, Integer> map) {
    Map<K, Integer> mapCopy = tryToCopyMap(map);
    for (Map.Entry<K, Integer> e : mapCopy.entrySet()) {
      e.setValue(-e.getValue());
    }
    return mapCopy;
  }

  /**
   * View of a key -> integer value map where each key is associated with the
   * negation of its value in the parameter map. The view of the passed map is
   * unmodifiable, unless it is itself an opposite view of another map, in which
   * case that map is unwrapped and returned.
   * 
   * @param map
   *          Map to wrap
   * @return key -> -original integer value map
   */
  public static <K> Map<K, Integer> negativeView(Map<K, Integer> map) {
    if (map instanceof OppositeMap<?>) {
      return ((OppositeMap<K>) map).getMap();
    }
    return new OppositeMap<>(map);
  }

  private static class OppositeMap<K> extends AbstractMap<K, Integer> {

    private final Map<K, Integer> map;

    OppositeMap(Map<K, Integer> map) {
      this.map = map;
    }

    Map<K, Integer> getMap() {
      return map;
    }

    @Override
    public Set<java.util.Map.Entry<K, Integer>> entrySet() {
      return new AbstractSet<Map.Entry<K, Integer>>() {

        @Override
        public Iterator<java.util.Map.Entry<K, Integer>> iterator() {
          final Iterator<Map.Entry<K, Integer>> it =
              getMap().entrySet().iterator();
          return new Iterator<Map.Entry<K, Integer>>() {

            @Override
            public boolean hasNext() {
              return it.hasNext();
            }

            @Override
            public java.util.Map.Entry<K, Integer> next() {
              final Map.Entry<K, Integer> entry = it.next();
              return new Map.Entry<K, Integer>() {

                @Override
                public K getKey() {
                  return entry.getKey();
                }

                @Override
                public Integer getValue() {
                  return -entry.getValue();
                }

                @Override
                public Integer setValue(Integer value) {
                  throw new UnsupportedOperationException();
                }
              };
            }

            @Override
            public void remove() {
              throw new UnsupportedOperationException();
            }
          };
        }

        @Override
        public int size() {
          return getMap().size();
        }
      };
    }

    @Override
    public boolean containsKey(Object key) {
      return getMap().containsKey(key);
    }

    @Override
    public Integer get(Object key) {
      return -getMap().get(key);
    }

    @Override
    public Set<K> keySet() {
      return getMap().keySet();
    }

  }
}