/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

import java.util.Collection;

/**
 * Static methods to calculate mean orientation and angular deviation (a.k.a.
 * orientation dispersion) of a set of line segments of possibly different
 * lengths and orientations (CHECK: only half-circle input allowed?).
 * 
 * The angular deviation is related to the standard deviation of a
 * length-weighted von Mises distribution. See
 * 
 * H. Birkholz: Mathematical Methods for the Quantiﬁcation of Actin-Filaments in
 * Microscopic Images. PhD Thesis, University of Rostock, 2012,
 * 
 * whose calculations in turn are based on
 * 
 * J.P. Marquez: Fourier analysis and automated measurement of cell and fiber
 * angular orientation distributions. International Journal of Solids and
 * Structures 43 (2006) 6413–6423
 * 
 * @author Arne Bittig
 * @date 2014-04-26
 */
public class Orientation {

  /**
   * Mean orientation of line segments given as collection of length-angle pairs
   * (in a double array)
   * 
   * @param lengthsAndAngles
   *          Line segment length and angle pairs
   * @return Mean orientation (in rad)
   */
  public static double meanOrientation(Collection<double[]> lengthsAndAngles) {
    double sin2mu = 0;
    double cos2mu = 0;
    for (double[] la : lengthsAndAngles) {
      assert la.length == 2.;
      double length = la[0];
      double twoPhi = la[1] * 2.;
      sin2mu += length * Math.sin(twoPhi);
      cos2mu += length * Math.cos(twoPhi);
    }
    return Math.atan2(sin2mu, cos2mu) / 2.;
  }

  /**
   * 
   * @param lengthsAndAngles
   *          Line segment length and angle pairs
   * @param mean
   *          Mean orientation (i.e. result of
   *          {@link #meanOrientation(Collection)})
   * @return Angular deviation ([0...0.5])
   */
  public static double angularDeviation(Collection<double[]> lengthsAndAngles,
      double mean) {
    double sum = 0;
    double totalLength = 0.;
    for (double[] la : lengthsAndAngles) {
      assert la.length == 2;
      double length = la[0];
      totalLength += length;
      sum += length * (1 - Math.cos((la[1] - mean) * 2));
    }
    return Math.sqrt(sum / totalLength) / 2;
  }
}
