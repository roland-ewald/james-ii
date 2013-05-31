/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;

/**
 * 
 * @author
 * 
 */
public class FailuresInARow implements ICancelOptimizationCriterion {

  private int numberOfFailures;

  public FailuresInARow() {
    numberOfFailures = 100;
  }

  public FailuresInARow(int number) {
    numberOfFailures = number;
  }

  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {

    if (state.getTotalConfigurationRuns() < numberOfFailures) {
      return false;
    }

    for (int i = state.getEvaluatedSolutions().size() - 1; i >= state
        .getEvaluatedSolutions().size() - numberOfFailures; i--) {
      if (state
          .getEvaluatedSolutions()
          .get(i)
          .getRepresentativeObjectives()
          .get(0)
          .compareTo(
              state.getBestConfigs().get(state.getBestConfigs().size() - 1)
                  .getRepresentativeObjectives().get(0)) <= 0) {
        return false;
      }
    }

    return true;
  }

}
