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
 * The Class FloatVariable.
 */
public class FloatVariable extends QuantitativeBaseVariable<Float> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1416199495429719855L;

  /**
   * Instantiates a new float variable.
   */
  public FloatVariable() {
    super("", false, (float) 1.0);
  }

  /**
   * Instantiates a new float variable.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   */
  public FloatVariable(String name, boolean ratio, Float sw) {
    super(name, ratio, sw);
  }

  @Override
  public Float modifyVariable(double factor) {
    double newVal = getValue() + factor * getStepWidth();
    if (newVal < Float.MIN_VALUE || newVal > Float.MAX_VALUE) {
      throw new OutOfBoundsException("Value '" + newVal
          + "' is out of the bounds of Float.");
    }
    return (float) newVal;
  }

  @Override
  public void setRandomValue() {

    if (getLowerBound() == null) {
      setLowerBound(Float.MIN_VALUE / 2);
    }

    if (getUpperBound() == null) {
      setUpperBound(Float.MAX_VALUE / 2);
    }

    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    float rand1 = r1.nextFloat();

    setValue(getLowerBound() + (getUpperBound() - getLowerBound()) * rand1);
  }
}
