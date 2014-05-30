/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Interface to <Key, Number> map where the value can be updated BY some number,
 * not only TO a new value. Implementations may also allow updating all values
 * by a certain fraction of themselves. The {@link #put(Object, Object)} method,
 * i.e. value update TO a new value without regard to the current value may be
 * disallowed in implementations.
 * 
 * @author Arne Bittig
 * 
 * @param <K>
 *          key type
 * @param <V>
 *          value type (some {@link Number})
 */
public interface IUpdateableMap<K, V extends Number> extends Map<K, V> {

  /**
   * Update the value associated with the key by the specified amount. If the
   * key is not present, put the specified value into the map (i.e. act as if it
   * were present with associated value 0).
   * 
   * @param key
   *          Key
   * @param updateByValue
   *          Value to add to the associated value
   * @return New value after update
   */
  V update(K key, V updateByValue);

  /**
   * Update the value associated with the key by the specified amount for all
   * keys in given map.
   * 
   * @param updateValueMap
   * @see #update(Object, Number)
   */
  void updateAll(Map<K, V> updateValueMap);

  /**
   * Add a single occurrence of the key, i.e. increase its associated value by
   * one.
   * 
   * @param key
   *          Key
   * @return New value associated with key after update
   */
  V add(K key);

  /**
   * Create a new map with the same keys associated with values corresponding to
   * a given fraction of the values in this map, decrease associated value in
   * this map by the same amount (optional operation; may require further
   * restriction of the value type; may require rounding of fractional parts)
   * 
   * @param frac
   *          Fraction of values to "split off"
   * @return Map containing the "removed" part frac*v for each key
   */
  IUpdateableMap<K, V> split(double frac);

  /**
   * Create a new map with the same keys associated with values corresponding to
   * a given fraction of the values in this map, decrease associated value in
   * this map by the same amount (optional operation; added random number
   * generator parameter compared to {@link #split(double)} for alternative
   * dealing with fractional parts)
   * 
   * This is only useful if the map's value type cannot accurately represent
   * every mathematically possible result of the multiplication of one of its
   * values with a Double, e.g. if it is Integer.
   * 
   * @param frac
   *          Fraction of values to "split off"
   * @param random
   *          Random number generator for assignment of marginal parts
   * @return Map containing the "removed" part frac*v for each key
   */
  IUpdateableMap<K, V> split(double frac, IRandom random);
}
