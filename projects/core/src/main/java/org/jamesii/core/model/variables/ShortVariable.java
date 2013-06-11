/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import org.jamesii.SimSystem;
import org.jamesii.core.math.Calc;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class ShortVariable.
 */
public class ShortVariable extends QuantitativeBaseVariable<Short> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8443524460339351774L;

  /**
   * Instantiates a new short variable.
   */
  public ShortVariable() {
    super("", false, (short) 1);
  }

  /**
   * Default constructor.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   */
  public ShortVariable(String name, boolean ratio, short sw) {
    super(name, ratio, sw);
  }

  @Override
  public Short modifyVariable(double factor) {
    long newVal = Math.round(getValue() + factor * getStepWidth());
    if (newVal < Short.MIN_VALUE || newVal > Short.MAX_VALUE) {
      throw new OutOfBoundsException("Value '" + newVal
          + "' is out of the bounds of Short.");
    }
    return (short) newVal;
  }

  @Override
  public void setRandomValue() {
    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    double rand1 = r1.nextDouble();

    if (getLowerBound() == null) {
      setLowerBound(Short.valueOf(Short.MIN_VALUE));
    }

    if (getUpperBound() == null) {
      setUpperBound(Short.valueOf(Short.MAX_VALUE));
    }

    Short lowB = Short.valueOf(getLowerBound().shortValue());
    Short upB = Short.valueOf(getUpperBound().shortValue());

    upB = Calc.subtract(upB, lowB);
    Object upBtmp = Calc.multiply(upB, rand1);

    upB = (Short) upBtmp;

    Short newValue = Calc.add(lowB, upB);

    setValue(newValue);
  }

}
