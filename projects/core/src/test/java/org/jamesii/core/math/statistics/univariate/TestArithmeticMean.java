/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.statistics.StatisticsTest;

/**
 * The Class TestArithmeticMean.
 */
public class TestArithmeticMean extends StatisticsTest {

  /** The dl. */
  private List<Double> dl;

  /** The ia. */
  private int[] ia;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dl = new ArrayList<>();
    dl.add(1.2);
    dl.add(3.4);
    dl.add(8.7);
    dl.add(-12.2);
    dl.add(6.1);
    dl.add(-69.3);
    dl.add(-22.24);
    dl.add(0.0);

    ia = new int[2000];
    for (int i = 1; i <= ia.length; i++) {
      ia[i - 1] = i;
    }
  }

  /**
   * Checks if is within epsilon.
   * 
   * @param a
   *          the a
   * @param b
   *          the b
   */
  private void isWithinEpsilon(double a, double b) {
    assertTrue(a + " is not close enough to " + b, Math.abs(a - b) < EPSILON);
  }

  /**
   * Test arithmetic mean double list.
   */
  public void testArithmeticMeanDoubleList() {
    isWithinEpsilon(ArithmeticMean.arithmeticMean(dl), -10.5425);

    assertEquals(ArithmeticMean.arithmeticMean(new ArrayList<Double>()), 0.0);

    List<Double> b = new ArrayList<>();
    b.add(42.0);
    assertEquals(ArithmeticMean.arithmeticMean(b), 42.0);
  }

  /**
   * Test arithmetic mean double array.
   */
  public void testArithmeticMeanDoubleArray() {
    double[] a = new double[dl.size()];
    int i = 0;
    for (double x : dl) {
      a[i++] = x;
    }

    isWithinEpsilon(ArithmeticMean.arithmeticMean(a), -10.5425);

    assertEquals(ArithmeticMean.arithmeticMean(new double[] {}), 0.0);

    assertEquals(ArithmeticMean.arithmeticMean(new double[] { 42.0 }), 42.0);

    assertEquals(ArithmeticMean.arithmeticMean(new double[] { 42.0 }),
        Analysis.compute(new double[] { 42.0 }).getMean(), EPSILON);
    assertEquals(ArithmeticMean.arithmeticMean(a), Analysis.compute(a)
        .getMean(), EPSILON);
  }

  /**
   * Test arithmetic mean int array.
   */
  public void testArithmeticMeanIntArray() {
    isWithinEpsilon(ArithmeticMean.arithmeticMean(ia), 1000.5);

    assertEquals(ArithmeticMean.arithmeticMean(new int[] {}), 0.0);

    assertEquals(ArithmeticMean.arithmeticMean(new int[] { 42 }), 42.0);
  }

  /**
   * Test arithmetic mean large double array.
   */
  public void testArithmeticMeanLargeDoubleArray() {
    double[] a = new double[dl.size()];
    int i = 0;
    for (double x : dl) {
      a[i++] = x;
    }

    isWithinEpsilon(ArithmeticMean.arithmeticMeanLarge(a), -10.5425);

    assertEquals(ArithmeticMean.arithmeticMean(a), Analysis.compute(a)
        .getMean(), EPSILON);

    assertEquals(ArithmeticMean.arithmeticMeanLarge(new double[] {}), 0.0);

    assertEquals(ArithmeticMean.arithmeticMeanLarge(new double[] { 42.0 }),
        42.0);
  }

  /**
   * Test arithmetic mean large int array.
   */
  public void testArithmeticMeanLargeIntArray() {
    isWithinEpsilon(ArithmeticMean.arithmeticMeanLarge(ia), 1000.5);

    assertEquals(ArithmeticMean.arithmeticMeanLarge(new int[] {}), 0.0);

    assertEquals(ArithmeticMean.arithmeticMeanLarge(new int[] { 42 }), 42.0);
  }
}
