/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests the utility functions available in {@link Maps}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestMaps extends TestCase {

  /**
   * Tests {@link Maps#extractKeysByPrefix(Map, String)}.
   */
  public void testExtractKeysByPrefix() {

    Map<String, Boolean> testMap = new HashMap<>();
    testMap.put("a.b", true);
    testMap.put("a.b.c", true);
    testMap.put("c.b.a", true);

    assertEquals("There are two entries having keys that start with 'a.'", 2,
        Maps.extractKeysByPrefix(testMap, "a.").size());
    assertEquals("There is one entry having a key that starts with 'a.b.'", 1,
        Maps.extractKeysByPrefix(testMap, "a.b.").size());
    assertTrue("There is no entry having a key that starts with 'c..'", Maps
        .extractKeysByPrefix(testMap, "c..").isEmpty());
    assertTrue("Prefix is removed from key",
        Maps.extractKeysByPrefix(testMap, "a.").containsKey("b"));
  }
}
