/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Coordinate/Vector factory
 *
 * Each factory should always return only vectors of the same two subclasses of
 * {@link IPositionVector} and {@link IDisplacementVector}, and all of the same
 * dimension.
 *
 * @author Arne Bittig
 */
public interface IVectorFactory extends java.io.Serializable {

  /**
   * Get number of dimensions of the produced vectors - if setting of dimensions
   * (see {@link #setDimension(int)}) is supported, a negative value may be
   * returned if called before number of dimensions has been set.
   *
   * @return number of dimensions
   */
  int getDimension();

  /**
   * Set number of dimensions of the produced vectors (optional operation*) -
   * Sometimes, the kind of vector factory desired is known before the number of
   * dimensions is known. This operation allows setting the number of dimensions
   * after the creation of the vector factory. Implementations need not (and
   * should not) allow changing the number once it has been set, to make sure
   * all vectors created by one factory have the same dimension. *This operation
   * should do nothing (i.e. not throw an exception) if the parameter equals the
   * fixed or previously set number of dimensions.
   *
   * @param dim
   *          number of dimensions
   */
  void setDimension(int dim);

  /**
   * Create position vector from given coordinates (varargs method; if an array
   * is passed, it shall be used without copying)
   *
   * @param c
   *          coordinates
   * @return new position vector with given coordinates
   * @throws IllegalArgumentException
   *           if the number of dimensions does not match
   */
  IPositionVector newPositionVector(double... c);

  /**
   * Create displacement vector from given coordinates (varargs method; if an
   * array is passed, it shall be used without copying)
   *
   * @param c
   *          coordinates
   * @return new position vector with given coordinates
   * @throws IllegalArgumentException
   *           if the number of dimensions does not match
   */
  IDisplacementVector newDisplacementVector(double... c);

  /**
   * Unit vector in given direction
   *
   * @param dim
   *          dimension (1-based, i.e. x:1, y:2,...)
   * @return unit vector along given dimension
   */
  IDisplacementVector unit(int dim);

  /**
   * New position vector pointing to the origin
   *
   * @return new position vector with all coordinates 0
   */
  IPositionVector origin();

  /**
   * Neutral element of displacement operation
   *
   * @return new displacement vector with all coordinates 0
   */
  IDisplacementVector nullVector();

  /**
   * Maximum reasonable position - relevant for vectors within periodic
   * boundaries. If none are present, e.g. in the usual R^n vector space, the
   * return value shall be null, not an infinite vector. The returned vector
   * need not permit the usual vector operations (plus, minus, etc.), only
   * {@link IPositionVector#get(int)}, as the boundaries are only virtual and
   * the handling of overflow shall be entirely within the implementing class.
   * (However, it might be used as an argument to operations on another vector.
   * Although this does not make sense either, it is unavoidable to keep the
   * contract of vector operations.) Also, the coordinates should never be
   * changed, and thus the set operations should not be used on them. (A new
   * vector factory shall be used for different coordinates.)
   *
   * @return highest possible position
   */
  IPositionVector getHighBoundary();

  /**
   * Minimum reasonable position (null, i.e. none for the usual R^n vector
   * space; relevant for vectors within periodic boundaries)
   *
   * @see #getHighBoundary()
   * @return lowest possible position
   */
  IPositionVector getLowBoundary();

  /**
   * Maximum reasonable displacement vector (null, i.e. none for the usual R^n
   * vector space; relevant for vectors within periodic boundaries)
   *
   * @see #getHighBoundary()
   * @return maximum reasonable displacement vector
   */
  IDisplacementVector getPeriod();

  /**
   *
   * @return Type of vectors created by factory
   */
  VectorType getVectorType();

  /**
   * Type of vectors (vector spaces?)
   *
   * @author Arne Bittig
   *
   */
  public static enum VectorType {
    /** "normal" vectors with up to infinitely large coordinate values */
    UNBOUNDED,
    /** periodic vectors with limited coordinates and connected sides */
    PERIODIC
  }

}
