/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.Entity;
import org.jamesii.core.experiments.replication.RepNumberCriterionFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.steering.IExperimentSteerer;
import org.jamesii.core.experiments.steering.VariablesAssignment;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * Simple test implementation of {@link IExperimentSteerer}.
 * 
 * @author Roland Ewald
 */
public class SimpleExperimentSteerer extends Entity implements
    IExperimentSteerer {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3217962702666448999L;

  /** Flag to remember if steerer was just initialised. */
  boolean freshlyInitialized = false;

  /** Number of variable assignments to be iterated. */
  final int counter;

  /** Number of replications to be scheduled for variable assignment. */
  final int replications;

  /** Current count of variable assignments. */
  int currentCount = 0;

  /**
   * Instantiates a new simple experiment steerer.
   */
  public SimpleExperimentSteerer() {
    counter = 1;
    replications = 1;
  }

  /**
   * Instantiates a new simple experiment steerer.
   * 
   * @param assignmentCount
   *          the assignment count
   * @param replicationsPerVarAssignment
   *          the number of replications per variable assignment
   */
  public SimpleExperimentSteerer(int assignmentCount,
      int replicationsPerVarAssignment) {
    counter = assignmentCount;
    replications = replicationsPerVarAssignment;
  }

  @Override
  public ParameterBlock getExperimentParameters() {
    ParameterBlock result = new ParameterBlock();
    List<ParameterizedFactory<RepCriterionFactory>> repCritFactoryList =
        new ArrayList<>();
    repCritFactoryList.add(new ParameterizedFactory<RepCriterionFactory>(
        new RepNumberCriterionFactory(), new ParameterBlock().addSubBl(
            RepNumberCriterionFactory.NUM_REPS, replications)));
    result.addSubBlock(ExperimentVariables.REPLICATION_CRITERION_FACTORIES,
        repCritFactoryList);
    return result;
  }

  @Override
  public boolean allowSubStructures() {
    return true;
  }

  @Override
  public VariablesAssignment getNextVariableAssignment() {
    if (freshlyInitialized) {
      freshlyInitialized = false;
      currentCount = counter;
    }
    if (currentCount > 0) {
      currentCount--;
      VariablesAssignment va = new VariablesAssignment();
      va.put("steerer_" + hashCode(), currentCount);
      return va;
    }
    return null;
  }

  @Override
  public void init() {
    freshlyInitialized = true;
  }

  @Override
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInformation) {
  }

  @Override
  public boolean isFinished() {
    return currentCount == 0;
  }

}
