/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

import java.util.logging.Level;

import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Comparators;

/**
 * @author Arne Bittig
 *
 */
public class Periodic2DVectorFactory implements IVectorFactory {

  /** Serialization ID */
  private static final long serialVersionUID = 3021542063130826788L;

  /** lower bounds for the values of a position vector */
  private final Periodic2DBoundaryPoint min;

  /** upper bounds for the values of a position vector */
  private final Periodic2DBoundaryPoint max;

  /** periodicity in each dimension (max displacement depends on this) */
  private final Periodic2DBoundaryLength period;

  /** "cached" return value of {@link #nullVector()} */
  private final Periodic2DDisplacementVector nullVector;

  /**
   * Vector factory producing 2D vectors in periodic boundary conditions
   *
   * Shapes extending over half or more of the space in any dimension may not be
   * handled correctly, e.g. give unexpected results for shape relation
   * operations.
   *
   * @param lowX
   *          lower bound for x
   * @param lowY
   *          lower bound for y
   * @param highX
   *          upper bound for x
   * @param highY
   *          upper bound for y
   */
  public Periodic2DVectorFactory(double lowX, double lowY, double highX,
      double highY) {
    min = new Periodic2DBoundaryPoint(lowX, lowY, this);
    max = new Periodic2DBoundaryPoint(highX, highY, this);
    period = new Periodic2DBoundaryLength(highX - lowX, highY - lowY, this);

    // consistency checks
    if (highX < -lowX || highY < -lowY) {
      ApplicationLogger.log(Level.WARNING, "Center of periodic boundary "
          + "space has negative coordinates in at least one "
          + "dimension! This may prevent absolute values "
          + "from being determined correctly.");
    }

    if (period.getX() <= 0. || period.getY() <= 0.) {
      throw new IllegalArgumentException("Period for periodic boundaries"
          + " must be greater than 0 in all directions.");
    } else if (period.getX() < 1. || period.getY() < 1.) {
      ApplicationLogger.log(Level.WARNING, "Periodic boundaries with period"
          + " of less than 1. This may give unexpected results for "
          + " unit vectors.");
    }
    nullVector = new Periodic2DDisplacementVector(0, 0, this);
  }

  @Override
  public VectorType getVectorType() {
    return VectorType.PERIODIC;
  }

  @Override
  public int getDimension() {
    return 2;
  }

  @Override
  public void setDimension(int dim) {
    if (dim != 2) {
      throw new UnsupportedOperationException("This vector factory "
          + "does not support " + dim + " dimensions: " + this);
    }
  }

  @Override
  public IPositionVector newPositionVector(double... c) {
    if (c.length != 2) {
      throw new IllegalArgumentException();
    }
    return new Periodic2DPositionVector(c[0], c[1], this);
  }

  @Override
  public IDisplacementVector newDisplacementVector(double... c) {
    if (c.length != 2) {
      throw new IllegalArgumentException();
    }
    return new Periodic2DDisplacementVector(c[0], c[1], this);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IndexOutOfBoundsException
   *           if dim is higher than the number of dimensions
   */
  @Override
  public IDisplacementVector unit(int dim) {
    switch (dim) {
    case 1:
      return new Periodic2DDisplacementVector(1, 0, this);
    case 2:
      return new Periodic2DDisplacementVector(0, 1, this);
    default:
      throw new IndexOutOfBoundsException();
    }

  }

  @Override
  public IPositionVector origin() {
    return new Periodic2DPositionVector(0, 0, this);
  }

  @Override
  public IDisplacementVector nullVector() {
    return nullVector;
  }

  @Override
  public IPositionVector getHighBoundary() {
    return max;
  }

  @Override
  public IPositionVector getLowBoundary() {
    return min;
  }

  @Override
  public IDisplacementVector getPeriod() {
    return period;
  }

  @Override
  public String toString() {
    return "Periodic2DVectorFactory for vectors from " + min.toString()
        + " to " + max.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = period.hashCode();
    result = prime * result + max.hashCode();
    result = prime * result + min.hashCode();
    return result;
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
    Periodic2DVectorFactory other = (Periodic2DVectorFactory) obj;
    if (!Comparators.equal(this.period, other.period)) {
      return false;
    }
    if (!Comparators.equal(this.max, other.max)) {
      return false;
    }
    if (!Comparators.equal(this.min, other.min)) {
      return false;
    }
    return true;
  }

  /**
   * Factory-internal classes to store the boundaries (which cannot be stored in
   * normal position and displacement vectors as they contain the very periodic
   * boundaries those are corrected with). The set... methods for the period
   * correction do nothing here and all methods directly changing the vector
   * throw {@link UnsupportedOperationException}s.
   *
   * @author Arne Bittig
   */

  private static final class Periodic2DBoundaryPoint extends
  Periodic2DPositionVector {

    private static final long serialVersionUID = 2889317128554420654L;

    Periodic2DBoundaryPoint(double x, double y, IVectorFactory vecFac) {
      super(x, y, vecFac);
    }

    @Override
    void setPeriodCorrectedX(double val) {
      setX(val);
    }

    @Override
    void setPeriodCorrectedY(double val) {
      setY(val);
    }

    @Override
    public Periodic2DPositionVector copy() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int d, double val) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void absolute() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(IDisplacementVector disp) {
      throw new UnsupportedOperationException();
    }
  }

  private static final class Periodic2DBoundaryLength extends
  Periodic2DDisplacementVector {

    private static final long serialVersionUID = 8135454639309387908L;

    Periodic2DBoundaryLength(double x, double y, IVectorFactory vecFac) {
      super(x, y, vecFac);
    }

    @Override
    void setPeriodCorrectedX(double val) {
      setX(val);
    }

    @Override
    void setPeriodCorrectedY(double val) {
      setY(val);
    }

    @Override
    public Periodic2DDisplacementVector copy() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int d, double val) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void absolute() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(IDisplacementVector disp) {
      throw new UnsupportedOperationException();
    }
  }
}
