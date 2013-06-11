/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.timeseries;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Triple;

/**
 * Class to calculate different distances from the mean of a trajectory.
 * 
 * @author Stefan Leye
 */
public final class BiasDetection {

  /**
   * Hidden constructor.
   */
  private BiasDetection() {
  }

  public static Pair<Triple<Integer, Integer, Integer>, Triple<Double, Double, Double>> biasDetection(
      List<? extends Number> timeSeries) {
    double mean = Variance.varianceAndAM(timeSeries).getSecondValue();
    int negCounter = 0;
    int posCounter = 0;
    double maxNegDistToMean = 0;
    double maxPosDistToMean = 0;
    for (Number num : timeSeries) {
      Double doub = num.doubleValue();
      Double dist = doub - mean;
      if (dist.compareTo(0.0) > 0) {
        posCounter++;
        if (dist.compareTo(maxPosDistToMean) > 0) {
          maxPosDistToMean = dist;
        }
      } else if (dist.compareTo(0.0) < 0) {
        negCounter++;
        if (dist.compareTo(maxNegDistToMean) < 0) {
          maxNegDistToMean = dist;
        }
      }
    }
    double maxAbsDist = Math.max(maxPosDistToMean, -maxNegDistToMean);
    int negPosDiff = posCounter - negCounter;
    Triple<Integer, Integer, Integer> counter =
        new Triple<>(posCounter, negCounter, negPosDiff);
    Triple<Double, Double, Double> distances =
        new Triple<>(maxPosDistToMean, maxNegDistToMean, maxAbsDist);
    return new Pair<>(counter, distances);
  }
}
