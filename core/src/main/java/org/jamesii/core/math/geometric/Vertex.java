/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometric;

import org.jamesii.core.util.misc.Triple;

/**
 * Simple vertex class. Non optimized. Used by {@link Geometry}.
 * 
 * @author Stefan Rybacki
 */
class Vertex extends Triple<Double, Double, Double> {

  /**
   * The Constant EPSILON.
   */
  private static final double EPSILON = Double.MIN_NORMAL;

  /**
   * Instantiates a new vertex.
   * 
   * @param a
   *          the a
   * @param b
   *          the b
   * @param c
   *          the c
   */
  public Vertex(Double a, Double b, Double c) {
    super(a, b, c);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vertex) {
      return Math.abs(((Vertex) obj).getA() - getA()) < EPSILON
          && Math.abs(((Vertex) obj).getB() - getB()) < EPSILON
          && Math.abs(((Vertex) obj).getC() - getC()) < EPSILON;
    }

    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    int hash =
        Double.valueOf(Math.round(getA().doubleValue() / EPSILON)).hashCode();
    hash *= 31;// NOSONAR
    hash +=
        Double.valueOf(Math.round(getB().doubleValue() / EPSILON)).hashCode();
    hash *= 31;// NOSONAR
    hash +=
        Double.valueOf(Math.round(getC().doubleValue() / EPSILON)).hashCode();

    return hash;
  }

  @Override
  public String toString() {
    return String.format("(%.3f %.3f %.3f)", getA(), getB(), getC());
  }

}
