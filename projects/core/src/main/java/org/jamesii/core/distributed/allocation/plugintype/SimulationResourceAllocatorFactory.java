/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.allocation.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base class for all factories for creating simulation resource allocators.
 * 
 * @author Simon Bartels
 * @author Roland Ewald
 * 
 */
public abstract class SimulationResourceAllocatorFactory extends
    Factory<ISimulationResourceAllocator> {

  /** Serialization id. */
  private static final long serialVersionUID = 3842782802906303423L;

  /**
   * Creates a simulation resource allocator.
 * @param paramBlock
   *          the parameter block
 * @return the simulation resource allocator
   * 
   *         A simulation resource allocator.
   */
  @Override
  public abstract ISimulationResourceAllocator create(ParameterBlock paramBlock, Context context);
}
