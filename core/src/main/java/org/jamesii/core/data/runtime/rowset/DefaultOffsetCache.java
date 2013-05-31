/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.util.misc.Pair;

import static java.lang.Math.abs;

/**
 * This is the default caching strategy for offsets in the file. It works by
 * trying to retain a uniformly-distributed view on the file. To this end, if
 * the cache grows too large, only every <i>n</i><sup>th</sup> value will be
 * stored, where <i>n</i> may vary over time to accommodate both smaller and
 * larger files. It starts out with {@code 1}, so initially every value will be
 * cached. If the cache grows too large, <i>n</i> will simply be doubled, but is
 * capped at 256. So even in the worst case (but assuming complete cache
 * coverage) the farthest cached value is only 128 lines away.
 * <p>
 * Of course, this only works well if the file is accessed sequentially and the
 * “key” indexes are eventually cached.
 * 
 * @author Johannes Rössel
 */
public class DefaultOffsetCache implements IOffsetCache {
  /** The map to hold the data. */
  private Map<Integer, Long> map = new HashMap<>(200);

  /**
   * Determines which lines should be cached. This will be adapted over time so
   * that the cache does not grow too large. The number simply tells the
   * distance between cache entries retained – so if this value is 32 every 32th
   * value will be retained.
   */
  private int cacheFrequency = 1;

  /**
   * Retrieves the maximum number of entries to be cached. This varies with the
   * frequency.
   * 
   * @return The maximum number of entries
   */
  private int getMaxEntries() {
    if (cacheFrequency < 256) {
      return 500;
    }

    return Integer.MAX_VALUE;
  }

  /** Removes elements from the cache that no longer fit the cache criteria. */
  private void cull() {
    Iterator<Entry<Integer, Long>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      if (it.next().getKey() % cacheFrequency != 0) {
        it.remove();
      }
    }
  }

  @Override
  public void put(int index, long offset) {
    if (index < 0 || offset < 0) {
      throw new IllegalArgumentException(
          "Neither index nor offset may be negative.");
    }

    if (index % cacheFrequency != 0) {
      return;
    }
    map.put(index, offset);
    if (map.size() > getMaxEntries()) {
      cacheFrequency *= 2;
      cull();
    }
  }

  @Override
  public void putTentatively(int index, long offset) {
    put(index, offset);
  }

  @Override
  public long get(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index may not be negative.");
    }

    Long o = map.get(index);
    if (o == null) {
      return -1;
    }
    return o;
  }

  @Override
  public Pair<Integer, Long> findClosest(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index may not be negative.");
    }

    if (map.size() == 0) {
      return null;
    }

    long o = get(index);
    if (o != -1) {
      return new Pair<>(index, o);
    }

    int closest = -1;
    long diff = Long.MAX_VALUE;
    for (int i : map.keySet()) {
      if (abs((long) i - index) < diff) {
        closest = i;
        diff = abs((long) i - index);
      }
    }

    return new Pair<>(closest, get(closest));
  }

  @Override
  public void clear() {
    map.clear();
  }

}
