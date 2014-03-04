/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util.sampling;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * Interface for configuration samplers.
 * 
 * @author Robert Engelke
 * @author Roland Ewald
 * 
 */
public interface IConfigurationSampler {

  /**
   * Gets an initial configuration where the ILS algorithm starts the search.
   * 
   * @return the initial configuration
   */
  ParameterBlock getInitialConfiguration();

  /**
   * Gets a neighbor for the given Configuration.
   * 
   * @param simulationConfiguration
   *          the simulation configuration
   * @return a neighbor of the simulation configuration
   */
  ParameterBlock getRandomNeighbour(ParameterBlock simConfig);

  /**
   * Gets a random neighbour with a change at the given position.
   * 
   * @param simConfig
   *          the simulation configuration
   * @param positionToChange
   *          the position to be changed
   * @return the random neighbour changed at the given position
   */
  ParameterBlock getRandomNeighbour(ParameterBlock simConfig,
      int positionToChange);

  /**
   * Gets the neighbourhood for a given simulation configuration.
   * 
   * @param simConfig
   *          the simulation configuration
   * @return the neighbourhood
   */
  List<ParameterBlock> getNeighbourhood(ParameterBlock simConfig);

  /**
   * Gets the number of parameters.
   * 
   * @param configuration
   *          the configuration
   * @return the number of parameters
   */
  int getNumberOfParameters(ParameterBlock configuration);

  /**
   * Gets a fully random configuration to restart the search.
   * 
   * @return the random configuration
   */
  ParameterBlock restart();

}
