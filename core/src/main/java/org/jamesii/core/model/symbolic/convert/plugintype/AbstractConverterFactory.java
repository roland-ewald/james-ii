/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic.convert.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * A factory for creating AbstractConverter objects.
 */
public class AbstractConverterFactory extends
    AbstractFilteringFactory<ConverterFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2942114163078187506L;

  /** The Constant type. */
  public static final String TYPE = "TYPE";

  /**
   * Instantiates a new abstract model factory.
   */
  public AbstractConverterFactory() {
    super();

  }

}