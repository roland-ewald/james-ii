/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import org.jamesii.core.math.statistics.univariate.Range;

import junit.framework.TestCase;

/**
 * @author Jan Himmelspach *
 */
public class RangeTest extends TestCase {

  /**
   * @param name
   */
  public RangeTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test range.
   */
  public void testRange() {
    // the arrays have to be sorted, for the "normal" range methods
    double[] d0 = { 1. };
    assertTrue(Double.compare(Range.range(d0), 0) == 0);
    double[] d1 = { 1., 1., 1., 1., 1., 1. };
    assertTrue(Double.compare(Range.range(d1), 0) == 0);
    double[] d2 = { 1., 1., 1., 1., 2., 1. };
    assertTrue(Double.compare(Range.range(d2), 0.) == 0);
    double[] d3 = { 1., 4., 1., 1., 2., 1. };
    assertTrue(Double.compare(Range.range(d3), 0.) == 0);
    int[] i3 = { 1, 4, 1, 1, 2, 1 };
    assertTrue(Range.range(i3) == 0);

    assertTrue(Double.compare(Range.rangeF(d1), 0.) == 0);
    assertTrue(Double.compare(Range.rangeS(d1), 0.) == 0);
    assertTrue(Double.compare(Range.rangeF(d2), 1.) == 0);
    assertTrue(Double.compare(Range.rangeS(d2), 1.) == 0);
    assertTrue(Double.compare(Range.rangeF(d3), 3.) == 0);
    assertTrue(Double.compare(Range.rangeS(d3), 3.) == 0);
    assertTrue(Range.rangeS(i3) == 3);
    assertTrue(Range.rangeF(i3) == 3);

    // test with sorted arrays
    double[] d4 = { 1., 100. };
    assertEquals(Range.range(d4), -99, 0.001);
    double[] d5 = { 1., 1., 1., 1., 1., 1., 1.1 };
    assertEquals(Range.range(d5), -.1, 0.001);
    double[] d6 = { 1., 1., 1., 1., 1., 2., 3. };
    assertEquals(Range.range(d6), -2, 0.001);
    double[] d7 = { 0.1, 1., 1., 1., 2. };
    assertEquals(Range.range(d7), -1.9, 0.001);
  }

}
