/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import org.jamesii.core.math.interpolation.plugintype.InterpolationFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating {@link CubicSplineInterpolator} objects.
 * 
 * @author Stefan Leye
 */
public class CubicSplineInterpolatorFactory extends InterpolationFactory {

  /** The serialization ID. */
  private static final long serialVersionUID = 1708586887730679225L;

  @Override
  public IInterpolator create(ParameterBlock parameters) {
    return new CubicSplineInterpolator();
  }

}
