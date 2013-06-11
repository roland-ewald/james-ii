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
import org.jamesii.core.math.statistics.univariate.Analysis;
import org.jamesii.core.math.statistics.univariate.Median;

/**
 * Tests the {@link Median} class.
 * 
 * @author Jan Himmelspach *
 */
public class MedianTest extends StatisticsTest {

  /** Tests the {@link Median#median(long[])} method. */
  public void testMedianLong() {
    long[] test = new long[] { 1, 2, 3, 4, 5, 6, 7 };
    assertEquals(4., Median.median(test));

    test = new long[] { 1, 2, 3, 4, 5, 6 };
    assertEquals(3.5, Median.median(test));
  }

  /** Tests the {@link Median#median(int[])} method. */
  public void testMedianInt() {
    int[] test = new int[] { 1, 2, 3, 4, 5, 6, 7 };
    assertEquals(4.0, Median.median(test));

    test = new int[] { 1, 2, 3, 4, 5, 6 };
    assertEquals(3.5, Median.median(test));

  }

  /** Tests the {@link Median#median(double[])} method. */
  public void testMedianDouble() {
    double[] test = new double[] { 1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7 };
    assertEquals(4.4, Median.median(test));

    assertEquals(4.4, new Analysis().compute(test).getMedian(), EPSILON);

    test = new double[] { 1.1, 2.2, 3, 4, 5.2, 6.4 };
    assertEquals(3.5, Median.median(test));

    assertEquals(3.5, new Analysis().compute(test).getMedian(), EPSILON);
  }

  /** Tests the {@link Median#median(float[])} method. */
  public void testMedianFloat() {
    float[] test = new float[] { 1.1f, 2.2f, 3.3f, 4.4f, 5.5f, 6.6f, 7.7f };
    assertEquals(4.4, Median.median(test), EPSILON);

    test = new float[] { 1, 2, 3, 4, 5, 6 };
    assertEquals(3.5, Median.median(test), EPSILON);
  }

  /**
   * Converts a {@code double} array into an {@link ArrayList}.
   * 
   * @param vals
   *          The values, as {@code double[]}.
   * @return An {@link ArrayList} containing the values.
   */
  private static List<Double> toList(double[] vals) {
    ArrayList<Double> result = new ArrayList<>();
    for (int i = 0; i < vals.length; i++) {
      result.add(vals[i]);
    }
    return result;
  }

  /** Tests the {@link Median#median(List)} method. */
  public void testMedianDoubleArrayList() {
    List<Double> test =
        toList(new double[] { 1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7 });

    assertEquals(4.4, Median.median(test));

    test = toList(new double[] { 1, 2, 3, 4, 5, 6 });
    assertEquals(3.5, Median.median(test));
  }

}
