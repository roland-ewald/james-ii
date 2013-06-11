/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.problemsolver;

/**
 * Base class for problem solvers, offering some convenient functionality.
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Stefan Leye
 * 
 */
public abstract class AbstractProblemSolver<S extends IProblemSolverState<?, ?>, I>
    implements IProblemSolver<S, I> {

  /**
   * The serialization id.
   */
  private static final long serialVersionUID = 3604011838469217553L;

  @SuppressWarnings("unchecked")
  // checking is done in checkStateType
  @Override
  public S solve(IProblemSolverState<?, ?> state, I input) {
    S checkedState = null;
    // check and set the state
    if (checkStateType(state)) {
      checkedState = (S) state;
    } else {
      throw new InvalidProblemSolverStateException(this.getClass()
          + " can't use a " + state.getClass() + " as internal state!");
    }
    return solveProblem(checkedState, input);
  }

  /**
   * Checks whether the given state has the correct type for this problem
   * solver.
   * 
   * @return true if the given state has the correct type for this problem
   *         solver.
   */
  protected abstract boolean checkStateType(IProblemSolverState<?, ?> state);

  /**
   * Is called by {@link #solve(IProblemSolverState, Object)} to execute the
   * desired functionality of this solver, using the right state type.
   * 
   * @param state
   *          the state
   * @param input
   *          the input
   * @return the following state
   */
  protected abstract S solveProblem(S state, I input);
}
