/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.problemsolver;

import java.io.Serializable;

/**
 * Basic interfaces for classes solving a specific problem, e.g., steady state
 * detection, optimization, model execution, etc. The process of solving a
 * problem is started by calling the method
 * {@link #solve(IProblemSolverState, Object)}. The result of the solving
 * process is stored in the state of the problem solver which is returned. The
 * solver itself cannot keep state information! If information of previous
 * iterations of the solving process are required, they come with the given
 * state. Accordingly, information about the current iteration should be
 * returned with the actual state. If another iteration of the solving process
 * is required the problem solver asks for additional information. Those
 * requests should stored in the state as well. Answers of such a request are
 * forwarded to the solver as input of
 * {@link #solve(IProblemSolverState, Object)}. The {@link #init()} method
 * returns the initial state of the solver.
 * 
 * @param <S>
 *          the type of the state, the solver can have
 * @param <I>
 *          the input type
 * @author Stefan Leye
 */
public interface IProblemSolver<S extends IProblemSolverState<?, ?>, I> extends
    Serializable {

  /**
   * Initializes the solver
   */
  S init();

  /**
   * Solves a problem of type P producing a result of type R.
   * 
   * @param problem
   *          the problem
   * @return the result
   */
  S solve(IProblemSolverState<?, ?> state, I input);
}
