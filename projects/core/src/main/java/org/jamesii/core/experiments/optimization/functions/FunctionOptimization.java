/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.optimization.Optimizer;
import org.jamesii.core.experiments.optimization.algorithm.IOptimizationAlgorithm;
import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.optimization.parameter.IOptimizationObjective;
import org.jamesii.core.experiments.optimization.parameter.OptimizationProblemDefinition;
import org.jamesii.core.experiments.optimization.parameter.cancellation.ICancelOptimizationCriterion;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObsModelInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObsSimInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.representativeValue.ArithmeticMeanOfObjectives;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.model.variables.BaseVariable;

/**
 * Simple class to allow the use of the simulation-based optimisers *without*
 * any simulation (giving them a simple objective function instead). This should
 * be quite easy, but isn't due to the structure of the optimisation framework.
 * The best option would probably be to re-design the optimiser class so that
 * the actual optimisation methods are not concerned with simulation anymore.
 * 
 * @author Roland Ewald
 */
public class FunctionOptimization {

  /** The problem definition. */
  private final OptimizationProblemDefinition problemDef;

  /** The pareto front that has been found (by the last execution). */
  private Map<Configuration, Map<String, Double>> paretoFront;

  /**
   * Instantiates a new function optimization.
   * 
   * @param functionToBeOptimized
   *          the function to be optimized
   * @param cancelCriterion
   *          the cancel criterion
   * @param startConfigs
   *          the start configs
   */
  public FunctionOptimization(
      final List<IOptimizationFunction> functionsToBeOptimized,
      ICancelOptimizationCriterion cancelCriterion,
      Map<String, BaseVariable<?>>... startConfigs) {

    problemDef = new OptimizationProblemDefinition() {
      /**
       * The constant serial version UID.
       */
      private static final long serialVersionUID = 4136607509184032911L;

      @Override
      public IResponseObsSimInstrumenter getSimulationInstrumenter() {
        return null;
      }

      @Override
      public IResponseObsModelInstrumenter getModelInstrumenter() {
        return null;
      }
    };
    for (final IOptimizationFunction function : functionsToBeOptimized) {
      problemDef.addOptimizationObjective(new IOptimizationObjective() {
        @Override
        public double calcObjective(Configuration configuration,
            Map<String, BaseVariable<?>> response) {
          Map<String, Object> valueMap = new HashMap<>();
          for (String varName : configuration.keySet()) {
            valueMap.put(varName, configuration.get(varName).getValue());
          }
          return function.call(valueMap);
        }

        @Override
        public String getName() {
          return function.getName();
        }
      });
    }

    problemDef.setRepresentedValueCalculation(new ArithmeticMeanOfObjectives());
    problemDef.addCancelOptimizationCriteria(cancelCriterion);

    for (Map<String, BaseVariable<?>> startConfig : startConfigs) {
      problemDef.addPredefinedConfiguration(new Configuration(startConfig));
    }
  }

  /**
   * Gets the problem definition.
   * 
   * @return the problem definition
   */
  public OptimizationProblemDefinition getProblemDefinition() {
    return problemDef;
  }

  /**
   * Executes given optimizer on the given function optimization problem.
   * 
   * @param optimizer
   *          the optimizer
   * 
   */
  public void execute(IOptimizationAlgorithm optAlgo) {
    Optimizer optimizer = new Optimizer(optAlgo, problemDef);
    try {
      while (true) {
        optimizer.next();
        TaskConfiguration simConfig = new TaskConfiguration();
        RunInformation runInfo =
            new RunInformation(simConfig.newComputationTaskConfiguration(null));
        runInfo.setResponse(new HashMap<String, Object>());
        optimizer.executionFinished(null, runInfo);
      }
    } catch (NoNextVariableException ex) {
      paretoFront = optimizer.getParetoFront();
    }
  }

  /**
   * Gets the pareto front.
   * 
   * @return the pareto front.
   */
  public Map<Configuration, Map<String, Double>> getParetoFront() {
    return paretoFront;
  }

  /**
   * Resets found solution.
   */
  public void resetSolution() {
    paretoFront.clear();
  }
}