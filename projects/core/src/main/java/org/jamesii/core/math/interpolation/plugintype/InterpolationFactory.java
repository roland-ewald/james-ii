/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.interpolation.IInterpolator;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base factory for creating Interpolation objects.
 * 
 * @author Stefan Leye
 */
public abstract class InterpolationFactory extends Factory<IInterpolator> {

  /** The serialization ID. */
  private static final long serialVersionUID = -4266294084178731016L;

  /**
   * Creates a new Interpolation object.
   * 
   * @param parameters
   *          TODO
   * 
   * @return the interpolator
   */
  @Override
  public abstract IInterpolator create(ParameterBlock parameters, Context context);
}
