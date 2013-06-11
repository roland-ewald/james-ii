/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.allocation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.distributed.allocation.plugintype.ISimulationResourceAllocator;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.experiments.SimulationRunConfiguration;

/**
 * Simple implementation of {@link ISimulationResourceAllocator} that requests a
 * constant number of hosts (or less, if not that much are available).
 * 
 * @author Simon Bartels
 * @author Roland Ewald
 * 
 */
public class ConstantResourceAllocator implements ISimulationResourceAllocator {

  /** The number of required resources. */
  private final int reqResources;

  /**
   * Instantiates a new constant resource allocator.
   * 
   * @param requiredResources
   *          the required resources
   */
  public ConstantResourceAllocator(int requiredResources) {
    reqResources = requiredResources;
  }

  @Override
  public List<ISimulationHost> estimateRequiredResources(
      SimulationRunConfiguration simRunConfig,
      List<ISimulationHost> freeResources) {
    return new ArrayList<>(freeResources.subList(0,
        Math.min(freeResources.size(), reqResources)));
  }

}
