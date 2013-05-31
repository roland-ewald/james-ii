/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries;

import java.util.List;

/**
 * Class to test the absence of autocorrelation in a time-series.
 * 
 * @author Stefan Leye
 */
public final class PortmanteauTest {

  /**
   * Hidden constructor.
   */
  private PortmanteauTest() {
  }

  /**
   * Returns the Q-statistic for a given time-series, according to the
   * Box-Pierce test. Should be used for time-series of length > 100.
   * 
   * @see Box, G. E. P. and Pierce, D. A. (1970), Distribution of residual
   *      correlations in autoregressive-integrated moving average time series
   *      models. Journal of the American Statistical Association, 65,
   *      1509–1526.
   * 
   * @param timeSeries
   *          the time-series
   * @param k
   *          the depth of the autocorrelation
   * @return the Q-statistic
   */
  public static double getBoxPierceQStatistic(
      List<? extends Number> timeSeries, double k) {
    double result = 0.0;
    for (int lag = 0; lag < k; lag++) {
      result += AutoCorrelation.autoCorrelation(timeSeries, lag);
    }
    return result * timeSeries.size();
  }

  /**
   * Returns the Q-statistic for a given time-series, according to the Ljung-Box
   * test. Should be used for time-series of length < 100.
   * 
   * @see Ljung, G.M.; Box, G.E.P.: On a Measure of Lack of Fit in Time Series
   *      Models; Biometrika 65, Nr. 2, 297-303, 1978
   * 
   * @param timeSeries
   *          the time-series
   * @param k
   *          the depth of the autocorrelation
   * @return the Q-statistic
   */
  public static double getLjungBoxQStatistic(List<Number> timeSeries, double k) {
    double result = 0.0;
    int size = timeSeries.size();
    for (int lag = 0; lag < k; lag++) {
      result += AutoCorrelation.autoCorrelation(timeSeries, lag) / (size - lag);
    }
    return result * size * (size + 2);
  }

}
