/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

import java.util.Arrays;

/**
 * @author Arne Bittig
 * @param <T>
 *          Subtype (position or displacement vector)
 */
abstract class AbstractArrayBasedVector<T extends IVector<T>> implements
    IVector<T> {

  private static final long serialVersionUID = 4328404165372835574L;

  /** the coordinates as array (elements may change!) */
  private final double[] c;

  /**
   * Create vector using given coordinate array (without copy)
   *
   * @param c
   *          coordinates as double array
   */
  protected AbstractArrayBasedVector(double[] c) {
    this.c = c;
  }

  @Override
  public final double get(int d) {
    return getDirect(d - 1);
  }

  final double getDirect(int i) {
    return c[i];
  }

  /**
   * {@inheritDoc}
   *
   * @throws ArrayIndexOutOfBoundsException
   *           if d>{@link #getDimensions()}
   */
  @Override
  public void set(int d, double val) {
    c[d - 1] = val;
  }

  @Override
  public void absolute() {
    for (int i = 0; i < c.length; i++) {
      if (c[i] < 0) {
        c[i] = -c[i];
      }
    }
  }

  @Override
  public void add(IDisplacementVector disp) {
    this.addArr(disp.toArray());
  }

  /**
   * Add coordinate array (in place; no check for matching length)
   *
   * @param c2
   *          other coordinates
   */
  private void addArr(double[] c2) {
    for (int i = 0; i < c.length; i++) {
      c[i] += c2[i];
    }
  }

  /**
   * Substract coordinate array, create new one (useful for
   * {@link IDisplacementVector#minus(IDisplacementVector)} and
   * {@link IPositionVector#displacementTo(IPositionVector)})
   *
   * @param c2
   *          other coordinates
   * @return new coordinate array, c-c2 element-wise
   */
  final double[] minusArr(double[] c2) {
    double[] nc = new double[c.length];
    for (int i = 0; i < c.length; i++) {
      nc[i] = c[i] - c2[i];
    }
    return nc;
  }

  final double euclidNormSquared() {
    double ens = c[0] * c[0];
    for (int i = 1; i < c.length; i++) {
      ens += c[i] * c[i];
    }
    return ens;
  }

  @Override
  public boolean isEqualTo(IVector<?> v2) {
    if (this.getDimensions() != v2.getDimensions()) {
      return false;
    }
    double[] v2Arr = v2.toArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] != v2Arr[i]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isNullVector() {
    for (double element : c) {
      if (element != 0.) {
        return false;
      }
    }
    return true;
  }

  @Override
  public final int getDimensions() {
    return c.length;
  }

  /**
   * {@inheritDoc} This implementation creates a copy of the internally used
   * array.
   */
  @Override
  public final double[] toArray() {
    return Arrays.copyOf(c, c.length);
  }

  @Override
  public String toString() {
    return Vectors.arrayToString(c, "|", "%.5f");
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(c);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != this.getClass()) {
      return false;
    }
    return this.isEqualTo((IVector<?>) obj);
  }

}
