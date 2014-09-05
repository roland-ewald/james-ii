/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.integrators.AbstractODESolver;
import org.jamesii.core.math.integrators.IOde;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for all factories creating ODE solvers.
 * 
 * @author Jan Himmelspach
 */
public abstract class ODESolverFactory extends Factory<AbstractODESolver> {

  /** The serialization ID. */
  private static final long serialVersionUID = 5234735782242271853L;

  /**
   * Creates a new ODE solver instance.
   * 
   * @param odeSystem
   *          the ordinary differential equations
   * @param initialState
   *          the initial values of the variables
   * @param startVar
   *          the start value of the parameter (e.g. time)
   * @param stopVar
   *          the end value of the parameter
   * 
   * @return the ODE solver
   */
  public abstract AbstractODESolver createODESolver(IOde odeSystem,
      double startVar, double stopVar);

  @Override
  public AbstractODESolver create(ParameterBlock parameters, Context context) {
    return createODESolver((IOde) getParameter("ODE_SYSTEM", parameters),
        (Double) getParameter("START", parameters),
        (Double) getParameter("STOP", parameters));
  }

}
