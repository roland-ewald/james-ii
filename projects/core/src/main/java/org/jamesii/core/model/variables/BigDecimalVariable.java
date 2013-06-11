/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import java.math.BigDecimal;

import org.jamesii.SimSystem;
import org.jamesii.core.math.Calc;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class BigDecimalVariable.
 */
public class BigDecimalVariable extends QuantitativeBaseVariable<BigDecimal> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4320183138716421581L;

  /**
   * Instantiates a new big decimal variable.
   */
  public BigDecimalVariable() {
    super("", false, BigDecimal.ONE);
  }

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   */
  public BigDecimalVariable(String name, boolean ratio, BigDecimal sw) {
    super(name, ratio, sw);
  }

  @Override
  public BigDecimal modifyVariable(double factor) {
    BigDecimal sw = new BigDecimal(getStepWidth().toString());
    BigDecimal factVal = new BigDecimal(Double.valueOf(factor).toString());
    BigDecimal addVal = sw.multiply(factVal);
    return getValue().add(addVal);
  }

  @Override
  public void setRandomValue() {
    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    double rand1 = r1.nextDouble();

    if (getLowerBound() == null) {
      setLowerBound(new BigDecimal(Double.MIN_VALUE));
    }

    if (getUpperBound() == null) {
      setUpperBound(new BigDecimal(Double.MAX_VALUE));
    }

    BigDecimal lowB = new BigDecimal(getLowerBound().doubleValue());
    BigDecimal upB = new BigDecimal(getUpperBound().doubleValue());

    upB = Calc.subtract(upB, lowB);
    Object upBtmp = Calc.multiply(upB, rand1);

    upB = (BigDecimal) upBtmp;

    BigDecimal newValue = lowB.add(upB);

    setValue(newValue);
  }
}
