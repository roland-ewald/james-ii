/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import java.util.List;

import org.jamesii.core.experiments.optimization.ConfigurationInfos;
import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * 
 * @author Peter Sievert
 */
public class MovingAverageImprovements implements ICancelOptimizationCriterion {

  private int length_of_moving_average;

  private int counter;

  private double leastImprovement;

  private boolean relativeMeasure;

  private double ma;

  public MovingAverageImprovements() {
    length_of_moving_average = 10;
    leastImprovement = 0.01;
    relativeMeasure = true;
    ma = 0.0;
  }

  private ConfigurationInfos getBestSoFar(List<ConfigurationInfos> list, int pos) {
    if (list.size() == 0) {
      return null;
    }

    int best = 0;
    int i = 0;
    double bestValue = list.get(0).getRepresentativeObjectives().get(0);
    while (i < pos) {
      if (list.get(i).getRepresentativeObjectives().get(0).compareTo(bestValue) < 0) {
        best = i;
        bestValue = list.get(i).getRepresentativeObjectives().get(0);
      }
      i++;
    }

    return list.get(best);
  }

  public MovingAverageImprovements(int length, double _leastImprovement,
      boolean _relativeMeasure) {
    length_of_moving_average = length;
    this.leastImprovement = _leastImprovement;
    this.relativeMeasure = _relativeMeasure;
    ma = 0.0;
    counter = 1;
  }

  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {

    if (state.getTotalConfigurationRuns() <= counter) {
      return false;
    }

    while (counter < state.getTotalConfigurationRuns()) {
      double improvement = 0;

      if (relativeMeasure) {
        improvement =
            getBestSoFar(state.getEvaluatedSolutions(), counter - 1)
                .getRepresentativeObjectives().get(0)
                / state.getEvaluatedSolutions().get(counter)
                    .getRepresentativeObjectives().get(0) - 1;
      } else {
        improvement =
            getBestSoFar(state.getEvaluatedSolutions(), counter - 1)
                .getRepresentativeObjectives().get(0)
                - state.getEvaluatedSolutions().get(counter)
                    .getRepresentativeObjectives().get(0);
      }

      if (improvement < 0) {
        improvement = 0;
      }

      if (ma == 0.0) {
        ma = improvement;
      } else {
        ma = (improvement - ma) * 2 / (1 + length_of_moving_average) + ma;
      }

      counter++;
    }

    if (state.getTotalConfigurationRuns() < length_of_moving_average) {
      return false;
    }

    if (ma < this.leastImprovement) {
      return true;
    }

    return false;
  }

}
