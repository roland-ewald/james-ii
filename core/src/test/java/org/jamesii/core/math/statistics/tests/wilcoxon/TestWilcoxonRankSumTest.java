/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests.wilcoxon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.BinomialDistribution;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.statistics.tests.wilcoxon.WilcoxonRankSumTest;
import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * Test for Wilcoxon {@link WilcoxonRankSumTest}.
 * 
 * @author Roland Ewald
 * @author Georg Straube
 */
public class TestWilcoxonRankSumTest extends TestCase {

  /** The sample size for the test with two samples containing the same number. */
  private static final int SAMPLE_SIZE_IDENTICAL_SAMPLES = 10000;

  /** The Wilcoxon rank-sum test. */
  WilcoxonRankSumTest test;

  /** RNG to generate random samples. */
  IRandom random = SimSystem.getRNGGenerator().getNextRNG();

  /** The sample size. */
  static final int SAMPLE_SIZE = 100;

  /** The maximal allowed p-value for the artificial examples. */
  private static final Double MAXIMAL_P_VALUE = 0.001;

  @Override
  public void setUp() {
    test = new WilcoxonRankSumTest();
  }

  /** Applies test to a constant sample and a random sample. */
  public void testConstantVsRandomSample() {
    List<Double> constantSample =
        getRandomSample(new UniformDistribution(random, 1.0, 1.0));
    List<Double> randomSample =
        getRandomSample(new UniformDistribution(random, 0.0, 1.0));
    checkTestResult(constantSample, randomSample);
  }

  /** Applies test to normally distributed and binomially distributed samples. */
  public void testNormalVsBinomialSample() {
    List<Double> normalDistSample =
        getRandomSample(new NormalDistribution(random, 5.0, 2.0));
    List<Double> binomialDistSample =
        getRandomSample(new BinomialDistribution(random));
    checkTestResult(normalDistSample, binomialDistSample);
  }

  /** Applies test to normally distributed and binomially distributed samples. */
  public void testRandomSamplesFromSameDistribution() {
    List<Double> normalDistSample1 =
        getRandomSample(new NormalDistribution(random, 5.0, 2.0));
    List<Double> normalDistSample2 =
        getRandomSample(new NormalDistribution(random, 5.0, 2.0));
    assertFalse(
        "This null hypothesis should not be easy to reject, as both samples are indeed generated by the same distribution.",
        test.executeTest(normalDistSample1, normalDistSample2) < MAXIMAL_P_VALUE);
  }

  /**
   * Tests the calculation of u-values with a concrete 'text-book' example.
   * Source: <a
   * href="http://de.wikipedia.org/wiki/Wilcoxon-Mann-Whitney-Test">Wikipedia
   * </a>.
   */
  public void testUCalculationWithConcreteExample() {
    checkRankCalculations(
        getListFor(0., 500., 600., 750., 800., 1000., 1100., 1500., 1800.,
            1900., 2000., 2200., 3500.), 151., 31.,
        getListFor(400., 550., 650., 900., 950., 1200., 1600.), 59., 60.);
  }

  /**
   * Test example from Sheskin's handbook (see {@link WilcoxonRankSumTest} for
   * citation, p. 516).
   */
  public void testExampleFromSheskinHandbook() {
    checkRankCalculations(getListFor(11., 1., 0., 2., 0.), 19., 21.,
        getListFor(11., 11., 5., 8., 4.), 36., 4.);
  }

  /**
   * Test example from Sheskin's handbook (see {@link WilcoxonRankSumTest} for
   * citation, p. 516).
   */
  public void testIdenticalNumberLists() {
    List<Double> list1 = new ArrayList<>();
    List<Double> list2 = new ArrayList<>();

    for (int i = 0; i < SAMPLE_SIZE_IDENTICAL_SAMPLES; i++) {
      list1.add(1.);
      list2.add(1.);
    }

    assertTrue("This test should not be rejected",
        test.executeTest(list1, list2) > MAXIMAL_P_VALUE);
  }

  /**
   * Check rank calculations.
   * 
   * @param sample1
   *          sample #1
   * @param expectedRankSumSample1
   *          the expected rank sum of sample #1
   * @param expectedUValueSample1
   *          the expected u-value of sample #1
   * @param sample2
   *          sample #2
   * @param expectedRankSumSample2
   *          the expected rank sum of sample #2
   * @param expectedUValueSample2
   *          the expected u-value of sample #2
   */
  private void checkRankCalculations(List<Double> sample1,
      double expectedRankSumSample1, double expectedUValueSample1,
      List<Double> sample2, double expectedRankSumSample2,
      double expectedUValueSample2) {
    Pair<Double, Double> rankSums = test.calculateRankSums(sample1, sample2);
    assertEquals(expectedRankSumSample1, rankSums.getFirstValue());
    assertEquals(expectedRankSumSample2, rankSums.getSecondValue());

    Pair<Double, Double> uValues = test.calculateUValues(sample1, sample2);
    assertEquals(expectedUValueSample1, uValues.getFirstValue());
    assertEquals(expectedUValueSample2, uValues.getSecondValue());
  }

  /**
   * Creates a list for the given values.
   * 
   * @param values
   *          the values
   * @return the list
   */
  private <T> List<T> getListFor(T... values) {
    List<T> sample = Arrays.asList(values);
    return sample;
  }

  /**
   * Checks test result of two samples (that should be generated by *differing*
   * distributions).
   * 
   * @param sample1
   *          the first sample
   * @param sample2
   *          the second sample
   */
  private void checkTestResult(List<Double> sample1, List<Double> sample2) {
    assertTrue("Test confidence should be smaller than 0.05",
        test.executeTest(sample1, sample2) < MAXIMAL_P_VALUE);
  }

  /**
   * Generates a random sample of size
   * {@link TestWilcoxonRankSumTest#SAMPLE_SIZE}.
   * 
   * @param distribution
   *          the distribution used to generate the sample
   * @return the random sample
   */
  private List<Double> getRandomSample(AbstractDistribution distribution) {
    List<Double> normalDistSample = new ArrayList<>();
    for (int i = 0; i < SAMPLE_SIZE; i++) {
      normalDistSample.add(distribution.getRandomNumber());
    }
    return normalDistSample;
  }

}
