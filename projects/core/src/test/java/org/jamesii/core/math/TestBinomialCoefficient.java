/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import org.jamesii.core.math.BinomialCoefficient;

import junit.framework.TestCase;

/**
 * Tests the {@link BinomialCoefficient} class.
 * 
 * @author Johannes Rössel
 */
public class TestBinomialCoefficient extends TestCase {

  /** Tests the {@link BinomialCoefficient#binomial(int, int)} method. */
  public void testBinomial() {
    for (int i = 0; i < 30; i++) {
      assertEquals(1, BinomialCoefficient.binomial(i, i));
      assertEquals(1, BinomialCoefficient.binomial(i, 0));
    }
    for (int i = 1; i < 30; i++) {
      assertEquals(i, BinomialCoefficient.binomial(i, i - 1));
      assertEquals(i, BinomialCoefficient.binomial(i, 1));
    }

    assertEquals(28, BinomialCoefficient.binomial(8, 2));
    assertEquals(15, BinomialCoefficient.binomial(6, 4));

    assertEquals(0, BinomialCoefficient.binomial(1, -4));
    assertEquals(0, BinomialCoefficient.binomial(1, 5));

    assertEquals(28, BinomialCoefficient.binomial(8d, 2d), Double.MIN_NORMAL);
    assertEquals(15, BinomialCoefficient.binomial(6d, 4d), Double.MIN_NORMAL);
  }

  /**
   * Tests whether {@link BinomialCoefficient#binomial(int, int)} throws the
   * correct exceptions when encountering invalid parameters.
   */
  public void testBinomialExceptions() {
    try {
      BinomialCoefficient.binomial(-1, -4);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      BinomialCoefficient.binomial(-1, 4);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /** Tests the {@link BinomialCoefficient#binomialQuick(int, int)} method. */
  public void testBinomialQuick() {
    for (int i = 0; i < 30; i++) {
      assertEquals(1.0, BinomialCoefficient.binomialQuick(i, i), 0.000001);
      assertEquals(1.0, BinomialCoefficient.binomialQuick(i, 0), 0.000001);
    }
    for (int i = 1; i < 30; i++) {
      assertEquals(i, BinomialCoefficient.binomialQuick(i, i - 1), 0.000001);
      assertEquals(i, BinomialCoefficient.binomialQuick(i, 1), 0.000001);
    }

    assertEquals(28.0, BinomialCoefficient.binomialQuick(8, 2), 0.000001);
    assertEquals(15.0, BinomialCoefficient.binomialQuick(6, 4), 0.000001);

    assertEquals(0.0, BinomialCoefficient.binomialQuick(1, 2));
    assertEquals(0.0, BinomialCoefficient.binomialQuick(1, 8));
    assertEquals(0.0, BinomialCoefficient.binomialQuick(2, -5));
  }

  /** Tests the {@link BinomialCoefficient#binomialQuick(int, int)} method. */
  public void testBinomialQuick3() {
    for (int i = 0; i < 30; i++) {
      assertEquals(1.0, BinomialCoefficient.binomialQuick3(i, i), 0.000001);
      assertEquals(1.0, BinomialCoefficient.binomialQuick3(i, 0), 0.000001);
    }
    for (int i = 1; i < 30; i++) {
      assertEquals(i, BinomialCoefficient.binomialQuick3(i, i - 1), 0.000001);
      assertEquals(i, BinomialCoefficient.binomialQuick3(i, 1), 0.000001);
    }

    assertEquals(28.0, BinomialCoefficient.binomialQuick3(8, 2), 0.000001);
    assertEquals(15.0, BinomialCoefficient.binomialQuick3(6, 4), 0.000001);

    assertEquals(0.0, BinomialCoefficient.binomialQuick3(1, 2));
    assertEquals(0.0, BinomialCoefficient.binomialQuick3(1, 8));
    assertEquals(0.0, BinomialCoefficient.binomialQuick3(2, -5));
  }

  /**
   * Tests whether {@link BinomialCoefficient#binomialQuick(int, int)} throws
   * the correct exceptions.
   */
  public void testBinomialQuickExceptions() {
    try {
      BinomialCoefficient.binomialQuick(-1, -4);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      BinomialCoefficient.binomialQuick(-23, 10);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }
}
