/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry;

/**
 * Extension of {@link IShapedComponent} where {@link #getShape()} returns a
 * shape ready for modification.
 *
 * @author Arne Bittig
 * @date 17.10.2012
 */
public interface IModifiableShapedComponent extends IShapedComponent {

  /**
   * Change center coordinates along given dimension to given value
   *
   * @param dim
   *          Dimension
   * @param coord
   *          Value to set
   */
  void moveAlongDimTo(int dim, double coord);

}
