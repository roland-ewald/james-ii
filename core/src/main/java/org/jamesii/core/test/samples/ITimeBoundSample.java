/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.samples;

/**
 * Basic interface for a sample which holds information about the (simulation)
 * time interval in which it has been taken. This is necessary for instance to
 * define the start and end time of a simulation whose outputs shall be compared
 * to the sample.
 * 
 * @param <V>
 *          the type of the sampled data
 * 
 * @author Stefan Leye
 * 
 */
public interface ITimeBoundSample<V> extends ISample<V> {

  /**
   * Returns the start time of the sample.
   * 
   * @return start time of sample
   */
  double getStartTime();

  /**
   * Returns the end time of the sample.
   * 
   * @return end time of sample
   */
  double getEndTime();

}
