/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.experiments.optimization.ConfigurationInfos;
import org.jamesii.core.experiments.optimization.Optimizer;

/**
 * Simple class to be used by all representative-value calculations that only
 * rely on a single dimension.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractRepresentativeValueCalulator implements
    IRepresentativeValueOfObjectives {

  /** The constant serial version UID. */
  private static final long serialVersionUID = 4321984714992304563L;

  @Override
  public Map<String, Double> calcRepresentativeValue(
      ConfigurationInfos configInfos) {
    if (configInfos.containsInfinty()) {
      return Optimizer.createPositiveInfinityObjectives(configInfos
          .getRepresentativeObjectives().keySet());
    }
    Map<String, Double> result = new HashMap<>();
    for (String index : configInfos.getObjectives().get(0).keySet()) {
      List<Double> avgList = new ArrayList<>();
      for (Map<String, Double> objList : configInfos.getObjectives()) {
        avgList.add(objList.get(index));
      }
      result.put(index, calculateRepresentativeValue(avgList));
    }
    return result;
  }

  /**
   * Calculates the representative value for a list of objective values of the
   * same kind (= from the same objective function).
   * 
   * @param objectiveValues
   *          the list of objective values
   * @return the representative value
   */
  protected abstract Double calculateRepresentativeValue(
      List<Double> objectiveValues);

}
