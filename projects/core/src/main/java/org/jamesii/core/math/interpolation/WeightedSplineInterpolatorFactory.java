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
 * A factory for creating {@link WeightedSplineInterpolator} objects.
 * 
 * @author Thomas Flisgen
 */
public class WeightedSplineInterpolatorFactory extends InterpolationFactory {

  /** The serialization ID. */
  private static final long serialVersionUID = 2262161895584466535L;

  @Override
  public IInterpolator create(ParameterBlock parameters, Context context) {
    return new WeightedSplineInterpolator();
  }

}