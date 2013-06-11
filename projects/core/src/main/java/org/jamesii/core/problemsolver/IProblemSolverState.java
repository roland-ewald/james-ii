/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.problemsolver;

import java.io.Serializable;

/**
 * Represents the internal state of {@link IProblemSolver} objects.
 * 
 * @author Roland Ewald
 * @author Stefan Leye
 * 
 */
public interface IProblemSolverState<RES, REQ> extends Serializable {

  /**
   * Get the result after the current iteration of problem solving.
   * 
   * @return the result
   */
  RES getCurrentResult();

  /**
   * Get the request for information about the problem that are necessary for
   * the next iteration of solving, e.g., the next variables assignment for an
   * optimization problem.
   * 
   * @return the next request of information about the given problem
   */
  REQ getNextRequest();

}
