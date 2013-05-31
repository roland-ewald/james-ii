/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.cancellation;

import java.util.Map;

import org.jamesii.core.experiments.optimization.OptimizationStatistics;
import org.jamesii.core.experiments.optimization.parameter.representativeValue.IRepresentativeValuesComparator;

/**
 * Stops optimization if the desired level of the object function has been
 * reached.
 * 
 * @author Arvid Schwecke
 */
public class GoalObjectiveReached implements ICancelOptimizationCriterion {

  /** Desired value of objective function. */
  private Map<String, Double> goalObjectives;

  private final IRepresentativeValuesComparator repValueComparator;

  /**
   * The Constructor.
   * 
   * @param finalObjectiveLevels
   *          the final objective levels (per optimization function)
   * @param comparator
   *          comparator for representative objective values
   */
  public GoalObjectiveReached(Map<String, Double> finalObjectiveLevels,
      IRepresentativeValuesComparator comparator) {
    super();
    this.goalObjectives = finalObjectiveLevels;
    repValueComparator = comparator;
  }

  @Override
  public boolean meetsCancelCriterion(OptimizationStatistics state) {
    return repValueComparator.compare(state.getLastConfigurationResults()
        .getRepresentativeObjectives(), goalObjectives) < 0;
  }

}
