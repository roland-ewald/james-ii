/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class LongVariable.
 */
public class LongVariable extends QuantitativeBaseVariable<Long> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4255623442981944569L;

  /**
   * Instantiates a new long variable.
   */
  public LongVariable() {
    super("", false, (long) 1);
  }

  /**
   * Instantiates a new long variable.
   * 
   * @param value
   *          the initial value
   */
  public LongVariable(long value) {
    super("", false, 0l);
    setValue(value);
  }

  /**
   * Instantiates a new long variable.
   * 
   * @param name
   *          the name
   * @param value
   *          the initial value
   */
  public LongVariable(String name, long value) {
    super(name, false, 0l);
    setValue(value);
  }

  /**
   * The Constructor.
   * 
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   * @param name
   *          the name
   */
  public LongVariable(String name, boolean ratio, long sw) {
    super(name, ratio, sw);
  }

  @Override
  public Long modifyVariable(double factor) {
    return Math.round(getValue() + factor * getStepWidth());
  }

  @Override
  public void setRandomValue() {
    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    long rand1 = r1.nextLong();

    if (getLowerBound() == null) {
      setLowerBound(Long.MIN_VALUE / 2);
    }

    if (getUpperBound() == null) {
      setUpperBound(Long.MAX_VALUE / 2);
    }

    rand1 = rand1 % (getUpperBound() - getLowerBound());

    if (rand1 < 0) {
      rand1 += getUpperBound() - getLowerBound();
    }

    rand1 += getLowerBound();

    setValue(rand1);
  }
}
