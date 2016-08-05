/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Interface for vectors denoting displacement. meaning, e.g. that they can be
 * scaled or added to another (resulting in a new displacement vector) or to a
 * position vector (resulting in a new position).
 *
 * @author Arne Bittig
 *
 */
public interface IDisplacementVector extends IVector<IDisplacementVector> {

  /**
   * Compare to another vector: greater or equal for each coordinate
   *
   * @param v2
   *          vector to compare to
   * @return True if all coordinates of this one are greater than or equal to
   *         the respective coordinate of the argument (incl. total equality)
   */
  boolean greaterOrEqual(IDisplacementVector v2);

  /**
   * Compare to another vector: strictly greater for each coordinate
   *
   * @param v2
   *          vector to compare to
   * @return True if all coordinates of this one are greater than or equal to
   *         the respective coordinate of the argument (incl. total equality)
   */
  boolean greaterThan(IDisplacementVector v2);

  /**
   * Scale vector in place
   *
   * @param sc
   *          scaling factor
   * @see #times(double) returns result as new vector, leaving this unchanged
   */
  void scale(double sc);

  /**
   * Multiple of this vector
   *
   * @param sc
   *          scaling factor
   * @return vector with coordinates sc*this
   * @see #scale(double) for changing coordinates in place
   */
  IDisplacementVector times(double sc);

  // /**
  // * element-wise product of two vectors (e.g. to set some coordinates 0 for
  // * projection onto an axis/plane)
  // *
  // * @param v2
  // * other vector
  // * @return new IDispVec({this.get(1)*v2.get(1),this.get(2)*v2.get(2),...})
  // */
  // IDisplacementVector timesElementWise(IDisplacementVector
  // v2);

  @Override
  IDisplacementVector plus(double val);

  @Override
  IDisplacementVector plus(IDisplacementVector disp);

  @Override
  IDisplacementVector minus(IDisplacementVector disp);

  /**
   * Length of displacement vector (Euclidean norm)
   *
   * @return length of vector according to Euclidean norm
   * @see #lengthSquared()
   */
  double length();

  /**
   * Square of length of displacement vector (Euclidean norm) (useful for
   * distance comparison, as it skips the square root calculation needed for
   * {@link #length()})
   *
   * @return square of length of vector according to Euclidean norm
   * @see #length()
   */
  double lengthSquared();

  @Override
  IDisplacementVector copy();

}