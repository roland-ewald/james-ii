/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;


import java.io.IOException;
import java.util.Set;

import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * Classes implementing this interface can be used to handle
 * {@link AlgorithmChangeEvaluator} output.
 * 
 * @see AlgorithmChangeEvaluator
 * 
 * @author Roland Ewald
 * 
 */
public interface IOutputACEHandler {

  /**
   * Initialisation method. Is called before the output starts.
   * 
   * @param configsWithAlgo
   *          the set of IDs of configurations that contain the algorithm to be
   *          tested
   */
  void init(Set<Long> configsWithAlgo) throws IOException;

  /**
   * Output the results in some form.
   * 
   * @param simProblem
   *          the simulation problem that was considered
   * @param job
   *          the job description and results of the comparison
   */
  void output(IProblemDefinition simProblem, ComparisonJob job)
      throws IOException;

  /**
   * Finalisation method. Is called when all results have been analysed.
   */
  void finish() throws IOException;

}
