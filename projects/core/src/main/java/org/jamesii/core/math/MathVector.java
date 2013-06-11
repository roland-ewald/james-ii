/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import java.util.List;

/**
 * MathVector Description: Class containing an implementation of a mathematical
 * vector together with the basic vector manipulation methods.
 * 
 * @author Stefan Leye
 */
public class MathVector extends Matrix {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 1174890065162245380L;

  /**
   * Default constructor.
   * 
   * @param size
   *          of the vector
   */
  public MathVector(int size) {
    super(1, size);
  }

  /**
   * Creates a new MathVector initialized with the values from the list.
   * 
   * @param values
   *          the list with vector elements
   */
  public MathVector(List<Double> values) {
    super(1, values.size());
    for (int i = 0; i < values.size(); i++) {
      this.setElement(i, values.get(i));
    }
  }

  /**
   * Creates a copy of the vector.
   * 
   * @return new vector, equivalent to this one
   */
  @Override
  public MathVector copy() {
    MathVector m = new MathVector(size());
    for (int i = 0; i < this.size(); i++) {
      m.setElement(i, getElement(i));
    }
    return m;
  }

  /**
   * Returns the element at the specified position.
   * 
   * @param pos
   *          position
   * @return the value
   */
  public double getElement(int pos) {
    return getElement(0, pos);
  }

  /**
   * Multiplicates this vector with a scalar.
   * 
   * @param scalar
   *          the scalar
   */
  public void multWithScalar(double scalar) {
    multRow(0, scalar);
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer("(" + getElement(0));
    for (int i = 1; i < this.size(); i++) {
      result.append(result + "," + getElement(i));
    }
    return result + ")";
  }

  /**
   * Calculates the scalar product of this vector and another one.
   * 
   * @param v
   *          vector for multiplication
   * @return scalar product
   */
  public double scalarProduct(MathVector v) {
    double result = 0;
    if (this.size() == v.size()) {
      for (int i = 0; i < this.size(); i++) {
        result = result + this.getElement(i) * v.getElement(i);
      }
    } else {
      throw new MatrixException(
          "Vectors need to have the same length for scalar product!");
    }
    return result;
  }

  /**
   * Sets the element at the specified position.
   * 
   * @param pos
   *          position
   * @param value
   *          to set
   */
  public final void setElement(int pos, double value) {
    setElement(0, pos, value);
  }

  /**
   * Returns the size of the vector.
   * 
   * @return the number of columns / the length of the vector
   */
  public int size() {
    return getColumns();
  }

  /**
   * Calculates the vector or cross product of this vector and another one.
   * 
   * @param v
   *          vector for multiplication
   * @return vector product
   */
  public MathVector vectorProduct(MathVector v) {
    MathVector result;
    if (this.size() == v.size()) {
      result = new MathVector(size());
      // TODO implement generalized vector product.
      if (size() == 3) {
        result.setElement(
            0,
            this.getElement(1) * v.getElement(2) - this.getElement(2)
                * v.getElement(1));
        result.setElement(
            1,
            this.getElement(2) * v.getElement(0) - this.getElement(0)
                * v.getElement(2));
        result.setElement(
            2,
            this.getElement(0) * v.getElement(1) - this.getElement(1)
                * v.getElement(0));
      } else {
        throw new MatrixException(
            "Vector product for size != 3 not implemented yet!");
      }
    } else {
      throw new MatrixException(
          "Vectors need to have the same length for vector product!");
    }
    return result;
  }
}
