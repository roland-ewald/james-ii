/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.allocation;

import org.jamesii.core.distributed.allocation.plugintype.ISimulationResourceAllocator;
import org.jamesii.core.distributed.allocation.plugintype.SimulationResourceAllocatorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for the most simple resource allocator.
 * 
 * @author Roland Ewald
 */
public class ConstantResourceAllocatorFactory extends
    SimulationResourceAllocatorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5390121981408458884L;

  /**
   * The name of the parameter that defines how many resources should be
   * allocated. Type: {@link Integer}.
   */
  public static final String NUM_RESOURCES = "numberOfResources";

  @Override
  public ISimulationResourceAllocator create(ParameterBlock paramBlock) {
    return new ConstantResourceAllocator(paramBlock.getSubBlockValue(
        NUM_RESOURCES, 1));
  }

}
