/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

/**
 * Class to represent four-element composites.
 * 
 * @param <E1>
 *          type of first element
 * @param <E2>
 *          type of second element
 * @param <E3>
 *          type of third element
 * @param <E4>
 *          type of fourth element
 * 
 * @see Pair
 * @see Triple
 * @author Roland Ewald
 */
public class Quadruple<E1, E2, E3, E4> {

  /** The first element. */
  private final E1 e1;

  /** The second element. */
  private final E2 e2;

  /** The third element. */
  private final E3 e3;

  /** The fourth element. */
  private final E4 e4;

  /**
   * Instantiates a new quadruple.
   * 
   * @param element1
   *          the element1
   * @param element2
   *          the element2
   * @param element3
   *          the element3
   * @param element4
   *          the element4
   */
  public Quadruple(E1 element1, E2 element2, E3 element3, E4 element4) {
    e1 = element1;
    e2 = element2;
    e3 = element3;
    e4 = element4;
  }

  /**
   * @return the e1
   */
  public final E1 getA() {
    return getE1();
  }

  /**
   * @return the e2
   */
  public final E2 getB() {
    return getE2();
  }

  /**
   * @return the e3
   */
  public final E3 getC() {
    return getE3();
  }

  /**
   * @return the e4
   */
  public final E4 getD() {
    return getE4();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Quadruple<?, ?, ?, ?>)) {
      return false;
    }
    Quadruple<?, ?, ?, ?> q = (Quadruple<?, ?, ?, ?>) obj;
    return Comparators.equal(getE1(), q.getE1())
        && Comparators.equal(getE2(), q.getE2())
        && Comparators.equal(getE3(), q.getE3())
        && Comparators.equal(getE4(), q.getE4());
  }

  @Override
  public int hashCode() {

    return (getE1() == null ? 0 : getE1().hashCode())
        ^ (getE2() == null ? 0 : getE2().hashCode()
            ^ (getE3() == null ? 0 : getE3().hashCode()))
        ^ (getE4() == null ? 0 : getE4().hashCode());
  }

  /**
   * @return the e1
   */
  public final E1 getE1() {
    return e1;
  }

  /**
   * @return the e2
   */
  public final E2 getE2() {
    return e2;
  }

  /**
   * @return the e3
   */
  public final E3 getE3() {
    return e3;
  }

  /**
   * @return the e4
   */
  public final E4 getE4() {
    return e4;
  }

  @Override
  public String toString() {
    return "(" + e1 + "," + e2 + "," + e3 + "," + e4 + ")";
  }

}
