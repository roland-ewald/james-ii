/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.Serializable;

/**
 * Pair is a simple class for the representation of a tuple. The two type
 * parameters E1 and E2 define the objects to be stored as value1 (E1) and
 * value2 (E2).
 * 
 * @param <E1>
 *          type of the first value to be stored in the pair
 * @param <E2>
 *          type of the second value to be stored in the pair
 * 
 * @author Susanne Biermann
 */
public class Pair<E1, E2> implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5512850252645885922L;

  /** The first value of the pair. */
  private E1 firstValue = null;

  /** The second value of the pair. */
  private E2 secondValue = null;

  /**
   * Create a new pair and use the passed values as content.
   * 
   * @param e1
   *          value of value1
   * @param e2
   *          value of value2
   */
  public Pair(E1 e1, E2 e2) {
    this.firstValue = e1;
    this.secondValue = e2;
  }

  /**
   * Creates a new pair from the elements of a given pair.
   * 
   * @param pair
   *          the pair
   */
  public Pair(Pair<? extends E1, ? extends E2> pair) {
    this.firstValue = pair.getFirstValue();
    this.secondValue = pair.getSecondValue();
  }

  /**
   * Instantiates a new pair (null,null).
   */
  public Pair() {
  }

  /**
   * Gets the first value.
   * 
   * @return the first value
   */
  public E1 getFirstValue() {
    return firstValue;
  }

  /**
   * Gets the second value.
   * 
   * @return the second value
   */
  public E2 getSecondValue() {
    return secondValue;
  }

  /**
   * Sets the first value.
   * 
   * @param firstValue
   *          the new first value
   */
  public void setFirstValue(E1 firstValue) {
    this.firstValue = firstValue;
  }

  /**
   * Sets the second value.
   * 
   * @param secondValue
   *          the new second value
   */
  public void setSecondValue(E2 secondValue) {
    this.secondValue = secondValue;
  }

  @Override
  public String toString() {
    return "(" + firstValue + "," + secondValue + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair<?, ?>)) {
      return false;
    }
    Pair<?, ?> p = (Pair<?, ?>) obj;
    return Comparators.equal(firstValue, p.getFirstValue())
        && Comparators.equal(secondValue, p.getSecondValue());
  }

  @Override
  public int hashCode() {
    return (firstValue == null ? 0 : firstValue.hashCode())
        ^ (secondValue == null ? 0 : secondValue.hashCode());
  }
}
