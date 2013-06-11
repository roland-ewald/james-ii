/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests.wilcoxon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jamesii.core.math.statistics.tests.IPairedTest;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;

/**
 * Implements a Wilcoxon rank-sum test (also known as Mann–Whitney U test, or
 * Mann–Whitney–Wilcoxon test) for two independent variates.
 * 
 * It tests whether two independent samples were drawn from populations with
 * different median values (see Sheskin, 'Handbook of Parametric and
 * Non-Parametric Tests', 4th ed., ch. 12 [p 513] - ISBN: 978-1584888147).
 * 
 * Per default, this test will correct for ties and continuity in the
 * calculation of z-values, so in comparison with other software the z-values
 * may differ slightly (but: you can switch this off, and the u-statistic is
 * still identical).
 * 
 * @author Roland Ewald
 * @author Georg Straube
 * @author Simon Bartels
 */
public class WilcoxonRankSumTest implements IPairedTest {

  /** The list of all ranks (for both samples). */
  private List<Double> ranks = new ArrayList<>();

  /**
   * The list of all tie groups (each element denotes the number of equal
   * elements in a group).
   */
  private List<Integer> ties = new ArrayList<>();

  /**
   * The flag to apply a correction for continuity (as a discrete rank
   * distribution is approximated by a continuous normal distribution) during
   * the calculation of the z-value. See Sheskin's handbook p. 520 (eq. 12. 5)
   * and p. 232 for details.
   */
  private boolean correctingForContinuity = true;

  /**
   * The flag to apply a correction for ties, as a large number of tied ranks
   * may introduce bias. See Sheskin's handbook p. 521 (eq. 12.6).
   */
  private boolean correctingForTies = true;

  /** The size of the first sample. */
  private double size1 = 0;

  /** The size of the second sample. */
  private double size2 = 0;

  @Override
  public double executeTest(List<? extends Number> sample1,
      List<? extends Number> sample2) {

    ranks.clear();
    ties.clear();
    size1 = sample1.size();
    size2 = sample2.size();

    if (size1 < 20 || size2 < 20) {
      throw new IllegalArgumentException(
          "This test does not rely on tabulated data for the U-statistic in case of small sample values (< 20 sample values), so it should only be used with sufficiently many sample data.");
    }

    Pair<Double, Double> uValues = calculateUValues(sample1, sample2);

    // For the two-sample test, we need the minimal U value
    double zValue =
        calculateZValue(Math.min(uValues.getFirstValue(),
            uValues.getSecondValue()));

    return calculateFinalResult(sample1, sample2, zValue);
  }

  /**
   * Calculates the final results of the test. Warns the user in case of
   * 'strange' results (+/-Inf, NaN), unless both samples contain exactly the
   * same number (if this is the case, zValue seems to approach one).
   * 
   * @param sample1
   *          the first sample
   * @param sample2
   *          the second sample
   * @param zValue
   *          the z-value
   * @return the p-value
   */
  @SuppressWarnings("unchecked")
  // samplesContainSameSingleNumber only requires list elements of type Number
  private double calculateFinalResult(List<? extends Number> sample1,
      List<? extends Number> sample2, double zValue) {

    // Checks corner case: zValue is not properly defined in case both lists
    // contain a single (identical) number
    if (Double.isInfinite(zValue) || Double.isNaN(zValue)) {
      if (samplesContainSameSingleNumber(sample1, sample2)) {
        return calculatePVal(1.);
      } else {
        throw new IllegalArgumentException(
            "Numerical problems detected with inputs '"
                + Strings.dispIterable(sample1) + "' and '"
                + Strings.dispIterable(sample2) + "'. Z-value is " + zValue
                + ", returning p-value of 1.");
      }
    }

    // Calculate probability value and thus the confidence in the null
    // hypothesis (using a conversion to standard normal distribution)
    return calculatePVal(zValue);
  }

  /**
   * Checks whether the samples only consist of a single number. In other words,
   * all numbers have to be equal to each other.
   * 
   * @param samples
   *          the samples
   * @return true, if successful
   */
  private boolean samplesContainSameSingleNumber(
      List<? extends Number>... samples) {
    Number singleNumber = samples[0].get(0);
    for (List<? extends Number> sample : samples) {
      for (Number sampleElement : sample) {
        if (!singleNumber.equals(sampleElement)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Calculate z-value using an approximation formula.
   * 
   * @param uValue
   *          the u-value
   * @return the z-value
   */
  private double calculateZValue(double uValue) {
    double nominator = continuityCorrection(uValue - size1 * size2 / 2.);
    double denominator =
        Math.sqrt(((size1 * size2 * (size1 + size2 + 1)) / 12)
            - calculateTieCorrection());
    return nominator / denominator;
  }

  /**
   * Continuity correction. See
   * {@link WilcoxonRankSumTest#correctingForContinuity}.
   * 
   * @param nominator
   *          the nominator of the z-value calculation
   * @return the corrected nominator, or the original one if continuity
   *         correction is diabled
   */
  private double continuityCorrection(double nominator) {
    if (!correctingForContinuity) {
      return nominator;
    }
    return Math.abs(nominator) - .5;
  }

  /**
   * Calculates tie correction. See
   * {@link WilcoxonRankSumTest#correctingForTies}.
   * 
   * @return the correction, 0 if tie correction is disabled
   */
  private double calculateTieCorrection() {
    double result = 0.;
    if (correctingForTies) {

      double tieSum = 0.;

      // Sum over all ties, cast to double prevents int-overflow in expression
      for (Integer tieGroupSize : ties) {
        tieSum += tieGroupSize * (tieGroupSize * (double) tieGroupSize - 1);
      }

      result =
          (size1 * size2 * tieSum)
              / (12 * (size1 + size2) * (size1 + size2 - 1));
    }
    return result;
  }

  /**
   * Gets the u values for the two samples. See eq. 12.1 and 12.2 in Sheskin's
   * handbook (see class documentation above).
   * 
   * @param sample1
   *          sample #1
   * @param sample2
   *          sample #2
   * @return the tuple of U values
   */
  protected Pair<Double, Double> calculateUValues(
      List<? extends Number> sample1, List<? extends Number> sample2) {

    Pair<Double, Double> rankSums = calculateRankSums(sample1, sample2);

    double n1 = sample1.size();
    double n2 = sample2.size();

    return new Pair<>(n1 * n2 + n1 * (n1 + 1) / 2 - rankSums.getFirstValue(),
        n1 * n2 + n2 * (n2 + 1) / 2 - rankSums.getSecondValue());
  }

  /**
   * Calculates rank sums.
   * 
   * @param sample1
   *          sample #1
   * @param sample2
   *          sample #2
   * @return the tuple (rank sum sample #1, rank sum sample #2)
   */
  protected Pair<Double, Double> calculateRankSums(
      List<? extends Number> sample1, List<? extends Number> sample2) {
    List<Pair<Boolean, Double>> overallData =
        mergeAndSortSamples(sample1, sample2);
    calculateRanks(overallData);
    return sumRanks(overallData);
  }

  /**
   * Calculate sum of ranks for both samples.
   * 
   * @param mergedSampleData
   *          the merged sample data
   * @return the rank sums
   */
  protected Pair<Double, Double> sumRanks(
      List<Pair<Boolean, Double>> mergedSampleData) {
    double rankSumSample1 = 0.;
    double rankSumSample2 = 0.;
    for (int i = 0; i < mergedSampleData.size(); i++) {
      double rank = ranks.get(i);
      if (mergedSampleData.get(i).getFirstValue()) {
        rankSumSample2 += rank;
      } else {
        rankSumSample1 += rank;
      }
    }
    return new Pair<>(rankSumSample1, rankSumSample2);
  }

  /**
   * Transforms both samples (first sample: flag false, second sample: flag
   * true) in a sorted list of tuples (sample_one?,value).
   * 
   * @param sample1
   *          sample #1
   * @param sample2
   *          the sample #2
   * @return list of tuples
   */
  private List<Pair<Boolean, Double>> mergeAndSortSamples(
      List<? extends Number> sample1, List<? extends Number> sample2) {
    List<Pair<Boolean, Double>> overallData = new ArrayList<>();

    overallData.addAll(generateListElements(false, sample1));
    overallData.addAll(generateListElements(true, sample2));

    Collections.sort(overallData, new Comparator<Pair<Boolean, Double>>() {
      @Override
      public int compare(Pair<Boolean, Double> o1, Pair<Boolean, Double> o2) {
        return Double.compare(o1.getSecondValue(), o2.getSecondValue());
      }
    });
    return overallData;
  }

  /**
   * Calculate ranks of the given pairs.
   * 
   * @param mergedSample
   *          the overall data
   * @return the list of ranks
   */
  private void calculateRanks(List<Pair<Boolean, Double>> mergedSample) {
    int startIndex = 0;
    while (startIndex < mergedSample.size()) {
      int nextHigherIndex = startIndex + 1;
      while (nextHigherIndex < mergedSample.size()
          && mergedSample.get(startIndex).getSecondValue()
              .equals(mergedSample.get(nextHigherIndex).getSecondValue())) {
        nextHigherIndex++;
      }
      int sizeOfTieGroup = nextHigherIndex - startIndex;
      if (sizeOfTieGroup > 1) {
        ties.add(sizeOfTieGroup);
      }
      double rank = (1 + startIndex) + (sizeOfTieGroup - 1) / 2.0;
      for (int i = startIndex; i < nextHigherIndex; i++) {
        ranks.add(rank);
      }
      startIndex = nextHigherIndex;
    }
  }

  /**
   * Generates the list elements.
   * 
   * @param flagValue
   *          the boolean value of the first element in each pair
   * @param sample
   *          the list from which the second elements will be generated
   * @return a list of tuples: (flagValue, sample-elem1), (flagValue,
   *         sample-elem2), ...
   */
  private Collection<? extends Pair<Boolean, Double>> generateListElements(
      boolean flagValue, List<? extends Number> sample) {
    List<Pair<Boolean, Double>> result = new ArrayList<>();
    for (Number sampleValue : sample) {
      result.add(new Pair<>(flagValue, sampleValue.doubleValue()));
    }
    return result;
  }

  /**
   * Calculates the standard normal probability of z.
   * 
   * @param zVal
   *          the z-value
   * 
   * @return the p-value
   */
  private static double calculatePVal(double zVal) {
    double zValue = Math.abs(zVal);
    double a1 = 0.0000053830;
    double a2 = 0.0000488906;
    double a3 = 0.0000380036;
    double a4 = 0.0032776263;
    double a5 = 0.0211410061;
    double a6 = 0.0498673470;
    double pVal =
        (((((a1 * zValue + a2) * zValue + a3) * zValue + a4) * zValue + a5)
            * zValue + a6)
            * zValue + 1;
    pVal = Math.pow(pVal, -16);
    return pVal;
  }

  /**
   * Checks if test is correcting for continuity.
   * 
   * @return true, if it is correcting for continuity
   */
  public boolean isCorrectingForContinuity() {
    return correctingForContinuity;
  }

  /**
   * Checks if test is correcting for ties.
   * 
   * @return true, if it is correcting for ties
   */
  public boolean isCorrectingForTies() {
    return correctingForTies;
  }

  /**
   * Sets the flag w.r.t. correcting for continuity.
   * 
   * @param correctingForContinuity
   *          the new flag value
   */
  public void setCorrectingForContinuity(boolean correctingForContinuity) {
    this.correctingForContinuity = correctingForContinuity;
  }

  /**
   * Sets the flag w.r.t. correcting for ties.
   * 
   * @param correctingForTies
   *          the new flag value
   */
  public void setCorrectingForTies(boolean correctingForTies) {
    this.correctingForTies = correctingForTies;
  }

}
