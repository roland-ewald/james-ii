/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.fitness;


import java.io.Serializable;
import java.util.List;

import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.util.misc.Pair;


/**
 * Interface for fitness functions for the genetic algorithm to rely on. This
 * has to return either only positive values, or only negative values, or values
 * that are determined by the signs of the performance data.
 * 
 * @author René Schulz, Roland Ewald
 */
public interface IPortfolioFitness extends Serializable {

  /**
   * Calculate fitness.
   * 
   * @param the
   *          portfolio problem
   * @param performances
   *          the performances
   * @param genome
   *          the individual's genome
   * 
   * @return the fitness of the individual
   */
  double calculateFitness(PortfolioProblemDescription problem,
      Double[][] performances, boolean[] genome);

  /**
   * Gets a detailed fitness description. The returned list contains tuples
   * (description, value) so that it allows to inspect the fitness of a given
   * individual in more detail. Should only be used to debugging, logging, and
   * analysis purposes.
   * 
   * @param problem
   *          the problem
   * @param performances
   *          the performances
   * @param genome
   *          the genome
   * @return the detailed fitness description
   */
  List<Pair<String, Double>> getDetailedFitnessDescription(
      PortfolioProblemDescription problem, Double[][] performances,
      boolean[] genome);

}
