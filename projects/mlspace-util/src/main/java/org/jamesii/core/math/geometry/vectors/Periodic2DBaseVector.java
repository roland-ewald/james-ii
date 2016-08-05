/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * @author Arne Bittig
 * @param <T>
 *          {@link IPositionVector} or {@link IDisplacementVector}
 */
public abstract class Periodic2DBaseVector<T extends IVector<T>> implements
IVector<T> {

  private static final long serialVersionUID = 4884084624708524141L;

  /** Vector factory that produced this */
  private final IVectorFactory vecFac;

  /** x coordinate */
  private double x;

  /** y coordinate */
  private double y;

  /**
   * Standard constructor
   *
   * @param x
   *          x coordinate
   * @param y
   *          y coordinate
   * @param vecFac
   *          vector factory
   */
  Periodic2DBaseVector(double x, double y, IVectorFactory vecFac) {
    this.vecFac = vecFac;
    setPeriodCorrectedX(x);
    setPeriodCorrectedY(y);
  }

  /**
   * @return the x
   */
  final double getX() {
    return x;
  }

  /**
   * @param x
   *          the x to set
   */
  final void setX(double x) {
    this.x = x;
  }

  /**
   * @return the y
   */
  final double getY() {
    return y;
  }

  /**
   * @param y
   *          the y to set
   */
  final void setY(double y) {
    this.y = y;
  }

  @Override
  public final double get(int d) {
    switch (d) {
    case 1:
      return x;
    case 2:
      return y;
    default:
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public void set(int d, double val) {
    switch (d) {
    case 1:
      setPeriodCorrectedX(val);
      return;
    case 2:
      setPeriodCorrectedY(val);
      return;
    default:
      throw new IndexOutOfBoundsException();
    }
  }

  /**
   * @return Vector factory for creating new vectors of same type (and thus
   *         within same periodic boundaries)
   */
  final IVectorFactory getVecFac() {
    return vecFac;
  }

  /**
   * Set x coordinate, correcting for extension of space (i.e. periodic
   * boundaries) -- this is different for position and displacement vectors
   *
   * @param val
   *          Value to set x to
   */
  abstract void setPeriodCorrectedX(double val);

  /**
   * Set y coordinate, correcting for extension of space (i.e. periodic
   * boundaries) -- this is different for position and displacement vectors
   *
   * @param val
   *          Value to set y to
   */
  abstract void setPeriodCorrectedY(double val);

  /**
   * {@inheritDoc} NOTE: For periodic boundaries where -min > max (i.e. with
   * center to the negative side of the origin) this will not produce correct
   * results for all vectors (and in fact, cannot)
   */
  @Override
  public void absolute() {
    if (x < 0) {
      x = -x;
    }
    if (y < 0) {
      y = -y;
    }

  }

  @Override
  public void add(IDisplacementVector disp) {
    setPeriodCorrectedX(x + disp.get(1));
    setPeriodCorrectedY(y + disp.get(2));
  }

  @Override
  public boolean isNullVector() {
    return x == 0. && y == 0.;
  }

  @Override
  public boolean isEqualTo(IVector<?> v2) {
    if (x != v2.get(1)) { // NOSONAR: that's the point of an equality test
      return false;
    }
    if (y != v2.get(2)) {// NOSONAR: that's the point of an equality test
      return false;
    }
    if (v2.getDimensions() != this.getDimensions()) {
      return false;
    }
    return true;
  }

  @Override
  public int getDimensions() {
    return 2;
  }

  @Override
  public double[] toArray() {
    return new double[] { x, y };
  }

  private static String sepStr = "|";

  private static String formStr = "%.5f";

  @Override
  public String toString() {
    return "(" + String.format(formStr, x) + sepStr + String.format(formStr, y)
        + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = vecFac.hashCode();
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ temp >>> 32); // NOSONAR:
    // auto-gen
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ temp >>> 32); // NOSONAR:
    // auto-gen
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
    Periodic2DBaseVector<?> other = (Periodic2DBaseVector<?>) obj;
    if (vecFac == null) {
      if (other.vecFac != null) {
        return false;
      }
    } else if (!vecFac.equals(other.vecFac)) {
      return false;
    }
    return isEqualTo(other);
  }

}
