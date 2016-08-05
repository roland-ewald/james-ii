/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Vector factory returning unbounded vectors of a given dimension > 0 based on
 * internal arrays.
 *
 * @author Arne Bittig
 */
public class AVectorFactory implements IVectorFactory {

  private static final long serialVersionUID = 5234503086713147742L;

  /**
   * number of dimensions (should only be set once, but not necessarily in the
   * constructor; thus the negative default value: to indicate
   * "has not been set yet"
   */
  private int dimensions = -1; // s.t. for any double[] c,

  // c.length!=dimensions fails

  /** "cached" return value of {@link #nullVector()} */
  private ADisplacementVector nullVector = null;

  /**
   * Vector factory producing unbounded, array-based vectors
   *
   * @param dim
   *          number of dimensions
   */
  public AVectorFactory(int dim) {
    this.setDimension(dim);
  }

  /**
   * Vector factory producing unbounded, array-based vectors; minimal
   * constructor that requires calling {@link #setDimension(int)} before any
   * vectors can be created.
   *
   */
  public AVectorFactory() {
  }

  @Override
  public VectorType getVectorType() {
    return VectorType.UNBOUNDED;
  }

  @Override
  public int getDimension() {
    return dimensions;
  }

  @Override
  public final void setDimension(int dim) {
    // final as it is called from the constructor
    if (dim <= 0) {
      throw new IllegalArgumentException("Cannot produce " + dim
          + "-dimensional vectors. Value > 0 expected.");
    }
    if (dimensions < 0) {
      dimensions = dim;
    } else if (dimensions != dim) {
      throw new IllegalArgumentException("Vector factory dimension"
          + " number has already been set to " + dimensions
          + "; cannot be set to " + dim);
    }
    nullVector = new ADisplacementNullVector(dimensions);
  }

  /**
   * Immutable null vector for avoiding recreation at every
   * {@link IVectorFactory#nullVector()} call.
   *
   * @author Arne Bittig
   * @date 06.11.2012
   */
  private static final class ADisplacementNullVector extends
  ADisplacementVector {
    private static final long serialVersionUID = 3892177030575615349L;

    ADisplacementNullVector(int dim) {
      super(new double[dim]);
    }

    @Override
    public boolean isNullVector() {
      return true;
    }

    @Override
    public void add(IDisplacementVector disp) {
      throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public void set(int d, double val) {
      throw new UnsupportedOperationException("Immutable");
    }
  }

  /**
   * @param c
   */
  private void dimCheck(int dim) {
    if (dim != dimensions) {
      if (dimensions < 0) {
        setDimension(dim);
      } else {
        throw new IllegalArgumentException("Vector creation attempt with "
            + dim + " instead of " + dimensions + " dimensions.");
      }
    }
  }

  @Override
  public IPositionVector newPositionVector(double... c) {
    dimCheck(c.length);
    return new APositionVector(c);
  }

  @Override
  public IDisplacementVector newDisplacementVector(double... c) {
    dimCheck(c.length);
    return new ADisplacementVector(c);
  }

  /**
   * {@inheritDoc}
   *
   * @throws ArrayIndexOutOfBoundsException
   *           if dim is higher than the number of dimensions
   */
  @Override
  public IDisplacementVector unit(int dim) {
    IDisplacementVector nv = nullVector();
    nv.set(dim, 1);
    return nv;
  }

  @Override
  public IPositionVector origin() {
    return new APositionVector(new double[dimensions]);
  }

  @Override
  public IDisplacementVector nullVector() {
    return nullVector;
  }

  @Override
  public IPositionVector getHighBoundary() {
    throw new UnsupportedOperationException("Unbound vector space");
  }

  @Override
  public IPositionVector getLowBoundary() {
    throw new UnsupportedOperationException("Unbound vector space");
  }

  @Override
  public IDisplacementVector getPeriod() {
    return null;
    // throw new UnsupportedOperationException("Unbound vector space");
  }

  @Override
  public String toString() {
    return "AVectorFactory [dimensions=" + dimensions + "]";
  }

  @Override
  public int hashCode() {
    return dimensions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return dimensions == ((AVectorFactory) obj).dimensions;
  }

}
