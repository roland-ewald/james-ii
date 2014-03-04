/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.fitness;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.core.util.misc.Pair;


/**
 * Auxiliary super class for {@link IPortfolioFitness} implementations, contains
 * simple default implementations for all methods that are non-essential.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractPortfolioFitness implements IPortfolioFitness {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2570752357091972785L;

  @Override
  public List<Pair<String, Double>> getDetailedFitnessDescription(
      PortfolioProblemDescription problem, Double[][] performances,
      boolean[] genome) {
    List<Pair<String, Double>> result = new ArrayList<>();
    result.add(new Pair<>("Overall Fitness", this
        .calculateFitness(problem, performances, genome)));
    return result;
  }

}
