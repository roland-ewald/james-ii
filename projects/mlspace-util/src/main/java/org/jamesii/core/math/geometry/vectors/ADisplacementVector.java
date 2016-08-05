/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Implementation of {@link IDisplacementVector} based on an internal array (see
 * {@link AbstractArrayBasedVector}).
 *
 * @author Arne Bittig
 *
 */
public class ADisplacementVector extends
AbstractArrayBasedVector<IDisplacementVector> implements
IDisplacementVector {

  private static final long serialVersionUID = 3821950341340559836L;

  protected ADisplacementVector(double[] c) {
    super(c);
  }

  @Override
  public boolean greaterOrEqual(IDisplacementVector v2) {
    for (int d = getDimensions(); d > 0; d--) {
      if (this.get(d) < v2.get(d)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean greaterThan(IDisplacementVector v2) {
    for (int d = getDimensions(); d > 0; d--) {
      if (this.get(d) <= v2.get(d)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public final void scale(double sc) {
    for (int d = getDimensions(); d > 0; d--) {
      set(d, get(d) * sc);
    }
  }

  // @Override
  // public IDisplacementVector signsSwitched(IVector<?> v2) {
  // double[] nc = toArrayCopy();
  // for (int i = 0; i < c.length; i++) {
  // double v2val = v2.get(i + 1);
  // if (v2val < 0)
  // nc[i] = -c[i];
  // else if (v2val == 0)
  // nc[i] = 0;
  // }
  // return new ADisplacementVector(nc);
  // }

  @Override
  public ADisplacementVector times(double sc) {
    ADisplacementVector nv = this.copy();
    nv.scale(sc);
    return nv;
  }

  @Override
  public ADisplacementVector plus(double val) {
    int dim = getDimensions();
    double[] nc = new double[dim];
    for (int i = 0; i < dim; i++) {
      nc[i] = getDirect(i) + val;
    }
    return new ADisplacementVector(nc);
  }

  @Override
  public ADisplacementVector plus(IDisplacementVector disp) {
    ADisplacementVector newVec = this.copy();
    newVec.add(disp);
    return newVec;
  }

  // @Override
  // public IDisplacementVector timesElementWise(IDisplacementVector v2) {
  // double[] nc = new double[c.length];
  // for (int i = 0; i < c.length; i++)
  // nc[i] = c[i] * v2.get(i + 1);
  // return new ADisplacementVector(nc);
  // }

  @Override
  public ADisplacementVector minus(IDisplacementVector disp) {
    return new ADisplacementVector(this.minusArr(disp.toArray()));
  }

  @Override
  public double length() {
    return Math.sqrt(lengthSquared());
  }

  @Override
  public double lengthSquared() {
    return euclidNormSquared();
  }

  @Override
  public ADisplacementVector copy() {
    return new ADisplacementVector(this.toArray());
  }

}
