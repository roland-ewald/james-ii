/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Interface for vectors denoting positions, meaning, e.g., that they cannot
 * reasonably be scaled or added to another of the same type
 *
 * @author Arne Bittig
 */
public interface IPositionVector extends IVector<IPositionVector> {

  /**
   * Displacement from this point to another
   *
   * @param p2
   *          other position vector
   * @return displacement such that it should always be true that
   *         this.plus(this.displacementTo(p2)).equals(p2)
   */
  IDisplacementVector displacementTo(IPositionVector p2);

  /**
   * Scaled displacement from this point to another -- same as
   * this.displacementTo(p2).times(sc) for non-periodic boundaries, but more
   * direct calculation; for periodic boundaries, no period correction step is
   * to be performed between displacement calculation and scaling
   *
   * @param p2
   *          other position vector
   * @param sc
   *          scaling factor
   * @return displacement from this to p2, scaled by factor sc
   */
  IDisplacementVector scaledDisplacementTo(IPositionVector p2, double sc);

  /**
   * Distance of point from another point
   *
   * @param p2
   *          other position vector
   * @return distance of this position from p2 (or the other way around)
   * @see #distanceSquared(IPositionVector) #displacementTo(IPositionVector)
   *      {@link IDisplacementVector#length()}
   */
  double distance(IPositionVector p2);

  /**
   * Square of distance of point from another point (useful for distance
   * comparison, as it skips the square root calculation needed for
   * {@link #distance(IPositionVector)})
   *
   * - same as this.plus(c2.times(-1)).lengthSqr(), but more direct
   *
   * @param c2
   *          Position vector of the other point
   * @param p2
   *          other position vector
   * @return square of distance of this position from p2 (or the other way
   *         around)
   * @see #distance(IPositionVector) #displacementTo(IPositionVector)
   *      {@link IDisplacementVector#length()}
   */
  double distanceSquared(IPositionVector p2);

  @Override
  IPositionVector plus(double val);

  @Override
  IPositionVector plus(IDisplacementVector disp);

  @Override
  IPositionVector minus(IDisplacementVector disp);

  /**
   * Linear combination of this vector and another (e.g. linComb(0.5,p2,0.5)
   * gives the middle between this point and p2)
   *
   * @param w1
   *          weight for this vector
   * @param v2
   *          other vector
   * @param w2
   *          weight for other vector
   * @return w1*this + w2*v2
   */
  IPositionVector interpolate(double w1, IVector<?> v2, double w2);

  @Override
  IPositionVector copy();

}