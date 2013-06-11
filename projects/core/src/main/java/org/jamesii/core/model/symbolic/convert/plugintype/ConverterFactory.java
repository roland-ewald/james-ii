/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic.convert.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.model.symbolic.convert.IConverter;

/**
 * A factory for creating Converter objects.
 */
public abstract class ConverterFactory extends Factory<IConverter> implements
    IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2160308540930230218L;

  /**
   * Instantiates a new model factory.
   */
  public ConverterFactory() {
    super();
  }

}
