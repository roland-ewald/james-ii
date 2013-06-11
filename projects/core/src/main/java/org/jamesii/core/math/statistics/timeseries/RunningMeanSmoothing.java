/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.statistics.univariate.ArithmeticMean;

/**
 * Class to smooth a time series using a running mean.
 * 
 * @author Stefan Leye
 * 
 */
public final class RunningMeanSmoothing {

  /**
   * Hidden constructor.
   */
  private RunningMeanSmoothing() {
  }

  public static List<Double> smoothing(List<? extends Number> timeSeries,
      double neighbourhood) {
    List<Double> result = new ArrayList<>();
    int nLength = (int) (timeSeries.size() * neighbourhood / 2);
    int tLength = timeSeries.size();
    int currentPos = 0;
    Double runningMean = 0.0;
    int upperBound = 0;
    int lowerBound = 0;
    int currentLength = 0;
    for (int i = 0; i < nLength && i < tLength; i++) {
      Double currentValue = timeSeries.get(i).doubleValue();
      upperBound++;
      currentLength++;
      runningMean =
          ArithmeticMean.arithmeticMean(runningMean, currentValue, i + 1);
    }
    for (currentPos = 0; currentPos < nLength && currentPos < tLength; currentPos++) {
      Double newValue = timeSeries.get(upperBound).doubleValue();
      upperBound++;
      currentLength++;
      runningMean =
          ArithmeticMean.arithmeticMean(runningMean, newValue, currentLength);
      result.add(runningMean);
    }
    for (; currentPos < tLength; currentPos++) {
      runningMean *= currentLength;
      if (upperBound < timeSeries.size()) {
        Double newValue = timeSeries.get(upperBound).doubleValue();
        upperBound++;
        runningMean += newValue;
      }
      Double oldValue = timeSeries.get(lowerBound).doubleValue();
      lowerBound++;
      runningMean -= oldValue;
      runningMean /= currentLength;
      result.add(runningMean);
    }
    return result;
  }

}
