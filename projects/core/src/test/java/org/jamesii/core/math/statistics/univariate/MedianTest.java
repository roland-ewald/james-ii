/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jamesii.core.math.statistics.StatisticsTest;

/**
 * Tests the {@link Median} class.
 * 
 * @author Jan Himmelspach
 */
public class MedianTest extends StatisticsTest {

  /** Tests the {@link Median#median(Collection)} method. */
  public void testMedian() {
    List<Double> test = Arrays.asList(1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7);
    assertEquals(4.4, Median.median(test));
    Collections.shuffle(test);
    assertEquals(4.4, Median.median(test));
    assertEquals(3.5, Median.median(Arrays.asList(1., 2., 3., 4., 5., 6.)));
    assertEquals(3.5, Median.median(Arrays.asList(1, 6, 5, 4, 2, 3)));
  }

  /** Tests the {@link Median#medianFromSorted(List)} method. */
  public void testMedianFromSorted() {
    List<Double> test = Arrays.asList(1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7);

    assertEquals(4.4, Median.medianFromSorted(test));

    test = Arrays.asList(1., 2., 3., 4., 5., 6.);
    assertEquals(3.5, Median.medianFromSorted(test));
  }

  /** Tests the {@link Median#medianFromSorted(long[])} method. */
  public void testMedianFromSortedLong() {
    long[] test = new long[] { 1, 2, 3, 4, 5, 6, 7 };
    assertEquals(4., Median.medianFromSorted(test));

    test = new long[] { 1, 2, 3, 4, 5, 6 };
    assertEquals(3.5, Median.medianFromSorted(test));
  }

  /** Tests the {@link Median#medianFromSorted(int[])} method. */
  public void testMedianFromSortedInt() {
    int[] test = new int[] { 1, 2, 3, 4, 5, 6, 7 };
    assertEquals(4.0, Median.medianFromSorted(test));

    test = new int[] { 1, 2, 3, 4, 5, 6 };
    assertEquals(3.5, Median.medianFromSorted(test));

  }

  /** Tests the {@link Median#medianFromSorted(double[])} method. */
  public void testMedianFromSortedDouble() {
    double[] test = new double[] { 1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7 };
    assertEquals(4.4, Median.medianFromSorted(test));

    assertEquals(4.4, new Analysis().compute(test).getMedian(), EPSILON);

    test = new double[] { 1.1, 2.2, 3, 4, 5.2, 6.4 };
    assertEquals(3.5, Median.medianFromSorted(test));

    assertEquals(3.5, new Analysis().compute(test).getMedian(), EPSILON);
  }

  /** Tests the {@link Median#medianFromSorted(float[])} method. */
  public void testMedianFromSortedFloat() {
    float[] test = new float[] { 1.1f, 2.2f, 3.3f, 4.4f, 5.5f, 6.6f, 7.7f };
    assertEquals(4.4, Median.medianFromSorted(test), EPSILON);

    test = new float[] { 1, 2, 3, 4, 5, 6 };
    assertEquals(3.5, Median.medianFromSorted(test), EPSILON);
  }

}
