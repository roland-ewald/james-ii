/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import java.util.Arrays;
import java.util.List;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwister;
import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.util.misc.Pair;

/**
 * Tests {@link AbstractNormalDistribution}.
 * 
 * @author Arne Bittig
 */
public abstract class AbstractNormalDistributionTest extends ChattyTestCase {

  private static final double[] MEANS = { 0., -1., 1., -5. };

  private static final double[] DEVIATIONS = { 1., 2., 9. };

  private static final int NUM_DRAWS = 100000;

  public void testDistributionFunction() {
    for (double mean : MEANS) {
      for (double dev : DEVIATIONS) {
        AbstractNormalDistribution normDist =
            getDistribution(new MersenneTwister(), mean, dev);
        double[] values = new double[NUM_DRAWS];
        for (int i = 0; i < NUM_DRAWS; i++) {
          values[i] = normDist.getRandomNumber();
        }
        Arrays.sort(values);
        Pair<Double, Double> varianceAndAM = Variance.varianceAndAM(values);
        System.out.println(normDist.getClass().getSimpleName() + "," + mean
            + "," + dev + "," +values[(int) (NUM_DRAWS*0.03)]+ ","+values[NUM_DRAWS/2] + ","+ values[(int) (NUM_DRAWS*0.97)]+","
            +minMedianMeanMaxStdAmountMutating(values));
      }
    }
  }

  /**
   * @param numbers
   *          Array of numbers (will be sorted!)
   * @return min, median, mean, max, variance, non-zero amount
   */
  public static List<Number> minMedianMeanMaxStdAmountMutating(double[] numbers) {
    Arrays.sort(numbers);
    int size = numbers.length;
    double median;
    if (size % 2 == 1) {
      median = numbers[size / 2];
    } else {
      median = 0.5 * (numbers[size / 2 - 1] + numbers[size / 2]);
    }
    double sum = 0.;
    for (double n : numbers) {
      sum += n;
    }
    double mean = sum / size;
    double var = 0;
    for (double n1 : numbers) {
      double dev = -mean + n1;
      var += dev * dev;
    }
    var /= size;
    return Arrays.asList(new Number[] { numbers[0], median, mean,
        numbers[size - 1], Math.sqrt(var), size });
  }

  /**
   * @param rand
   *          pseudo random number generator
   * @param mean
   *          Mean
   * @param dev
   *          Standard deviation
   * @return Normal distribution
   */
  protected abstract AbstractNormalDistribution getDistribution(IRandom rand,
      double mean, double dev);
}
