/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy.plugintype;

import org.jamesii.core.factories.AbstractFactory;

/**
 * Selects a computation task end policy / criteria / condition.
 * 
 * @author Jan Himmelspach
 */
public class AbstractComputationTaskStopPolicyFactory extends
    AbstractFactory<ComputationTaskStopPolicyFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5361308565374446562L;

  /**
   * Standard constructor.
   */
  public AbstractComputationTaskStopPolicyFactory() {
  }

}
