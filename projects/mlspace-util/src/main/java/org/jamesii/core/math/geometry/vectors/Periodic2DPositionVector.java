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
public class Periodic2DPositionVector extends
Periodic2DBaseVector<IPositionVector> implements IPositionVector {

  private static final long serialVersionUID = 4682404012450450974L;

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
  Periodic2DPositionVector(double x, double y, IVectorFactory vecFac) {
    super(x, y, vecFac);
  }

  @Override
  void setPeriodCorrectedX(double val) {
    setX(val);
    double min = getVecFac().getLowBoundary().get(1);
    double max = getVecFac().getHighBoundary().get(1);
    while (getX() > max) {
      setX(getX() - (max - min));
    }
    while (getX() < min) {
      setX(getX() + (max - min));
    }
  }

  @Override
  void setPeriodCorrectedY(double val) {
    setY(val);
    double min = getVecFac().getLowBoundary().get(2);
    double max = getVecFac().getHighBoundary().get(2);
    while (getY() > max) {
      setY(getY() - (max - min));
    }
    while (getY() < min) {
      setY(getY() + (max - min));
    }
  }

  @Override
  public Periodic2DDisplacementVector displacementTo(IPositionVector p2) {
    return new Periodic2DDisplacementVector(p2.get(1) - getX(), p2.get(2)
        - getY(), getVecFac());
  }

  @Override
  public IDisplacementVector scaledDisplacementTo(IPositionVector p2, double sc) {
    return new Periodic2DDisplacementVector(sc * (p2.get(1) - getX()), sc
        * (p2.get(2) - getY()), getVecFac());
  }

  @Override
  public double distance(IPositionVector p2) {
    return Math.sqrt(distanceSquared(p2)); // includes period correct. step
  }

  @Override
  public double distanceSquared(IPositionVector p2) {
    return displacementTo(p2).lengthSquared(); // includes period corr. step
  }

  @Override
  public Periodic2DPositionVector plus(double val) {
    return new Periodic2DPositionVector(getX() + val, getY() + val, getVecFac());
  }

  @Override
  public Periodic2DPositionVector plus(IDisplacementVector disp) {
    return new Periodic2DPositionVector(getX() + disp.get(1), getY()
        + disp.get(2), getVecFac());
  }

  @Override
  public Periodic2DPositionVector minus(IDisplacementVector disp) {
    return new Periodic2DPositionVector(getX() - disp.get(1), getY()
        - disp.get(2), getVecFac());
  }

  @Override
  public Periodic2DPositionVector interpolate(double w1, IVector<?> v2,
      double w2) {
    if (v2 instanceof Periodic2DBaseVector<?>) {
      Periodic2DBaseVector<?> v2p = (Periodic2DBaseVector<?>) v2;
      return new Periodic2DPositionVector(this.getX() * w1 + v2p.getX() * w2,
          this.getY() * w1 + v2p.getY() * w2, getVecFac());
    } else {
      double[] v2c = v2.toArray();
      return new Periodic2DPositionVector(this.getX() * w1 + v2c[0] * w2,
          this.getY() * w1 + v2c[1] * w2, getVecFac());
    }
  }

  @Override
  public Periodic2DPositionVector copy() {
    return new Periodic2DPositionVector(getX(), getY(), getVecFac());
  }
}
