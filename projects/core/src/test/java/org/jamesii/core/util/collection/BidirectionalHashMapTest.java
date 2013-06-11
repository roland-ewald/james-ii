/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.collection.BidirectionalHashMap;
import org.jamesii.core.util.collection.IBidirectionalMap;

/**
 * @author Stefan Rybacki
 * 
 */
public class BidirectionalHashMapTest extends ChattyTestCase {
  private final Map<String, String> mappingTest = new HashMap<>();

  @Override
  protected void setUp() throws Exception {
    mappingTest.clear();
    long seed = (long) (Math.random() * System.nanoTime());
    addParameter("seed", seed);
    Random rnd = new Random(seed);

    int c = rnd.nextInt(1000);
    int offset = rnd.nextInt(1000) - 500;
    for (int i = 0; i < c; i++) {
      mappingTest.put(String.valueOf(offset + i), String.valueOf(offset - i));
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.BidirectionalHashMap#clear()}.
   */
  public final void testClear() {
    IBidirectionalMap<String, String> biMap = new BidirectionalHashMap<>();
    biMap.putAll(mappingTest);
    assertEquals(mappingTest.size(), biMap.size());
    biMap.clear();
    assertEquals(0, biMap.size());
    for (Entry<String, String> e : mappingTest.entrySet()) {
      assertFalse(biMap.containsValue(e.getValue()));
      assertEquals(null, biMap.getKey(e.getValue()));
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.BidirectionalHashMap#containsValue(java.lang.Object)}
   * .
   */
  public final void testContainsValueObject() {
    IBidirectionalMap<String, String> biMap = new BidirectionalHashMap<>();
    biMap.putAll(mappingTest);
    assertEquals(mappingTest.size(), biMap.size());
    for (Entry<String, String> e : mappingTest.entrySet()) {
      assertTrue(biMap.containsValue(e.getValue()));
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.BidirectionalHashMap#getKey(java.lang.Object)}
   * .
   */
  public final void testGetKey() {
    IBidirectionalMap<String, String> biMap = new BidirectionalHashMap<>();
    biMap.putAll(mappingTest);
    assertEquals(mappingTest.size(), biMap.size());
    for (Entry<String, String> e : mappingTest.entrySet()) {
      assertEquals(e.getKey(), biMap.getKey(e.getValue()));
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.BidirectionalHashMap#put(java.lang.Object, java.lang.Object)}
   * .
   */
  public final void testPutKV() {
    IBidirectionalMap<String, String> biMap = new BidirectionalHashMap<>();
    for (Entry<String, String> e : mappingTest.entrySet()) {
      biMap.put(e.getKey(), e.getValue());
      assertTrue(biMap.containsValue(e.getValue()));
      assertEquals(e.getKey(), biMap.getKey(e.getValue()));
    }
    assertEquals(mappingTest.size(), biMap.size());
    biMap.clear();
    assertEquals(0, biMap.size());
    // try to add the same value with different keys
    biMap.put("hello", "world!");
    try {
      biMap.put("greetings", "world!");
      fail("We shouldn't reach here because \"world\" was added twice!");
    } catch (IllegalArgumentException e) {
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.BidirectionalHashMap#remove(java.lang.Object)}
   * .
   */
  public final void testRemoveObject() {
    IBidirectionalMap<String, String> biMap = new BidirectionalHashMap<>();
    biMap.putAll(mappingTest);
    assertEquals(mappingTest.size(), biMap.size());
    for (Entry<String, String> e : mappingTest.entrySet()) {
      assertEquals(e.getValue(), biMap.remove(e.getKey()));
      assertFalse(biMap.containsValue(e.getValue()));
    }
  }

}
