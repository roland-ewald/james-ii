/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.problemsolver.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * A factory for creating AbstractProblemSolver objects.
 * 
 * @author Stefan Leye
 */
public class AbstractProblemSolverFactory extends
    AbstractFilteringFactory<ProblemSolverFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5540086353791317969L;

  /** The Constant PROBLEM_CLASS. */
  public static final String PROBLEM_CLASS = "problemClass";

  /**
   * Instantiates a new abstract problem solver factory.
   */
  public AbstractProblemSolverFactory() {
    super();
  }
}