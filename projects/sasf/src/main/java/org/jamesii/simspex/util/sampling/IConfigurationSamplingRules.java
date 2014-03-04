/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util.sampling;


import java.util.List;
import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Defines the rules for the configuration sampling. Most importantly, it is
 * responsible for defining a (deterministic!) neighborhood for each
 * configuration.
 * 
 * @author Roland Ewald
 */
public interface IConfigurationSamplingRules {

  /**
   * Calculates selection probabilities for each parameterization tree. The
   * return values must sum up to one and have to be from [0,1]. Will only be
   * called once at start-up.
   * 
   * @param trees
   *          the trees
   * @return the map from trees to their selection probabilities
   */
  Map<ParameterizationTree, Double> calculateSelectionProbabilities(
      List<ParameterizationTree> trees);

  /**
   * Gets a random neighbor of the given configuration.
   * 
   * @param configuration
   *          the configuration
   * @param treeForConfig
   *          the tree that generated the configuration
   * @param parameterToChange
   *          the index of the parameter to change
   * @return the neighbor
   */
  ParameterBlock getNeighbor(ParameterBlock configuration,
      ParameterizationTree treeForConfig, int parameterToChange);

  /**
   * Gets the neighborhood of a configuration.
   * 
   * @param configuration
   *          the configuration
   * @param treeForConfig
   *          the tree that generated the configuration
   * @return the neighborhood
   */
  List<ParameterBlock> getNeighborhood(ParameterBlock configuration,
      ParameterizationTree treeForConfig);

  /**
   * Sets the random number generator that shall be used.
   * 
   * @param random
   *          the random number generator to be used
   */
  void setRNG(IRandom random);

}
