/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries.sets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.ChattyTestCase;
import org.jamesii.SimSystem;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.statistics.tests.IPairedTest;
import org.jamesii.core.math.statistics.tests.plugintype.PairedTestFactory;
import org.jamesii.core.math.statistics.tests.wilcoxon.WilcoxonRankSumTestFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Tests for the {@link SetsOfTimeSeriesBootstrappingComparison}.
 * 
 * @author Roland Ewald
 * 
 */
public class SetsOfTimeSeriesBootstrappingComparisonTest extends ChattyTestCase {

  /** The number of data points per time series. */
  private static final int DATA_POINTS = 100;

  /** The number of time series per sample set. */
  private static final int SAMPLE_SIZE = 100000;

  /** The number of random samples that should be used for comparison. */
  private static final int SAMPLE_SIZE_COMPARISON = 100000;

  /** The interval shift used to test the detection for uniform distributions. */
  private static final double INTERVAL_SHIFT_FOR_TESINTG = .02;

  /**
   * A lower (than default) standard deviation to test the detection for normal
   * distributions.
   */
  private static final double LOWER_STANDARD_DEV_FOR_TESTING = 0.9;

  /**
   * A higher (than default) mean to test the detection for normal
   * distributions.
   */
  private static final double HIGHER_MEAN_FOR_TESTING = 0.2;

  /** The default random distribution to be used. */
  private static final IDistribution DEFAULT_RANDOM_DIST =
      new NormalDistribution(SimSystem.getRNGGenerator().getNextRNG());

  /** The statistical test to be used. */
  private static final PairedTestFactory STAT_TEST =
      new WilcoxonRankSumTestFactory();

  /**
   * A simple test case with two similarly generated sets of time series.
   */
  public void testSimpleMatchingCase() {
    testWithRandomData(false, DEFAULT_RANDOM_DIST, DEFAULT_RANDOM_DIST);
  }

  /**
   * Test simple rejection with a second set of time series generated with a
   * higher mean.
   */
  public void testSimpleRejectionWithHigherMean() {
    testWithRandomData(true, DEFAULT_RANDOM_DIST, new NormalDistribution(
        SimSystem.getRNGGenerator().getNextRNG(), HIGHER_MEAN_FOR_TESTING, 1));
  }

  /**
   * Test simple rejection with a second set of time series generated with a
   * lower variance.
   */
  public void testSimpleRejectionWithLowerVariance() {
    testWithRandomData(true, DEFAULT_RANDOM_DIST, new NormalDistribution(
        SimSystem.getRNGGenerator().getNextRNG(), 0,
        LOWER_STANDARD_DEV_FOR_TESTING));
  }

  /**
   * Test simple rejection with a second set of time series generated with a
   * lower variance.
   */
  public void testSimpleRejectionWithUniformDistributions() {
    testWithRandomData(true, new UniformDistribution(SimSystem
        .getRNGGenerator().getNextRNG(), INTERVAL_SHIFT_FOR_TESINTG, 1.),
        new UniformDistribution(SimSystem.getRNGGenerator().getNextRNG(), .0,
            1 - INTERVAL_SHIFT_FOR_TESINTG));
  }

  /**
   * Test the comparator with random data.
   * 
   * @param shouldReject
   *          the flag to determine whether the comparison should reject the
   *          null hypothesis (i.e., that both time series samples are drawn
   *          from the same distribution)
   * @param distribution1
   *          the distribution to generate the data points of the first time
   *          series set
   * @param distribution2
   *          the distribution to generate the data points of the second time
   *          series set
   */
  private void testWithRandomData(boolean shouldReject,
      IDistribution distribution1, IDistribution distribution2) {
    List<List<Double>> sample1 =
        generateRandomTimeSeriesSampleSet(distribution1);
    List<List<Double>> sample2 =
        generateRandomTimeSeriesSampleSet(distribution2);

    SetsOfTimeSeriesBootstrappingComparison<Double> timeSeriesComparison =
        new SetsOfTimeSeriesBootstrappingComparison<>(sample1, sample2,
            new TimeSeriesDistanceForTesting(),
            new IPairedTest[] { STAT_TEST.create(new ParameterBlock(), SimSystem.getRegistry().createContext()) });

    double[] confidences = timeSeriesComparison.execute(SAMPLE_SIZE_COMPARISON);
    assertEquals("There is one difference measure, hence one cnonfidence.", 1,
        confidences.length);
    reportConfidences(confidences[0]);
    assertEquals(
        "The null hypothesis should "
            + (shouldReject ? "" : "not")
            + " be rejected (note that this test may be false positive/negative occasionally!).",
        shouldReject, confidences[0] < .001);
  }

  /**
   * Reports confidence.
   * 
   * @param confidence
   *          the confidence
   */
  private void reportConfidences(double confidence) {
    String report =
        "Simple case confidence with '"
            + STAT_TEST.getClass().getCanonicalName() + "':" + confidence;
    addInformation(report);
    SimSystem.report(Level.INFO, report);
  }

  /**
   * Generate random time series sample set.
   * 
   * @param randomDist
   *          the random distribution to be used for time series generation
   * @return the generated sample set
   */
  private List<List<Double>> generateRandomTimeSeriesSampleSet(
      IDistribution randomDist) {
    List<List<Double>> sampleSet = new ArrayList<>();
    for (int i = 0; i < SAMPLE_SIZE; i++) {
      sampleSet.add(generateTimeSeries(randomDist));
    }
    return sampleSet;
  }

  /**
   * Generates a random time series.
   * 
   * @param randomDist
   *          the random distribution to be used
   * @return the generated time series
   */
  private List<Double> generateTimeSeries(IDistribution randomDist) {
    List<Double> timeSeries = new ArrayList<>();
    for (int i = 0; i < DATA_POINTS; i++) {
      timeSeries.add(randomDist.getRandomNumber());
    }
    return timeSeries;
  }
}

/**
 * A simple distance measure to test the comparison.
 */
class TimeSeriesDistanceForTesting implements ITimeSeriesDistance<Double> {
  @Override
  public Double compare(List<Double> timeSeries1, List<Double> timeSeries2) {
    double maxDiff = 0.;
    for (int i = 0; i < Math.min(timeSeries1.size(), timeSeries1.size()); i++) {
      maxDiff =
          Math.max(maxDiff, Math.abs(timeSeries1.get(i) - timeSeries2.get(i)));
    }
    return maxDiff;
  }

}
