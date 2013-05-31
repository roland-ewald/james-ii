/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate.bootstrapping.sampling;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.util.misc.Pair;

public class CaseResamplingBootstrapper implements IBootStrapper {

  @Override
  public List<Pair<Double, Double>> bootStrap(List<? extends Number> list,
      int repetitions, IRandom random) {
    List<Pair<Double, Double>> result = new ArrayList<>();
    int size = list.size();
    for (int i = 0; i < repetitions; i++) {
      Double mean = 0.0;
      Double variance = 0.0;
      for (int j = 0; j < size; j++) {
        double value = resample(list, random);
        variance = Variance.varianceAddEntry(variance, mean, value, j + 1);
        mean = ArithmeticMean.arithmeticMean(mean, value, j + 1);
      }
      insertValue(result, mean, variance);
    }
    return result;
  }

  protected double resample(List<? extends Number> list, IRandom random) {
    int r = random.nextInt(list.size());
    return list.get(r).doubleValue();
  }

  private void insertValue(List<Pair<Double, Double>> list, Double mean,
      Double variance) {
    int j = 0;
    for (j = 0; j < list.size(); j++) {
      Double listValue = list.get(j).getFirstValue();
      if (mean.compareTo(listValue) < 0) {
        break;
      }
    }
    list.add(j, new Pair<>(mean, variance));
  }

}
