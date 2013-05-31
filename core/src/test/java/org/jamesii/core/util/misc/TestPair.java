/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * The Class TestPair.
 * 
 * @author Johannes Rössel
 */
public class TestPair extends TestCase {

  /** The value of the first element of the pair, for testing. */
  private static final int FIRST_VALUE = 42;

  /** The value of the second element of the pair, for testing. */
  private static final int SECOND_VALUE = 23;

  /**
   * Tests proper setting of components in the constructor.
   */
  public static void testConstructors() {
    Pair<Integer, Integer> p = new Pair<>(FIRST_VALUE, SECOND_VALUE);
    checkPairValues(p);
    Pair<Integer, Integer> p2 = new Pair<>(p);
    checkPairValues(p2);
  }

  /**
   * Checks whether pair values are OK.
   * 
   * @param pair
   *          the p
   */
  private static void checkPairValues(Pair<Integer, Integer> pair) {
    assertTrue(pair.getFirstValue() == FIRST_VALUE);
    assertTrue(pair.getSecondValue() == SECOND_VALUE);
  }

  /**
   * Tests whether setters work correctly.
   */
  public static void testSetters() {
    Pair<Integer, Integer> p = new Pair<>(0, 0);
    assertTrue(p.getFirstValue() == 0);
    assertTrue(p.getSecondValue() == 0);
    p.setFirstValue(80);
    assertTrue(p.getFirstValue() == 80);
    assertTrue(p.getSecondValue() == 0);
    p.setSecondValue(74);
    assertTrue(p.getFirstValue() == 80);
    assertTrue(p.getSecondValue() == 74);
  }
}
