/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.loadbalancing.plugintype;

import org.jamesii.core.factories.AbstractFactory;

/**
 * Selects a load balancing algorithm for a given problem.
 * 
 * @author Roland Ewald
 */
public class AbstractLoadBalancingFactory extends
    AbstractFactory<LoadBalancingFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5029344566119995425L;

  /**
   * Standard constructor.
   */
  public AbstractLoadBalancingFactory() {
  }

}
