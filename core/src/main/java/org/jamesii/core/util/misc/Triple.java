/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.Serializable;

/**
 * Simple class for holding three values.
 * 
 * @see Pair
 * 
 * @param <A>
 *          type of the first element
 * @param <B>
 *          type of the second element
 * @param <C>
 *          type of the third element
 * 
 * @author Jan Himmelspach
 */
public class Triple<A, B, C> implements Serializable {

  /**
   * The serialization id.
   */
  private static final long serialVersionUID = 4442297643729823472L;

  /** The first element. */
  private A a;

  /** The second element. */
  private B b;

  /** The third element. */
  private C c;

  /**
   * Instantiates a new triple.
   * 
   * @param a
   *          the first element
   * @param b
   *          the second element
   * @param c
   *          the third element
   */
  public Triple(A a, B b, C c) {
    super();
    this.a = a;
    this.b = b;
    this.c = c;
  }

  /**
   * Gets the first element.
   * 
   * @return the first element
   */
  public A getA() {
    return a;
  }

  /**
   * Sets the first element.
   * 
   * @param a
   *          the new first element
   */
  public void setA(A a) {
    this.a = a;
  }

  /**
   * Gets the second element.
   * 
   * @return the second element
   */
  public B getB() {
    return b;
  }

  /**
   * Sets the second element.
   * 
   * @param b
   *          the new second element
   */
  public void setB(B b) {
    this.b = b;
  }

  /**
   * Gets the third element.
   * 
   * @return the third element
   */
  public C getC() {
    return c;
  }

  /**
   * Sets the third element.
   * 
   * @param c
   *          the new third element
   */
  public void setC(C c) {
    this.c = c;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Triple<?, ?, ?>)) {
      return false;
    }
    Triple<?, ?, ?> t = (Triple<?, ?, ?>) obj;
    return Comparators.equal(a, t.getA()) && Comparators.equal(b, t.getB())
        && Comparators.equal(c, t.getC());
  }

  @Override
  public String toString() {
    return "(" + (a == null ? "" : a.toString()) + ","
        + (b == null ? "" : b.toString()) + ","
        + (c == null ? "" : c.toString()) + ")";
  }

  @Override
  public int hashCode() {
    return (a == null ? 0 : a.hashCode())
        ^ (b == null ? 0 : b.hashCode() ^ (c == null ? 0 : c.hashCode()));
  }
}
