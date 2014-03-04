/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;

/**
 * Auxiliary methods for predictor generators.
 * 
 * @author Roland Ewald
 * 
 */
public final class PredictorGenerators {

  /**
   * Should not be instantiated.
   */
  private PredictorGenerators() {
  }

  /**
   * Constructs a set of (distinct) configurations.
   * 
   * @param tuples
   *          list of performance tuples from which the set shall be constructed
   * @return set of distinct configurations
   */
  public static Set<Configuration> getDistinctConfigs(
      List<PerformanceTuple> tuples) {
    Set<Configuration> confSet = new HashSet<>();
    for (PerformanceTuple tuple : tuples) {
      confSet.add(tuple.getConfiguration());
    }
    return confSet;
  }

  /**
   * Segregates the training data-set into the different performance classes. To
   * distinguish n classes, one needs n-1 double values.
   * 
   * @param trainingData
   *          from which to derive discrete number of performance classes
   * @param classes
   *          number of desired classes
   * @author Kaustav Saha
   * @author Roland Ewald
   * @return performance levels as an array of size (classes - 1), which
   *         contains the thresholds in the training Data set where the
   *         categorisation is done
   */
  public static Double[] performanceLevels(
      List<? extends PerformanceTuple> trainingData, int classes) {
    int sizeData = trainingData.size();
    Double[] perfLevels = new Double[classes - 1];
    Double perfArray[] = new Double[sizeData];

    for (int i = 0; i < perfArray.length; i++) {
      PerformanceTuple tuple = trainingData.get(i);
      perfArray[i] = tuple.getPerformance();
    }
    Arrays.sort(perfArray);

    int dataposition = sizeData / classes;
    for (int i = 1; i < classes; i++) {
      perfLevels[i - 1] = perfArray[dataposition * i - 1];
    }
    return perfLevels;
  }

  /**
   * Classifies performance value for a given discretisation of the performance
   * space. Performance borders are interpreted as inclusive lower boundaries,
   * e.g. having performance borders <1;2;3>, there are four classes (enumerated
   * 0 - 3) to be distinguished: [-inf,1), [1,2), [2,3), [3,inf].
   * 
   * @param perfBorders
   *          array with performance borders (needs to be sorted!)
   * @param perf
   *          performance to be classified
   * @return number of class
   */
  public static int classifyPerformance(Double[] perfBorders, Double perf) {
    for (int j = 0; j < perfBorders.length; j++) {
      if (perf < perfBorders[j]) {
        return j;
      }
    }
    return perfBorders.length;
  }

}
