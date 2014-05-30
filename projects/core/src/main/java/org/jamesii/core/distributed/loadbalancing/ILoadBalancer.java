/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.loadbalancing;

import java.io.Serializable;

import org.jamesii.core.observe.IObserver;

/**
 * Interface that represents the load balancing algorithm. It triggers the
 * {@link ILBProcessor} whenever appropriate.
 * 
 * @param <I>
 *          the kind of load information that is stored
 * @param <P>
 *          the type of the local processor
 * @author Roland Ewald
 * 
 */
public interface ILoadBalancer<I extends Serializable, P extends ILBProcessor>
    extends IObserver {

  /**
   * Initialises load balancing recorder.
   * 
   * @param processor
   *          the local processor
   */
  void init(P processor);

  /**
   * Gets information that is relevant for load balancing. Might be called by
   * remote load balancing algorithms.
   * 
   * @return relevant information
   */
  I getInfo();

  /**
   * Resets statistics. Should only be used by a remote instance of
   * {@link ILoadBalancer}.
   */
  void reset();

}
