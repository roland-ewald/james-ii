/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.model.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory to select the eligible {@link ModelInstrumenterFactory}
 * implementations for a given problem.
 * 
 * @author Roland Ewald
 */
public class AbstractModelInstrumenterFactory extends
    AbstractFilteringFactory<ModelInstrumenterFactory> {

  /** The serialization ID. */
  private static final long serialVersionUID = -3678089873863317156L;

  /** The model URI. */
  public static final String MODELURI = "modelURI";

}
