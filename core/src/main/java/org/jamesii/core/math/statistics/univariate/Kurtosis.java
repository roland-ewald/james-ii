/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

/**
 * The Class Kurtosis.
 * 
 * 
 * FIXME seems not to be fine. This class is broken and should not be used!
 * 
 * 
 * Created on 03.03.2005
 * 
 * @author Jan Pommerenke
 */
public final class Kurtosis {

  /**
   * Hidden constructor.
   */
  private Kurtosis() {
  }

  /**
   * This Method likens normal distribution with incoming distribution on
   * kurtosis (flat or steep). It is only for distribution with one bow. at
   * normal distribution is y = 3 => kurtosis := y - 3 at
   * 
   * <p>
   * <b>FIXME</b> seems not to be fine. This class is <b>broken and should not
   * be used</b>!
   * </p>
   * 
   * @param x
   *          array of integer Values
   * 
   * 
   * @return kurt < 0 => flat kurtosis kurt = 0 => normal kurtosis kurt > 0 =>
   *         steep kurtosis
   */
  public static double kurtosis(int[] x) {
    int n = x.length;
    double z = 0;
    double y = 0;
    double kurt = 0;
    double help = 0;
    double aM = ArithmeticMean.arithmeticMean(x);
    for (int i = 0; i < n; i++) {
      help = x[i] - aM;
      z = z + (help * help * help * help);
    }
    y = z / n;
    help = Variance.variance(x);
    y = y / (help * help * help * help);
    kurt = y - 3;
    return kurt;
  }

}
