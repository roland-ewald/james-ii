/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * @author Arne Bittig
 *
 */
public class Periodic2DDisplacementVector extends
Periodic2DBaseVector<IDisplacementVector> implements IDisplacementVector {

  private static final long serialVersionUID = 570945068274707563L;

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
  public Periodic2DDisplacementVector(double x, double y, IVectorFactory vecFac) {
    super(x, y, vecFac);
  }

  @Override
  void setPeriodCorrectedX(double val) {
    setX(val);
    double step = getVecFac().getPeriod().get(1);
    double lim = step / 2.;
    while (getX() > lim) {
      setX(getX() - step);
    }
    while (getX() < -lim) {
      setX(getX() + step);
    }
  }

  @Override
  void setPeriodCorrectedY(double val) {
    setY(val);
    double step = getVecFac().getPeriod().get(2);
    double lim = step / 2.;
    while (getY() > lim) {
      setY(getY() - step);
    }
    while (getY() < -lim) {
      setY(getY() + step);
    }
  }

  // CHECK: override equals?

  @Override
  public boolean greaterOrEqual(IDisplacementVector v2) {
    if (getX() < v2.get(1)) {
      return false;
    }
    if (getY() < v2.get(2)) {
      return false;
    }
    if (v2.getDimensions() != this.getDimensions()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean greaterThan(IDisplacementVector v2) {
    if (getX() <= v2.get(1)) {
      return false;
    }
    if (getY() <= v2.get(2)) {
      return false;
    }
    if (v2.getDimensions() != this.getDimensions()) {
      return false;
    }
    return true;
  }

  // @Override
  // public IDisplacementVector signsSwitched(IVector<?> v2) {
  // double newX = 0, newY = 0;
  // if (v2.get(1) < 0)
  // newX = -x;
  // else if (v2.get(1) > 0)
  // newX = x;
  // if (v2.get(2) < 0)
  // newY = -y;
  // else if (v2.get(2) > 0)
  // newY = y;
  // return new Periodic2DDisplacementVector(newX, newY, vecFac);
  // }

  @Override
  public void scale(double sc) {
    setPeriodCorrectedX(getX() * sc);
    setPeriodCorrectedY(getY() * sc);
  }

  @Override
  public Periodic2DDisplacementVector times(double sc) {
    return new Periodic2DDisplacementVector(getX() * sc, getY() * sc,
        getVecFac());
  }

  @Override
  public Periodic2DDisplacementVector plus(IDisplacementVector disp) {
    return new Periodic2DDisplacementVector(getX() + disp.get(1), getY()
        + disp.get(2), getVecFac());
  }

  @Override
  public IDisplacementVector plus(double val) {
    return new Periodic2DDisplacementVector(getX() + val, getY() + val,
        getVecFac());
  }

  // @Override
  // public IDisplacementVector timesElementWise(IDisplacementVector v2) {
  // return new Periodic2DDisplacementVector(x * v2.get(1), y * v2.get(2),
  // vecFac);
  // }

  @Override
  public IDisplacementVector minus(IDisplacementVector disp) {
    return new Periodic2DDisplacementVector(getX() - disp.get(1), getY()
        - disp.get(2), getVecFac());
  }

  @Override
  public double length() {
    return Math.hypot(getX(), getY()); // Math.sqrt(lengthSquared());
  }

  @Override
  public double lengthSquared() {
    return getX() * getX() + getY() * getY();
  }

  @Override
  public Periodic2DDisplacementVector copy() {
    return new Periodic2DDisplacementVector(getX(), getY(), getVecFac());
  }

}
