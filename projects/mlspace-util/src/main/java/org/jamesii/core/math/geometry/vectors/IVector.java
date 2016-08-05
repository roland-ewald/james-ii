/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Vector base methods. Implementations should use one of the two refinements of
 * this interface, either {@link IPositionVector} or {@link IDisplacementVector}
 * .
 *
 * @author Arne Bittig
 * @date Aug 22, 2011
 * @param <T>
 *          Subinterface, {@link IPositionVector} or {@link IDisplacementVector}
 */
interface IVector<T extends IVector<T>> extends java.io.Serializable {

  /**
   * Coordinate in given dimension
   *
   * @param d
   *          dimension (1-based)
   * @return Point or vector's coordinate in given dimension
   */
  double get(int d);

  /**
   * Set single coordinate to a new value
   *
   * @param d
   *          dimension (i.e. coordinate to set; 1-based)
   * @param val
   *          new value
   */
  void set(int d, double val);

  /**
   * set all coordinates to their respective absolute value (in place), i.e.
   * mirror vector at coordinate system hyperplanes until it points into the
   * first orthant (hyperoctant)
   */
  void absolute();

  /**
   * Add a displacement vector to this one (in place)
   *
   * @param disp
   *          displacement vector to add
   * @see #plus(IDisplacementVector) - addition result returned as new vector
   */
  void add(IDisplacementVector disp);

  /**
   * Add same value to each coordinate (e.g. for fixed error tolerance)
   *
   * @param val
   *          Value to add (may be negative)
   * @return vector with all values equal to "this" + val
   */
  T plus(double val);

  /**
   * Sum of this vector and a displacement vector
   *
   * @param disp
   *          displacement vector to add
   * @return this + disp
   * @see #add(IDisplacementVector) - add in place, changing this vector
   */
  T plus(IDisplacementVector disp);

  /**
   * Subtract displacement vector (same result as .plus(disp.times(-1)))
   *
   * @param disp
   *          displacement vector to subtract from this one
   * @return this - disp
   * @see #plus(double)
   */
  T minus(IDisplacementVector disp);

  /**
   * Is this vector the null vector?
   *
   * @return true if all coordinates are 0
   */
  boolean isNullVector();

  /**
   * Compare to another vector - shall return true even if comparing a position
   * and a displacement vector with equal coordinate values, which may not
   * necessarily be desired for {@link #equals(Object)}
   *
   * @param v2
   *          vector to compare to
   * @return True if both vector's corresponding coordinates are equal (should
   *         return false if number of dimensions does not match)
   */
  boolean isEqualTo(IVector<?> v2);

  /**
   * Number of dimensions of the vector (same as {@link #toArray()}.length)
   *
   * @return number of dimensions
   */
  int getDimensions();

  /**
   * Coordinates of the vector as double array (should be a copy, i.e. changes
   * to the returned array shall not change the actual vector. In turn if the
   * respective vector class' constructor of the respective
   * {@link IVectorFactory VectorFactory} method may not create a copy of a
   * passed array.)
   *
   * @return coordinate array
   */
  double[] toArray();

  /**
   * Vector with same coordinates and of same type
   *
   * @return (deep) copy of vector
   */
  T copy();

}