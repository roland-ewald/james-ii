/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.allocation.plugintype;

import java.util.List;

import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.experiments.SimulationRunConfiguration;

/**
 * Implementations of this interface can be called by {@link IMasterServer}
 * implementations to decide upon the number of simulation hosts
 * 
 * @author Simon Bartels
 * @author Roland Ewald
 * 
 */
public interface ISimulationResourceAllocator {

  /**
   * Estimate required resources.
   * 
   * @param simRunConfig
   *          the simulation configuration to be executed
   * @param freeResources
   *          the free resources
   * 
   * @return the resources that would be required for good performance
   */
  List<ISimulationHost> estimateRequiredResources(
      SimulationRunConfiguration simRunConfig,
      List<ISimulationHost> freeResources);

}
