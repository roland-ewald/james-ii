/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

import org.jamesii.core.factories.Context;
import org.jamesii.core.math.integrators.plugintype.AdaptiveOneStepOdeSolverFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating Runge-Kutta 45 ODE solvers.
 * 
 * @author Martin Kell
 */
public class RK45OdeSolverFactory extends AdaptiveOneStepOdeSolverFactory {

  /** The serialization ID. */
  private static final long serialVersionUID = 5393988887773320877L;

  /** Cash-Karp: Order 3(4). */
  private static final double A[][] = {
      { 0 },
      { 1.0 / 5.0, 0 },
      { 3.0 / 40.0, 9.0 / 40.0, 0 },
      { 3.0 / 10.0, -9.0 / 10.0, 6.0 / 5.0, 0 },
      { -11.0 / 54.0, 5.0 / 2.0, -70.0 / 27.0, 35.0 / 27.0, 0 },
      { 1631.0 / 55296.0, 175.0 / 512.0, 575.0 / 13824.0, 44275.0 / 110592.0,
          253.0 / 4096.0, 0 } };

  /** The b1. */
  private static final double B1[] = { 37.0 / 378.0, 0, 250.0 / 621.0,
      125.0 / 594.0, 0, 512.0 / 1771.0 };

  /** The b2. */
  private static final double B2[] = { 2825.0 / 27648.0, 0, 18575.0 / 48384.0,
      13525.0 / 55296.0, 277.0 / 14336.0, 1.0 / 4.0 };

  /** The c. */
  private static final double C[] = { 0, 1.0 / 5.0, 3.0 / 10.0, 3.0 / 5.0, 1,
      7.0 / 8.0 };

  /** The order. */
  private int order = 3;

  /*
   * // Runge-Kutta-Fehlberg: Order 4(5) double A[][] = { {0}, {1.0/5.0, 0},
   * {3.0/40.0, 9.0/40.0, 0}, {3.0/10.0, -9.0/10.0, 6.0/5.0, 0}, {-11.0/54.0,
   * 5.0/2.0 -70.0/27.0, 35.0/27.0, 0}, {1631.0/55296.0, 175.0/512.0,
   * 575.0/13824.0, 44275.0/110592.0, 253.0/4096.0, 0} }; double b1[] =
   * {37.0/378.0, 0, 250.0/621.0, 125.0/594.0, 0, 512.0/1771.0}; double b2[] =
   * {2825.0/27648.0, 0, 18575.0/48384.0, 13525.0/55296.0, 277.0/14336.0,
   * 1.0/4.0 }; double c[] = {0, 1.0/5.0, 3.0/10.0, 3.0/5.0, 1, 7.0/8.0};
   * 
   * int order = 4;
   */
  @Override
  public IOdeStepControl createOdeStepControl() {
    return new HairerStepControl();
  }

  @Override
  public IOdeOneStep create(ParameterBlock pb, Context context) {
    return new RKEmbedStep(A, B1, B2, C, order);
  }

}
