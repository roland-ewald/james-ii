/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.replication.IReplicationCriterion;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A variable modifier is a class which modifies a variable according to
 * internal rules whenever next is called. The modified variable value is
 * returned. By calling reset the variable has to be set to its initial value
 * again (e.g. for looping).
 * 
 * @param <V>
 *          The type of the variable to be modified
 * 
 * @author Jan Himmelspach
 */
public abstract class VariableModifier<V> implements IVariableModifier<V> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4812292895804573751L;

  @Override
  public IModelInstrumenter getModelInstrumenter() {
    return null;
  }

  @Override
  public IComputationInstrumenter getSimulationInstrumenter() {
    return null;
  }

  @Override
  public IReplicationCriterion getReplicationCriterion() {
    return null;
  }

  @Override
  public ParameterBlock getExperimentParameters() {
    return null;
  }

  /**
   * Compute the next variable value to be used.
   * 
   * @param variables
   *          - the set of variable for the current experiment
   * 
   * @return next variable value
   * 
   * @throws NoNextVariableException
   *           if there is no next variable
   */
  @Override
  public abstract V next(ExperimentVariables variables)
      throws NoNextVariableException;

  /**
   * Reset to initial value.
   */
  @Override
  public abstract void reset();

}
