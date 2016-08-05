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
public class Periodic3DDisplacementVector extends
    Periodic3DBaseVector<IDisplacementVector> implements IDisplacementVector {

  private static final long serialVersionUID = 570945068274707563L;

  /**
   * Standard constructor
   *
   * @param x
   *          x coordinate
   * @param y
   *          y coordinate
   * @param z
   * @param vecFac
   *          vector factory
   */
  public Periodic3DDisplacementVector(double x, double y, double z,
      IVectorFactory vecFac) {
    super(x, y, z, vecFac);
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

  @Override
  void setPeriodCorrectedZ(double val) {
    setZ(val);
    double step = getVecFac().getPeriod().get(3);
    double lim = step / 2.;
    while (getZ() > lim) {
      setZ(getZ() - step);
    }
    while (getZ() < -lim) {
      setZ(getZ() + step);
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
    if (getZ() < v2.get(3)) {
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
    if (getZ() <= v2.get(3)) {
      return false;
    }
    if (v2.getDimensions() != this.getDimensions()) {
      return false;
    }
    return true;
  }

  @Override
  public void scale(double sc) {
    setPeriodCorrectedX(getX() * sc);
    setPeriodCorrectedY(getY() * sc);
  }

  @Override
  public Periodic3DDisplacementVector times(double sc) {
    return new Periodic3DDisplacementVector(getX() * sc, getY() * sc, getZ()
        * sc, getVecFac());
  }

  @Override
  public Periodic3DDisplacementVector plus(IDisplacementVector disp) {
    return new Periodic3DDisplacementVector(getX() + disp.get(1), getY()
        + disp.get(2), getZ() + disp.get(3), getVecFac());
  }

  @Override
  public IDisplacementVector plus(double val) {
    return new Periodic3DDisplacementVector(getX() + val, getY() + val, getZ()
        + val, getVecFac());
  }

  @Override
  public IDisplacementVector minus(IDisplacementVector disp) {
    return new Periodic3DDisplacementVector(getX() - disp.get(1), getY()
        - disp.get(2), getZ() - disp.get(3), getVecFac());
  }

  @Override
  public double length() {
    return Math.sqrt(lengthSquared());
  }

  @Override
  public double lengthSquared() {
    return getX() * getX() + getY() * getY() + getZ() * getZ();
  }

  @Override
  public Periodic3DDisplacementVector copy() {
    return new Periodic3DDisplacementVector(getX(), getY(), getZ(), getVecFac());
  }

}
