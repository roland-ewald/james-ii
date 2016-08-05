/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.spatialindex;

import java.io.Serializable;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Abstract class for spatial index factories
 *
 * @author Arne Bittig
 */
public abstract class SpatialIndexFactory extends Factory<ISpatialIndex<?>> {

  private static final long serialVersionUID = 2217894625144954808L;

  @Override
  public ISpatialIndex<?> create(ParameterBlock parameters, Context context) {
    return createDirect(parameters);
  }

  /**
   * Create spatial index with the given parameters (delegation for type
   * parameter issues)
   *
   * @param params
   *          Parameter block
   * @return Spatial index
   */
  public abstract <C extends IShapedComponent & Serializable> ISpatialIndex<C> createDirect(
      ParameterBlock params);
}
