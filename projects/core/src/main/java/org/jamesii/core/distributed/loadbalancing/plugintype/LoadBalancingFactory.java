/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.loadbalancing.plugintype;

import org.jamesii.core.distributed.loadbalancing.ILoadBalancingSetup;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;

/**
 * 
 * Abstract factory for load balancing algorithms.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class LoadBalancingFactory extends Factory<ILoadBalancingSetup> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -8515833947686604414L;

  /**
   * Method to rule out incompatible modelling formalisms.
   * 
   * @param model
   *          the model to be simulated
   * @return value > 0 if model is supported
   */
  public abstract int supportsModel(IModel model);

  /**
   * Method to rule out incompatible processors.
   * 
   * @param processor
   *          the processor to be used for simulation
   * @return value > 0 if processor is supported
   */
  public abstract int supportsProcessor(IProcessor processor);

  /**
   * Creates a new {@link ILoadBalancingSetup} object.
 * @param paramBlock
   *          the parameter block
 * @return the load balancing setup component
   */
  @Override
  public abstract ILoadBalancingSetup create(ParameterBlock paramBlock, Context context);

}
