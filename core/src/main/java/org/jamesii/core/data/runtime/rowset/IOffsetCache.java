/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import org.jamesii.core.util.misc.Pair;

/**
 * An interface for a cache of file offsets, mapped to line indexes. A cache
 * must implement this interface but is free to implement the actual cache in
 * any way desired. Caches should be capped at a certain size and a particular
 * implementation can involve a certain strategy as to what entries are kept and
 * what get culled once the cache gets too large (e.g.
 * <em>most-recently used</em>, <em>most-frequently used</em>, etc.).
 * <p>
 * A cache must offer fast access to its entries (otherwise there is no
 * particular benefit to using a cache).
 * 
 * @author Johannes Rössel
 */
public interface IOffsetCache {
  /**
   * Stores a mapping from an index to a file offset in the cache.
   * <p>
   * If the index is already cached its offset will be overwritten by
   * {@code offset} as the new value.
   * 
   * @param index
   *          The line index.
   * @param offset
   *          The file offset.
   * 
   * @throws IllegalArgumentException
   *           if either {@code index} or {@code offset} are negative.
   */
  void put(int index, long offset);

  /**
   * <em>Might</em> store a mapping from an index to a file offset in the cache.
   * Whether or not this actually causes a value to be cached depends on the
   * implementation of the cache.
   * <p>
   * This method will be called while seeking to a requested index and in the
   * process iterating over lines. While this still technically is moving from
   * line to line in the file, the purpose of a cache might be to only store
   * values actually <em>requested</em> instead of merely iterated over.
   * <p>
   * Implementations seeking to broadly cover the complete file eventually
   * should probably implement this method. On the other hand, an implementation
   * only interested in most-recent use or frequency of use will likely ignore
   * this.
   * 
   * @param index
   *          The line index.
   * @param offset
   *          The file offset.
   * 
   * @throws IllegalArgumentException
   *           if either {@code index} or {@code offset} are negative.
   */
  void putTentatively(int index, long offset);

  /**
   * Retrieves a file offset from the cache.
   * 
   * @param index
   *          The line index.
   * @return The file offset for the given line index. If this cache doesn't
   *         contain a mapping for the given index, {@code -1} is returned.
   * 
   * @throws IllegalArgumentException
   *           if {@code index} is negative.
   */
  long get(int index);

  /**
   * Retrieves the index and corresponding offset that are closest (in either
   * direction) to the requested index. Since a cache often cannot store every
   * value ever needed this is used to find a value that is close to continue
   * seeking to the desired index.
   * 
   * @param index
   *          The requested line index.
   * @return A {@link Pair} of index and offset that is closest to the requested
   *         index or {@code null} if the cache was empty.
   * 
   * @throws IllegalArgumentException
   *           if {@code index} is negative.
   */
  Pair<Integer, Long> findClosest(int index);

  /**
   * Removes all items from this cache.
   */
  void clear();
}
