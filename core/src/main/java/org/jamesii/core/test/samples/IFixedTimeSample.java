/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.samples;

import java.util.List;

/**
 * Interface for sampled variable values for a fixed point in (simulation) time.
 * 
 * @param <V>
 *          the type of the sampled data
 * @author Roland Ewald
 */
public interface IFixedTimeSample<V> extends ISample<List<V>> {

  /**
   * Get the simulation time at which these values have been sampled.
   * 
   * @return the simulation time
   */
  double getSampleSimTime();

}
