/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract class to create suitable
 * {@link org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter}
 * instances.
 * 
 * @author Roland Ewald
 */
public class AbstractComputationInstrumenterFactory extends
    AbstractFilteringFactory<ComputationInstrumenterFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -9172525953128374611L;

  /** The URI of the model to be simulated, type: {@link java.net.URI}. */
  public static final String MODELURI = "modelURI";

}
