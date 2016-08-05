/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;

/**
 * Interface for {@link IShape shapes} that can be moved and resized
 *
 * @author Arne Bittig
 * @date 17.10.2012 (separation from IShape)
 */
public interface IModifiableShape extends IShape {

  /**
   * Move the whole shape
   * 
   * @param disp
   *          Displacement vector
   */
  void move(IDisplacementVector disp);

  /**
   * "Move" shape by allowing direct manipulation of center position.
   * {@link IShape#getCenter()}, in contrast, may return an unmodifiable view of
   * that vector.
   * 
   * @return Position vector of shape's center
   */
  IPositionVector getCenterForModification();

  /**
   * Scale by a factor, equally in all directions, keeping center constant
   * 
   * @param sc
   *          Scaling factor
   */
  void scale(double sc);

  /**
   * Scale so that resulting shape has given size (equally in all directions,
   * keeping center constant)
   * 
   * @param targetSize
   *          Volume of shape after scaling
   */
  void scaleToSize(double targetSize);
}
