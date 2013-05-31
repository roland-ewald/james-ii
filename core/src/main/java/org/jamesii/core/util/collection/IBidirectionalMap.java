/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.Map;

/**
 * This interface provides a {@link Map} value->key linking (using
 * {@link #getKey(Object)}). Usually handled by two {@link Map}s (one for each
 * direction) implementations of this interface can handle both. The only
 * requirement is that there can be no two keys identifying the same value
 * (bijective).
 * 
 * @author Stefan Rybacki
 * @see BidirectionalHashMap
 */
public interface IBidirectionalMap<K, V> extends Map<K, V> {
  /**
   * Gets the key for a specific value.
   * 
   * @param value
   *          the value the key to retrieve for
   * @return the key
   */
  K getKey(Object value);

}
