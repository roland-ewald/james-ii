/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.abort;

import java.io.Serializable;

import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;


/**
 * Interfaces for all classes that implement a decision on when to stop the
 * genetic algorithm.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public interface IAbortCriterion extends Serializable {

  /**
   * Checks whether to abort the genetic algorithm.
   * 
   * @param selector
   *          the selector
   * 
   * @return true, if algorithm should be aborted
   */
  boolean abort(GeneticAlgorithmPortfolioSelector selector);

}
