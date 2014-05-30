/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class related to collections, providing methods for dealing with
 * pseudo-multi-maps (i.e. maps of keys to collections of values), find the
 * intersection of sorted data in O(m+n), counting multiply occurring elements,
 * etc.
 * 
 * @author Arne Bittig
 */
public final class CollectionUtils {

  /** Utility class' hidden constructor */
  private CollectionUtils() {
  }

  /**
   * Helper method to put single values into a Map<K,Collection<V>>, creating a
   * new {@link ArrayList}<V> if there nothing associated with the given key yet
   * (cf.: the Guava (formerly Google Collections) Library's ListMultimap)
   * 
   * @param multiMap
   *          Map of keys to collection of values
   * @param key
   *          Key
   * @param value
   *          Value
   * @return <tt>true</tt> if this collection changed as a result of the call
   *         (analoguously to {@link Collection#add(Object)}), i.e. if the given
   *         key was not already associated with the given value
   */
  public static <K, V> boolean putIntoListMultiMap(
      Map<K, Collection<V>> multiMap, K key, V value) {
    Collection<V> collV = multiMap.get(key);
    if (collV == null) {
      collV = new ArrayList<>(1);
      collV.add(value);
      multiMap.put(key, collV);
      return true;
    } else {
      return collV.add(value);
    }
  }

  /**
   * Helper method to put single values into a Map<K,Collection<V>>, creating a
   * new {@link LinkedHashSet}<V> if there nothing associated with the given key
   * yet (see also: the Guava (formerly Google Collections) Library's
   * SetMultimap)
   * 
   * @param multiMap
   *          Map of keys to collection of values
   * @param key
   *          Key
   * @param value
   *          Value
   * @return <tt>true</tt> if this collection changed as a result of the call
   *         (analoguously to {@link Collection#add(Object)}), i.e. if the given
   *         key was not already associated with the given value
   */
  public static <K, V> boolean putIntoSetMultiMap(
      Map<K, Collection<V>> multiMap, K key, V value) {
    Collection<V> collV = multiMap.get(key);
    if (collV == null) {
      collV = new LinkedHashSet<>();
      collV.add(value);
      multiMap.put(key, collV);
      return true;
    } else {
      return collV.add(value);
    }
  }

  /**
   * Helper method to remove single values from a Map<K,Collection<V>> (cf.: the
   * Guava (formerly Google Collections) Library's Multimap interface). Removes
   * the key altogether from the map if it was associated only with this one
   * value.
   * 
   * @param multiMap
   *          Map of keys to collection of values
   * @param key
   *          Key
   * @param value
   *          Value
   * @return true if the multiMap actually contained the given pair, i.e.
   *         changed as a result of this method call
   */
  public static <K, V> boolean removeFromMultiMap(
      Map<K, ? extends Collection<V>> multiMap, K key, V value) {
    Collection<V> collV = multiMap.get(key);
    if (collV == null) {
      return false;
    }
    if (collV.size() > 1) {
      collV.remove(value);
      return true;
    }
    if (collV.contains(value)) {
      multiMap.remove(key);
      return true;
    }
    return false;
  }

  /**
   * Check whether a collection of collections has an element that has the same
   * elements as a given other collection, ignoring order and duplicate elements
   * (i.e. using {@link Collection#containsAll(Collection)}, not
   * {@link Collection#equals(Object)}; e.g. considering [1,1,2] and [1,2,2] as
   * contained in each other).
   * 
   * @param collColl
   * @param otherColl
   * @return one element of collColl containing all of otherColl and contained
   *         in otherColl, null if none
   */
  public static <E> Collection<E> containsCollection(
      Collection<? extends Collection<E>> collColl, Collection<?> otherColl) {
    for (Collection<E> c : collColl) {
      if (c.containsAll(otherColl) && otherColl.containsAll(c)) {
        return c;
      }
    }
    return null;
  }

  /**
   * Find unique elements in a list and the number of each one's occurrences
   * (although the methods accepts {@link Iterable}s, it makes most sense for
   * lists, and none for sets, for example). Returns an updateable,
   * insertion-order-retaining map.
   * 
   * @param list
   *          List of objects
   * @return object->number of occurences in list (null if param is null)
   * @see #uniqueOccurences(Iterable, Map)
   */
  public static <T> IUpdateableMap<T, Integer> uniqueOccurrences(
      Iterable<T> list) {
    if (list == null) {
      return null;
    }
    Map<T, Integer> map = new LinkedHashMap<>();
    uniqueOccurences(list, map);
    return UpdateableAmountMap.wrap(map);
  }

  /**
   * Find unique elements in a list and the number of occurrences of each and
   * put them into the given map. Unlike {@link #uniqueOccurrences(Iterable)},
   * this method does not return a new map but rather fills a given one (which
   * is not checked for emptiness!), which is useful if a special type of map is
   * needed, e.g. a TreeMap. (Although the first argument is an {@link Iterable}
   * , the method makes most sense for lists, and none for sets, for example).
   * 
   * @param list
   *          List of objects
   * @param map
   *          Map in which to record the unique elements and their amounts
   */
  public static <T> void uniqueOccurences(Iterable<T> list, Map<T, Integer> map) {
    for (T el : list) {
      Integer prevVal = map.get(el);
      if (prevVal == null) {
        map.put(el, 1);
      } else {
        map.put(el, prevVal + 1);
      }
    }
  }

  /**
   * Fill map with given key-value pairs (for one-line initialization of maps
   * with entries)
   * 
   * @param map
   *          Map
   * @param firstKey
   *          First key
   * @param firstValue
   *          First value
   * @param otherKeysAndValues
   *          more key-value pairs (no compile-time type check!)
   * @return Map with keys and values added
   */
  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> fillMap(Map<K, V> map, K firstKey,
      V firstValue, Object... otherKeysAndValues) {
    if (otherKeysAndValues.length % 2 == 1) {
      throw new IllegalArgumentException("Got "
          + (otherKeysAndValues.length + 2)
          + " parameters. Pairs of key and value required");
    }
    map.put(firstKey, firstValue);
    for (int i = 0; i < otherKeysAndValues.length; i += 2) {
      map.put((K) otherKeysAndValues[i], (V) otherKeysAndValues[i + 1]);
    }
    return map;
  }

  /**
   * Add elements from given collection to given map, associated with the same
   * value
   * 
   * @param map
   *          Map to add to
   * @param c
   *          Collection of elements to add as keys
   * @param val
   *          Value to associate elements with
   * @return the given map
   */
  public static <K, V> Map<K, V> fillMap(Map<K, V> map, Collection<K> c, V val) {
    for (K key : c) {
      map.put(key, val);
    }
    return map;
  }

  /**
   * View of a collection as map (with collection elements as keys and null as
   * value). The remove operation removes entries from the backing list, too, if
   * possible. Other modifications are not supported.
   * 
   * @param c
   *          Collection
   * @return Map view of c
   */
  public static <K> Map<K, Object> mapView(final Collection<K> c) {
    return new AbstractMap<K, Object>() {

      @Override
      public Set<Map.Entry<K, Object>> entrySet() {
        return new AbstractSet<Map.Entry<K, Object>>() {

          @Override
          public Iterator<Map.Entry<K, Object>> iterator() {
            return new Iterator<Map.Entry<K, Object>>() {

              Iterator<K> collIt = c.iterator();

              @Override
              public boolean hasNext() {
                return collIt.hasNext();
              }

              @Override
              public java.util.Map.Entry<K, Object> next() {
                return new AbstractMap.SimpleImmutableEntry<K, Object>(
                    collIt.next(), null);
              }

              @Override
              public void remove() {
                collIt.remove();
              }
            };
          }

          @Override
          public int size() {
            return c.size();
          }
        };
      }

    };
  }

  /**
   * Intersection of two sorted collections (lists, usually, but LinkedHashSets
   * would do, too, for example)
   * 
   * @param c1
   *          One collection
   * @param c2
   *          Another collection
   * @return List of members of both collections
   */
  public static <T extends Comparable<T>> List<T> intersectSorted(
      Iterable<T> c1, Iterable<T> c2) {
    Iterator<T> it1 = c1.iterator();
    Iterator<T> it2 = c2.iterator();
    if (!it1.hasNext() || !it2.hasNext()) {
      // one collection empty => intersection empty
      return new ArrayList<>(0);
    }
    T i1 = it1.next();
    T i2 = it2.next();
    List<T> rv = new ArrayList<>();
    do {
      int cmp = i1.compareTo(i2);
      if (cmp == 0) {
        rv.add(i1);
        i1 = it1.next();
        i2 = it2.next();
      } else if (cmp < 0) {
        i1 = it1.next();
      } else {
        i2 = it2.next();
      }
    } while (it1.hasNext() && it2.hasNext());
    // now i1 is the final element of it1, or i2 the final of it2
    T tailEl = sortedCollectionTailIncludes(it1, it2, i1, i2);
    if (tailEl != null) {
      rv.add(tailEl);
    }
    return rv;
  }

  /**
   * Find the first common element in two sorted collections (lists, usually,
   * but LinkedHashSets would do, too, for example). Unlike
   * {@link #intersectSorted(Iterable, Iterable)}, this method returns after the
   * first common element found and does not look for all of them.
   * 
   * @param c1
   *          One collection
   * @param c2
   *          Another collection
   * @return null if no common element, first common element otherwise
   */
  public static <T extends Comparable<T>> T sortedCollectionsIntersect(
      Iterable<T> c1, Iterable<T> c2) {
    Iterator<T> it1 = c1.iterator();
    Iterator<T> it2 = c2.iterator();
    if (!it1.hasNext() || !it2.hasNext()) {
      return null;
      // one collection empty => intersection empty
    }
    T i1 = it1.next();
    T i2 = it2.next();
    while (it1.hasNext() && it2.hasNext()) {
      int cmp = i1.compareTo(i2);
      if (cmp == 0) {
        return i1;
      } else if (cmp < 0) {
        i1 = it1.next();
      } else {
        i2 = it2.next();
      }
    }
    return sortedCollectionTailIncludes(it1, it2, i1, i2);
  }

  /**
   * Helper method for {@link #sortedCollectionsIntersect(Iterable, Iterable)} :
   * check whether one sorted collection includes final element of the other
   * (using their respective iterators and elements returned by the last
   * {@link Iterator#next()} calll, assuming {@link Iterator#hasNext()} will
   * return false for at least one of the two iterators)
   * 
   * @param it1
   *          final part of one sorted collection; empty if it2 is not
   * @param it2
   *          final part of another sorted collection; empty if it1 is not
   * @param i1
   *          previous element of it1
   * @param i2
   *          previous element of it2
   * @return the element in question or null if it is not included
   */
  private static <T extends Comparable<T>> T sortedCollectionTailIncludes(
      Iterator<T> it1, Iterator<T> it2, T i1, T i2) {
    int cmp = i1.compareTo(i2);
    if (cmp == 0) {
      return i1;
    } else if (cmp < 0) {
      while (it1.hasNext() && cmp < 0) {
        cmp = it1.next().compareTo(i2);
        if (cmp == 0) {
          return i2;
        }
      }
    } else {
      // (cmp > 0)
      while (it2.hasNext() && cmp > 0) {
        cmp = i1.compareTo(it2.next());
        if (cmp == 0) {
          return i1;
        }
      }
    }
    return null;
  }
}