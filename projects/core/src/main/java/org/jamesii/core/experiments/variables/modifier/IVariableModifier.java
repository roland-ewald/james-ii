/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import java.io.Serializable;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.replication.IReplicationCriterion;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Interface for variable modifiers.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * 
 * @param <V>
 *          the type of the variable
 */
public interface IVariableModifier<V> extends Serializable {

  /**
   * Get the parameters for the experiment.
   * 
   * @return experiment parameters
   */
  ParameterBlock getExperimentParameters();

  /**
   * Get model instrumenter to allow the modifier a decision based on
   * observations.
   * 
   * @return necessary model observer, null if none is needed
   */
  IModelInstrumenter getModelInstrumenter();

  /**
   * Get simulation instrumenter to allow the modifier a decision based on an
   * observations.
   * 
   * @return necessary simulation observer, null if none is needed
   */
  IComputationInstrumenter getSimulationInstrumenter();

  /**
   * Get the replication criterion for the next simulation.
   * 
   * @return replication criterion
   */
  IReplicationCriterion getReplicationCriterion();

  /**
   * Get the next variable value to be used.
   * 
   * @param variables
   *          the current experiment variables
   * @return new value
   * @throws NoNextVariableException
   *           if there is no new variable setup to be chosen
   */
  V next(ExperimentVariables variables) throws NoNextVariableException;

  /**
   * Reset the variable modifier.
   */
  void reset();

}
