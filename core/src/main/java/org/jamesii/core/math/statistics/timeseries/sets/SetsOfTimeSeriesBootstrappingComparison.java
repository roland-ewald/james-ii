package org.jamesii.core.math.statistics.timeseries.sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.statistics.tests.IPairedTest;

/**
 * Simple procedure that allows two sets of time series to be compared with each
 * other. The general idea is quite simple:
 * 
 * <pre>
 * 1. Randomly draw a time series X', Y' from each set respectively, and
 * calculate their distance (repeat n times)
 * 
 * 2. Randomly draw two time series X', X'' from the *first* set and calculate
 * their distance (repeat n times)
 * 
 * 3. Apply a paired test to the empirical distributions of the differences. If
 * the null hypothesis can be rejected, the sets are not sampled from the same
 * distribution.
 * </pre>
 * 
 * @param <T>
 *          the type of the time series elements
 * @author Roland Ewald
 */
public class SetsOfTimeSeriesBootstrappingComparison<T> {

  /** The minimum number of time series per sample set. */
  private static final int MIN_PERMITTED_SET_SIZE = 10;

  /** The first set of time series. */
  private final Collection<List<T>> timeSeriesSet1;

  /** The second set of time series. */
  private final Collection<List<T>> timeSeriesSet2;

  /** The distance measure. */
  private final ITimeSeriesDistance<T> distanceMeasure;

  /** The statistical tests to be applied. */
  private final IPairedTest[] statisticalTests;

  /** The random number generator to be used. */
  private IRandom rng = SimSystem.getRNGGenerator().getNextRNG();

  /**
   * The empirical distribution of differences between time series from both
   * samples.
   */
  private List<Double> comparisonDiffDistribution;

  /**
   * The empirical distribution of differences between time series from a single
   * distribution.
   */
  private List<Double> singleSampleDiffDistribution;

  /**
   * Instantiates a bootstrapping comparison for two sets of time series .
   * 
   * @param firstTimeSeriesSet
   *          the first time series set
   * @param secondTimeSeriesSet
   *          the second time series set
   * @param timeSeriesDistance
   *          a time series distance measure
   * @param pairedTests
   *          the two-sample statistical tests to be applied
   */
  public SetsOfTimeSeriesBootstrappingComparison(
      Collection<List<T>> firstTimeSeriesSet,
      Collection<List<T>> secondTimeSeriesSet,
      ITimeSeriesDistance<T> timeSeriesDistance, IPairedTest... pairedTests) {

    if (firstTimeSeriesSet.size() < MIN_PERMITTED_SET_SIZE
        || secondTimeSeriesSet.size() < MIN_PERMITTED_SET_SIZE) {
      throw new IllegalArgumentException(
          "Per time series set there should be at least "
              + MIN_PERMITTED_SET_SIZE + " elements.");
    }

    timeSeriesSet1 = firstTimeSeriesSet;
    timeSeriesSet2 = secondTimeSeriesSet;
    distanceMeasure = timeSeriesDistance;
    statisticalTests = pairedTests;
  }

  /**
   * Executes the comparison.
   * 
   * @param sampleSize
   *          the sample size (see
   * @return he confidences of the statistical tests
   */
  public double[] execute(int sampleSize) {
    return execute(sampleSize, true);
  }

  /**
   * Executes the comparison.
   * 
   * @param sampleSize
   *          the sample size
   * @param useFirstSetForComparison
   *          the flag to determine whether to use the empirical difference
   *          distribution of the first sample set for comparison
   * @return the confidences of the statistical tests
   */
  public double[] execute(int sampleSize, boolean useFirstSetForComparison) {
    comparisonDiffDistribution =
        sampleDifferenceDistribution(sampleSize, timeSeriesSet1, timeSeriesSet2);

    Collection<List<T>> singleSampleSet =
        useFirstSetForComparison ? timeSeriesSet1 : timeSeriesSet2;

    singleSampleDiffDistribution =
        sampleDifferenceDistribution(sampleSize, singleSampleSet,
            singleSampleSet);

    return applyStatisticalTests();
  }

  /**
   * Applies all statistical tests to the empirical difference distributions.
   * 
   * @return the confidences (p-values) of the statistical tests (see
   *         {@link IPairedTest})
   */
  protected double[] applyStatisticalTests() {
    double[] results = new double[statisticalTests.length];
    for (int i = 0; i < results.length; i++) {
      results[i] =
          statisticalTests[i].executeTest(singleSampleDiffDistribution,
              comparisonDiffDistribution);
    }
    return results;
  }

  /**
   * Samples the difference distribution of two time series sets. Neglects the
   * comparison of *identical* (in the '==' sense) time series, which is
   * important in case the same sample set is chosen for sample1 and sample2.
   * 
   * @param sampleSize
   *          the sample size
   * @param sample1
   *          the first sample set from which to draw a time series
   * @param sample2
   *          the second sample set from which to draw a time series
   * @return the list of differences (length determined by sample size)
   */
  private List<Double> sampleDifferenceDistribution(int sampleSize,
      Collection<List<T>> sample1, Collection<List<T>> sample2) {
    List<Double> differences = new ArrayList<>();

    // While sample is still too small
    while (differences.size() < sampleSize) {

      int desiredSampleNumber = sampleSize - differences.size();

      // Draw random time series (as much as necessary)
      List<List<T>> firstResample =
          RandomSampler.sample(desiredSampleNumber, sample1, rng);
      List<List<T>> secondResample =
          RandomSampler.sample(desiredSampleNumber, sample2, rng);

      for (int i = 0; i < desiredSampleNumber; i++) {
        // This is not about equality, but to avoid actually comparing
        // *identical* elements
        if (firstResample.get(i) != secondResample.get(i)) {
          differences.add(distanceMeasure.compare(firstResample.get(i),
              secondResample.get(i)));
        }
      }
    }
    return differences;
  }

  /**
   * Gets the random number generator.
   * 
   * @return the random number generator
   */
  public IRandom getRNG() {
    return rng;
  }

  /**
   * Sets the random number generator.
   * 
   * @param rng
   *          the new random number generator
   */
  public void setRNG(IRandom rng) {
    this.rng = rng;
  }

  /**
   * Gets the empirical difference distribution of time series sampled from both
   * sets.
   * 
   * @return the empirical difference distribution (of comparing the time series
   *         sets)
   */
  public List<Double> getComparisonDiffDistribution() {
    return comparisonDiffDistribution;
  }

  /**
   * Gets the empirical difference distribution from time series drawn from the
   * same set.
   * 
   * @return the empirical difference distribution from time series drawn from
   *         the same set
   */
  public List<Double> getSingleSampleDiffDistribution() {
    return singleSampleDiffDistribution;
  }

}
