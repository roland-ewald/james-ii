/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.integrators.IOdeOneStep;
import org.jamesii.core.math.integrators.IOdeStepControl;

/**
 * Base class for factories creating one-step ODE solvers.
 * 
 * @author Martin Kell
 */
public abstract class AdaptiveOneStepOdeSolverFactory extends
    Factory<IOdeOneStep> {

  /** The serialization ID. */
  private static final long serialVersionUID = 7616643651648165162L;

  /**
   * Creates a new object to handle the step-control.
   * 
   * @return step controller
   */
  public abstract IOdeStepControl createOdeStepControl();

}
