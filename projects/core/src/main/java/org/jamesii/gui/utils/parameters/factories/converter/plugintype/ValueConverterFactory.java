/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.factories.converter.IStringConverter;

/**
 * A factory for creating ValueConverter objects.
 * 
 * @author Stefan Rybacki
 * @param <D>
 *          the type the converter converts into
 */
public abstract class ValueConverterFactory<D> extends
    Factory<IStringConverter<D>> implements IParameterFilterFactory {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -3039426107252686105L;

  /**
   * Creates the converter.
   * 
   * @param paramBlock
   *          the parameter block
   * @return the string converter
   */
  @Override
  public abstract IStringConverter<D> create(ParameterBlock paramBlock);

}
