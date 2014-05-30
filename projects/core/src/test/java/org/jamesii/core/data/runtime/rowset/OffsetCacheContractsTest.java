/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import org.jamesii.core.data.runtime.rowset.IOffsetCache;

import junit.framework.TestCase;

/**
 * Tests that implementations of {@link IOffsetCache} follow the contract
 * spelled out in the documentation.
 * 
 * @author Johannes Rössel
 */
public abstract class OffsetCacheContractsTest extends TestCase {

  /** The {@link IOffsetCache} implementation to test. */
  protected IOffsetCache cache;

  /**
   * Creates an instance of a particular {@link IOffsetCache} implementation for
   * use in this test.
   * 
   * @return The newly-created cache instance.
   */
  protected abstract IOffsetCache createCache();

  @Override
  protected void setUp() throws Exception {
    cache = createCache();
  }

  @Override
  protected void tearDown() throws Exception {
    cache = null;
  }

  public void testGetReturnsMinusOneIfIndexDoesNotExist() {
    assertEquals(-1, cache.get(50));
  }

  public void testFindClosestReturnsNullIfCacheIsEmpty() {
    assertNull(cache.findClosest(50));
  }

  public void testPutThrowsExceptionOnNegativeIndex() {
    try {
      cache.put(-1, 0);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testPutThrowsExceptionOnNegativeOffset() {
    try {
      cache.put(0, -1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testPutTentativelyThrowsExceptionOnNegativeIndex() {
    try {
      cache.putTentatively(-1, 0);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testPutTentativelyThrowsExceptionOnNegativeOffset() {
    try {
      cache.putTentatively(0, -1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testGetThrowsExceptionOnNegativeIndex() {
    try {
      cache.get(-1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testFindClosestThrowsExceptionOnNegativeIndex() {
    try {
      cache.put(0, 0);
      cache.findClosest(-1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

}
