package org.jamesii.core.util.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

/**
 * @author Arne Bittig
 */
public class UpdateableMapTest extends TestCase {

  /**
   * Create {@link UpdateableAmountMap}, add a few entries, update them, check
   * whether result is correct
   */
  public final void testUpdateableAmountMap() {
    IUpdateableMap<String, Integer> um1 =
        UpdateableAmountMap.wrap(new TreeMap<String, Integer>(), 0, 5);
    String testEnt = "first";
    um1.put(testEnt, 4);

    Map<String, Integer> umcopy = new UpdateableAmountMap<>(um1);

    um1.update(testEnt, -2);
    Boolean correct = false;
    try {
      umcopy.put(testEnt, 3);
      assertTrue(correct);
    } catch (IllegalArgumentException e) {
      correct = true;
    }
    assertTrue(correct);
    assertTrue(umcopy.get(testEnt) == 4);
    umcopy.put("second", 3);
    correct = false;
    try {
      umcopy.put("third", -1);
      umcopy.put("first", 1);
      assertTrue(correct);
    } catch (IllegalArgumentException e) {
      correct = true;
    }
    assertTrue(correct);
  }

  /**
   * Test {@link UpdateableAmountMap#split(double)}
   */
  public final void testSplit() {
    Map<String, Integer> map = new HashMap<>(5);
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);
    map.put("four", 4);
    map.put("five", 5);
    Map<String, Integer> split = UpdateableAmountMap.split(map, 0.2, null);
    assertEquals(1, split.size());
    assertEquals("five", split.keySet().iterator().next());
    assertTrue(1 == split.values().iterator().next());
    assertTrue(4 == map.get("five"));
  }

}
