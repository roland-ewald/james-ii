/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;


import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.entities.IProblemDefinition;


/**
 * Describes a single comparison job.
 * 
 * @author Roland Ewald
 */
public class ComparisonJob {

  /** The simulation problem to be re-evaluated. */
  private final IProblemDefinition problem;

  /** The map config_id => configuration. */
  private final Map<Long, ParameterBlock> configs;

  /** The comparison result. */
  private final ComparisonResult comparisonResult;

  /**
   * Instantiates a new comparison job.
   * 
   * @param checkConfigs
   *          the configurations to be checked
   * @param probID
   *          the problem id
   * @param rtcComp
   *          comparator of current runtime configurations (holds performance
   *          statistics)
   */
  public ComparisonJob(IProblemDefinition simProblem,
      Map<Long, ParameterBlock> checkConfigs, RTCComparator rtcComp) {
    problem = simProblem;
    configs = checkConfigs;
    comparisonResult = new ComparisonResult(rtcComp, configs);
  }

  /**
   * Gets the simulation problem.
   * 
   * @return the problem
   */
  public IProblemDefinition getProblem() {
    return problem;
  }

  /**
   * Gets the configurations.
   * 
   * @return the configurations
   */
  public Map<Long, ParameterBlock> getConfigs() {
    return configs;
  }

  /**
   * Gets the comparison result.
   * 
   * @return the comparison result
   */
  public ComparisonResult getComparisonResult() {
    return comparisonResult;
  }

}
