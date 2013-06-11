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
 * A factory for creating {@link LinearInterpolator} objects.
 * 
 * @author Stefan Leye
 */
public class LinearInterpolatorFactory extends InterpolationFactory {

  /** The serialization ID. */
  private static final long serialVersionUID = 7173904063960323011L;

  @Override
  public IInterpolator create(ParameterBlock parameters) {
    return new LinearInterpolator();
  }

}
