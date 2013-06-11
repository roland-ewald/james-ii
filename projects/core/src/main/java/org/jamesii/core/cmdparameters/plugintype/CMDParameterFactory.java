/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory for cmd line parameters.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class CMDParameterFactory extends Factory<Object> implements
    IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6490397922261619401L;

  /**
   * Creates the.
   * 
   * @param params
   *          the params
   * 
   * @return the object
   */
  @Override
  public abstract Object create(ParameterBlock params);

}
