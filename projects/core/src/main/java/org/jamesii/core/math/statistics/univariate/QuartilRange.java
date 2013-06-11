/**
 * Title:        CoSA: QuartilRange
 * Description:
 * Copyright:    Copyright (c) 2005
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 *               Created on 16.02.2005
 * @author       Jan Pommerenke
 * @version      1.0

 */

package org.jamesii.core.math.statistics.univariate;

/**
 * The Class QuartilRange.
 */
public final class QuartilRange {

  /**
   * Hidden constructor.
   */
  private QuartilRange() {
  }

  /**
   * This method needs sorted int-Values not Intervals.
   * 
   * @param x
   *          array of int values
   * 
   * @return the double
   */
  public static double quartilRangeNumber(int[] x) {
    int y = x.length;
    double h = 0;
    double q1 = 0;
    double q3 = 0;
    int[] z;
    int[] u;
    if (y % 2 == 0) {
      z = new int[y / 2];

      System.arraycopy(x, 0, z, 0, y / 2 - 1);

      // for (int i = 0; i < (y / 2) - 1; i++) {
      // z[i] = x[i];
      // }
      q1 = Median.median(z);
      u = new int[y / 2];
      for (int j = 0; j < (y / 2) - 1; j++) {
        for (int i = y / 2; i < y - 1; i++) {
          u[j] = x[i];
        }
      }
      q3 = Median.median(u);
      h = q3 - q1;
    } else {
      z = new int[(y + 1) / 2];
      System.arraycopy(x, 0, z, 0, (y + 1) / 2 - 1);
      // for (int i = 0; i < ((y + 1) / 2) - 1; i++) {
      // z[i] = x[i];
      // }
      q1 = Median.median(z);
      u = new int[(y - 1) / 2 - 1];
      for (int j = 0; j < ((y - 1) / 2) - 1; j++) {
        for (int i = (y - 1) / 2 + 1; i < y - 1; i++) {
          u[j] = x[i];
        }
      }
      q3 = Median.median(u);
      h = q3 - q1;
    }
    return h;
  }
}
