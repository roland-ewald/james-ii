/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.Arrays;
import java.util.List;

/**
 * The Class Analysis. Computes several statistical measurements at once. If you
 * need more than a single value this will be most often the best choice,
 * because the computational load should be lower as by using the standalone
 * methods. <br>
 * NOTE UNTESTED - please test before using <br>
 * there seems to be some serious flaws with the computation rules used. FIXME
 * !!
 * 
 * http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Va._Higher-
 * order_statistics
 * 
 * @author Jan Himmelspach
 */
public class Analysis {

  /**
   * The Class StatResult.
   */
  public class StatResult {

    /**
     * Get the value of the min.
     * 
     * @return the min
     */
    public final double getMin() {
      return min;
    }

    /**
     * Set the min to the value passed via the min attribute.
     * 
     * @param min
     *          the min to set
     */
    public final void setMin(double min) {
      this.min = min;
    }

    /**
     * Get the value of the max.
     * 
     * @return the max
     */
    public final double getMax() {
      return max;
    }

    /**
     * Set the max to the value passed via the max attribute.
     * 
     * @param max
     *          the max to set
     */
    public final void setMax(double max) {
      this.max = max;
    }

    /** The mean. */
    private double mean = 0.;

    /** The median. */
    private double median = 0.;

    /** The range. */
    private double range = 0.;

    private double min = Double.POSITIVE_INFINITY;

    private double max = Double.NEGATIVE_INFINITY;

    /** The ad. */
    private double ad = 0.;

    /** The stddev. */
    private double stddev = 0.;

    /** The variance. */
    private double variance = 0.;

    /** The skewness. */
    private double skewness;

    /** The kurtosis. */
    private double kurtosis;

    /**
     * @return the mean
     */
    public final double getMean() {
      return mean;
    }

    /**
     * @param mean
     *          the mean to set
     */
    public final void setMean(double mean) {
      this.mean = mean;
    }

    /**
     * @return the median
     */
    public final double getMedian() {
      return median;
    }

    /**
     * @param median
     *          the median to set
     */
    public final void setMedian(double median) {
      this.median = median;
    }

    /**
     * @return the range
     */
    public final double getRange() {
      return range;
    }

    /**
     * @param range
     *          the range to set
     */
    public final void setRange(double range) {
      this.range = range;
    }

    /**
     * @return the ad
     */
    public final double getAd() {
      return ad;
    }

    /**
     * @param ad
     *          the ad to set
     */
    public final void setAd(double ad) {
      this.ad = ad;
    }

    /**
     * @return the stddev
     */
    public final double getStddev() {
      return stddev;
    }

    /**
     * @param stddev
     *          the stddev to set
     */
    public final void setStddev(double stddev) {
      this.stddev = stddev;
    }

    /**
     * @return the variance
     */
    public final double getVariance() {
      return variance;
    }

    /**
     * @param variance
     *          the variance to set
     */
    public final void setVariance(double variance) {
      this.variance = variance;
    }

    /**
     * @return the skewness
     */
    public final double getSkewness() {
      return skewness;
    }

    /**
     * @param skewness
     *          the skewness to set
     */
    public final void setSkewness(double skewness) {
      this.skewness = skewness;
    }

    /**
     * @return the kurtosis
     */
    public final double getKurtosis() {
      return kurtosis;
    }

    /**
     * @param kurtosis
     *          the kurtosis to set
     */
    public final void setKurtosis(double kurtosis) {
      this.kurtosis = kurtosis;
    }

  }

  public StatResult compute(List<Double> x) {
    double[] x2 = new double[x.size()];
    int c = 0;
    for (double d : x) {
      x2[c] = d;
      c++;
    }
    return compute(x2);
  }

  /**
   * Compute measures for passed array. Array will be sorted first.
   * 
   * @param x
   *          the x
   * 
   * @return the stat result
   */
  public StatResult compute(double[] x) {
    StatResult result = new StatResult();

    // sorting is useful here (we should start adding small numbers, range
    // computation, median ...)
    Arrays.sort(x);

    // #range
    result.range = x[x.length - 1] - x[0];

    // #median
    if (x.length % 2 == 0) { // even
      result.median = (x[x.length / 2 - 1] + x[x.length / 2]) / 2;
    } else { // odd
      result.median = x[(x.length + 1) / 2 - 1];
    }

    // we first need to compute the arithmetic mean (req. for all following
    // values)
    for (int i = 0; i < x.length; i++) {
      // arithmetic mean
      result.mean += x[i];
    }

    // #arithmetic mean
    result.mean /= x.length;

    for (int i = 0; i < x.length; i++) {
      // average deviation
      result.ad += Math.abs(x[i] - result.mean);

      // variance, skewness, kurtosis
      // compute the difference of the current value to the arithmetic mean
      double help = x[i] - result.mean;

      if (Double.compare(x[i], result.max) > 0) {
        result.max = x[i];
      }

      if (Double.compare(x[i], result.min) < 0) {
        result.min = x[i];
      }

      // compute the base for the variance
      double help2 = help * help;
      result.variance += help2;

      // compute the base for the skewness
      double help3 = help2 * help;
      result.skewness += help3;

      // compute the base for the kurtosis
      double help4 = help3 * help;
      result.kurtosis += help4;
    }

    // #average deviation
    result.ad /= x.length;

    // #variance
    result.variance /= (x.length - 1);

    double var3 = Math.pow(Variance.variance(x), 3);

    // #skewness
    result.skewness /= x.length;
    result.skewness /= var3;

    // #kurtosis
    result.kurtosis /= x.length;
    result.kurtosis /= (var3 * result.variance);
    result.kurtosis -= 3;

    // #stddev
    result.stddev = Math.sqrt(result.variance);

    return result;
  }

}
