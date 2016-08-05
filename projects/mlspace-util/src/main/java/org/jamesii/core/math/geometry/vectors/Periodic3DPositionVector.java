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
public class Periodic3DPositionVector extends
Periodic3DBaseVector<IPositionVector> implements IPositionVector {

  private static final long serialVersionUID = 4682404012450450974L;

  /**
   * Standard constructor
   *
   * @param x
   *          x coordinate
   * @param y
   *          y coordinate
   * @param z
   *          z coordinate
   * @param vecFac
   *          vector factory
   */
  Periodic3DPositionVector(double x, double y, double z, IVectorFactory vecFac) {
    super(x, y, z, vecFac);
  }

  @Override
  void setPeriodCorrectedX(double val) {
    setX(periodCorrect(val, 1));
  }

  @Override
  void setPeriodCorrectedY(double val) {
    setY(periodCorrect(val, 2));
  }

  @Override
  void setPeriodCorrectedZ(double val) {
    setZ(periodCorrect(val, 3));
  }

  private double periodCorrect(double val, int dim) {
    double rv = val;
    double min = getVecFac().getLowBoundary().get(dim);
    double max = getVecFac().getHighBoundary().get(dim);
    while (rv > max) {
      rv -= max - min;
    }
    while (rv < min) {
      rv += max - min;
    }
    return rv;
  }

  @Override
  public Periodic3DDisplacementVector displacementTo(IPositionVector p2) {
    return new Periodic3DDisplacementVector(p2.get(1) - getX(), p2.get(2)
        - getY(), p2.get(3) - getZ(), getVecFac());
  }

  @Override
  public IDisplacementVector scaledDisplacementTo(IPositionVector p2, double sc) {
    return new Periodic3DDisplacementVector(sc * (p2.get(1) - getX()), sc
        * (p2.get(2) - getY()), sc * (p2.get(3) - getZ()), getVecFac());
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
  public Periodic3DPositionVector plus(double val) {
    return new Periodic3DPositionVector(getX() + val, getY() + val, getZ()
        + val, getVecFac());
  }

  @Override
  public Periodic3DPositionVector plus(IDisplacementVector disp) {
    return new Periodic3DPositionVector(getX() + disp.get(1), getY()
        + disp.get(2), getZ() + disp.get(3), getVecFac());
  }

  @Override
  public Periodic3DPositionVector minus(IDisplacementVector disp) {
    return new Periodic3DPositionVector(getX() - disp.get(1), getY()
        - disp.get(2), getZ() - disp.get(3), getVecFac());
  }

  @Override
  public Periodic3DPositionVector interpolate(double w1, IVector<?> v2,
      double w2) {
    if (v2 instanceof Periodic3DBaseVector<?>) {
      Periodic3DBaseVector<?> v2p = (Periodic3DBaseVector<?>) v2;
      return new Periodic3DPositionVector(this.getX() * w1 + v2p.getX() * w2,
          this.getY() * w1 + v2p.getY() * w2, this.getZ() * w1 + v2p.getZ()
          * w2, getVecFac());
    } else {
      double[] v2c = v2.toArray();
      return new Periodic3DPositionVector(this.getX() * w1 + v2c[0] * w2,
          this.getY() * w1 + v2c[1] * w2, this.getZ() * w1 + v2c[2] * w2,
          getVecFac());
    }
  }

  @Override
  public Periodic3DPositionVector copy() {
    return new Periodic3DPositionVector(getX(), getY(), getZ(), getVecFac());
  }
}
