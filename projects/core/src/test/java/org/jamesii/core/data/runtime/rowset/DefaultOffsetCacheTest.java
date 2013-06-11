package org.jamesii.core.data.runtime.rowset;

import org.jamesii.core.data.runtime.rowset.DefaultOffsetCache;
import org.jamesii.core.data.runtime.rowset.IOffsetCache;
import org.jamesii.core.util.misc.Pair;

/**
 * Tests the {@link DefaultOffsetCache} class.
 * 
 * @author Johannes Rössel
 */
public class DefaultOffsetCacheTest extends OffsetCacheContractsTest {

  @Override
  protected IOffsetCache createCache() {
    return new DefaultOffsetCache();
  }

  public void testPutTentativelyStoresValues() {
    cache.putTentatively(10, 10);
    assertEquals(10, cache.get(10));
  }

  public void testCullingRemovesCertainValues() {
    for (int i = 0; i < 2000; i++) {
      cache.put(i, i);
    }

    assertEquals(-1, cache.get(1));
    assertEquals(new Pair<>(0, 0L), cache.findClosest(1));
  }

}
