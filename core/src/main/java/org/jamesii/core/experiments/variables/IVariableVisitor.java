/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

/**
 * Simple visitor interface to generalise some minor tasks when traversing the
 * {@link ExperimentVariables} structure.
 * 
 * @author Roland Ewald
 * 
 */
public interface IVariableVisitor {

  /**
   * Visit a node.
   * 
   * @param expVar
   */
  void visit(ExperimentVariable<?> expVar);
}