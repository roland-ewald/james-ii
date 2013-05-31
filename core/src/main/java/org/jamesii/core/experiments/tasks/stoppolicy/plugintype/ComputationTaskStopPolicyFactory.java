/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy.plugintype;

import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for task stop criteria / policies / conditions.
 * 
 * An {@link IComputationTaskStopPolicy} object determines whether a computation
 * task shall continue or whether it has reached its end.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class ComputationTaskStopPolicyFactory extends
    Factory<IComputationTaskStopPolicy> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3039426107252686102L;

  /** The Constant COMPTASK. */
  public static final String COMPTASK = "COMPUTATIONTASK";

  /**
   * Creates a new {@link IComputationTaskStopPolicy} object.
   * 
   * @param paramBlock
   *          the parameter block
   * 
   * @return the computation task run end component
   */
  @Override
  public abstract IComputationTaskStopPolicy create(ParameterBlock paramBlock);

}
