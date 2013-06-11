/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.problemsolver.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.problemsolver.IProblemSolver;

/**
 * Base factory for classes solving problems.
 * 
 * @author Stefan Leye
 */
public abstract class ProblemSolverFactory extends
    Factory<IProblemSolver<?, ?>> implements IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2423683356450857270L;

  @Override
  public abstract IProblemSolver<?, ?> create(ParameterBlock params);

  @Override
  public int supportsParameters(ParameterBlock params) {
    if ((params != null)
        && params.getValue().equals(this.getClass().getName())) {
      return 1;
    }
    return 0;
  }
}