/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.failuredetection;


import java.util.List;

import org.jamesii.core.factories.Factory;

/**
 * Represents a failure report that contains the factory that is apparently
 * broken as well as all simulation configurations it breaks with.
 * 
 * @author Roland Ewald
 */
public class FailureReport {

  /** The broken factory. */
  private final Class<? extends Factory<?>> factory;

  /**
   * The trace, a list of failure descriptions that should help debugging.
   */
  private final List<FailureDescription> trace;

  /**
   * Instantiates a new failure report.
   * 
   * @param brokenFactory
   *          the broken factory
   * @param errorTrace
   *          the error trace
   */
  public FailureReport(Class<? extends Factory<?>> brokenFactory,
      List<FailureDescription> errorTrace) {
    factory = brokenFactory;
    trace = errorTrace;
  }

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  public Class<? extends Factory<?>> getFactory() {
    return factory;
  }

  /**
   * Gets the trace.
   * 
   * @return the trace
   */
  public List<FailureDescription> getTrace() {
    return trace;
  }

}
