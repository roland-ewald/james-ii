/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random;

import org.jamesii.core.math.random.generators.RNGPeriod;

import junit.framework.TestCase;

/**
 * Tests the {@link RNGPeriod} class.
 * 
 * @author Johannes Rössel
 */
public class TestRNGPeriod extends TestCase {

  /** Tests the constructor. */
  public void testConstructor() {
    RNGPeriod p = new RNGPeriod(2, 20);
    assertEquals(1.0, p.getMultiplier());
    assertEquals(2, p.getBase());
    assertEquals(20, p.getExponent());

    p = new RNGPeriod(3.14, 2, 20);
    assertEquals(3.14, p.getMultiplier(), 0.001);
    assertEquals(2, p.getBase());
    assertEquals(20, p.getExponent());
  }

  /** Tests the setter methods. */
  public void testSetters() {
    RNGPeriod p = new RNGPeriod(1, 1);
    assertEquals(1.0, p.getMultiplier());
    assertEquals(1, p.getBase());
    assertEquals(1, p.getExponent());

    p.setMultiplier(3.14);
    assertEquals(3.14, p.getMultiplier(), 0.001);
    assertEquals(1, p.getBase());
    assertEquals(1, p.getExponent());

    p.setBase(42);
    assertEquals(3.14, p.getMultiplier(), 0.001);
    assertEquals(42, p.getBase());
    assertEquals(1, p.getExponent());

    p.setExponent(420);
    assertEquals(3.14, p.getMultiplier(), 0.001);
    assertEquals(42, p.getBase());
    assertEquals(420, p.getExponent());
  }

  /** Tests the {@link RNGPeriod#compareTo(RNGPeriod)} method. */
  public void testCompareTo() {
    RNGPeriod p1 = new RNGPeriod(1, 2, 10);
    RNGPeriod p2 = new RNGPeriod(1.2, 2, 10);
    assertTrue(p1.compareTo(p2) < 0);

    p1.setMultiplier(1.4);
    assertTrue(p1.compareTo(p2) > 0);

    p1.setMultiplier(1);
    p1.setBase(3);
    p2.setMultiplier(1);
    assertTrue(p1.compareTo(p2) > 0);

    p2.setBase(3);
    p2.setExponent(11);
    assertTrue(p1.compareTo(p2) < 0);

    p1 = new RNGPeriod(2, 19937);
    p2 = new RNGPeriod(4.3, 10, 6001);
    assertTrue(p1.compareTo(p2) > 0);

    p2.setMultiplier(4.4);
    assertTrue(p1.compareTo(p2) < 0);
  }
}
