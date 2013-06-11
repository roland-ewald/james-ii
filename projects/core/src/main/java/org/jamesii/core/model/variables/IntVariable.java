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
 * Variable of type Integer See ancestor (BaseVariable) for further details.
 * 
 * @author Jan Himmelspach
 */
public class IntVariable extends QuantitativeBaseVariable<Integer> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4542133078795296114L;

  /**
   * Instantiates a new int variable.
   */
  public IntVariable() {
    super("", false, 1);
  }

  /**
   * Instantiates a new int variable.
   * 
   * @param value
   *          the value
   */
  public IntVariable(Integer value) {
    this();
    setValue(value);
  }

  /**
   * Instantiates a new int variable.
   * 
   * @param name
   *          the name
   */
  public IntVariable(String name) {
    this();
    setName(name);
  }

  /**
   * Instantiates a new int variable.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   */
  public IntVariable(String name, boolean ratio, Integer sw) {
    super(name, ratio, sw);
  }

  /**
   * Instantiates a new int variable.
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public IntVariable(String name, Integer value) {
    this(name);
    setValue(value);
  }

  /**
   * Instantiates a new int variable.
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   * @param lowerBoud
   *          the lower boud
   * @param upperBoud
   *          the upper boud
   * @param sw
   *          the sw
   */
  public IntVariable(String name, Integer value, Integer lowerBoud,
      Integer upperBoud, Integer sw) {
    this(name, false, sw);
    setValue(value);
    setLowerBound(lowerBoud);
    setUpperBound(upperBoud);
  }

  @Override
  public Integer modifyVariable(double factor) {
    long newVal = Math.round(getValue() + factor * getStepWidth());
    if (newVal < Integer.MIN_VALUE || newVal > Integer.MAX_VALUE) {
      throw new OutOfBoundsException("Value '" + newVal
          + "' is out of the bounds of Integer.");
    }
    return (int) newVal;
  }

  @Override
  public void setRandomValue() {
    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    int rand1;
    int increment = 1;

    if (getUpperBound() == null) {
      setUpperBound(Integer.MAX_VALUE / 2);
      increment = 0;
    }
    if (getLowerBound() == null) {
      setLowerBound(Integer.MIN_VALUE / 2);
    }

    rand1 = r1.nextInt(getUpperBound() - getLowerBound() + increment);

    rand1 += getLowerBound();

    setValue(rand1);
  }

}
