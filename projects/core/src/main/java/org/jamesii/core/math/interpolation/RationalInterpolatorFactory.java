/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import org.jamesii.core.factories.Context;
import org.jamesii.core.math.interpolation.plugintype.InterpolationFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating {@link RationalFunctionInterpolator} objects.
 * 
 * @author Stefan Leye
 */
public class RationalInterpolatorFactory extends InterpolationFactory {

  /** The serialization ID. */
  private static final long serialVersionUID = 6689392470528040941L;

  @Override
  public IInterpolator create(ParameterBlock parameters, Context context) {
    return new RationalFunctionInterpolator();
  }

}
