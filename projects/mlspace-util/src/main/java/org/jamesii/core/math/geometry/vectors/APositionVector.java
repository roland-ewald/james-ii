/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

/**
 * Implementation of {@link IPositionVector} based on an internal array (see
 * {@link AbstractArrayBasedVector}).
 *
 * @author Arne Bittig
 *
 */
public class APositionVector extends AbstractArrayBasedVector<IPositionVector>
implements IPositionVector {

  private static final long serialVersionUID = 3165747304578682782L;

  /**
   * @param c
   */
  protected APositionVector(double[] c) {
    super(c);
  }

  @Override
  public ADisplacementVector displacementTo(IPositionVector p2) {
    return new ADisplacementVector(p2minusThisArr(p2));
  }

  /**
   * @param p2
   * @return
   */
  private double[] p2minusThisArr(IPositionVector p2) {
    if (p2 instanceof APositionVector) {
      return ((APositionVector) p2).minusArr(this.toArray());
    } else {
      double[] minusArr = this.minusArr(p2.toArray());
      for (int i = 0; i < minusArr.length; i++) {
        minusArr[i] = -minusArr[i];
      }
      return minusArr;
    }
  }

  @Override
  public IDisplacementVector scaledDisplacementTo(IPositionVector p2, double sc) {
    double[] arr = p2minusThisArr(p2);
    for (int i = 0; i < arr.length; i++) {
      arr[i] *= sc;
    }
    return new ADisplacementVector(arr);
  }

  @Override
  public double distance(IPositionVector p2) {
    return Math.sqrt(distanceSquared(p2));
  }

  @Override
  public double distanceSquared(IPositionVector p2) {
    // TODO Auto-generated method stub
    return ((AbstractArrayBasedVector<IDisplacementVector>) displacementTo(p2))
        .euclidNormSquared();
  }

  // @Override
  // public IPositionVector signsSwitched(IVector<?> v2) {
  // double[] nc = toArrayCopy();
  // for (int i = 0; i < c.length; i++) {
  // double v2val = v2.get(i + 1);
  // if (v2val < 0)
  // nc[i] = -c[i];
  // else if (v2val == 0)
  // nc[i] = 0;
  // }
  // return new APositionVector(nc);
  // }

  @Override
  public APositionVector plus(double val) {
    int dim = getDimensions();
    double[] nc = new double[dim];
    for (int i = 0; i < dim; i++) {
      nc[i] = getDirect(i) + val;
    }
    return new APositionVector(nc);
  }

  @Override
  public APositionVector plus(IDisplacementVector disp) {
    APositionVector newVec = this.copy();
    newVec.add(disp);
    return newVec;
  }

  @Override
  public APositionVector minus(IDisplacementVector disp) {
    return new APositionVector(this.minusArr(disp.toArray()));
  }

  @Override
  public APositionVector interpolate(double w1, IVector<?> v2, double w2) {
    int dim = getDimensions();
    double[] c2 = v2.toArray();
    double[] lc = new double[dim];
    for (int i = 0; i < dim; i++) {
      lc[i] = w1 * getDirect(i) + w2 * c2[i];
    }
    return new APositionVector(lc);
  }

  @Override
  public APositionVector copy() {
    return new APositionVector(this.toArray());
  }
}
