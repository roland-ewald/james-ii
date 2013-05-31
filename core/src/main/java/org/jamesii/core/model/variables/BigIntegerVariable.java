/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jamesii.SimSystem;
import org.jamesii.core.math.Calc;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class BigIntegerVariable.
 */
public class BigIntegerVariable extends QuantitativeBaseVariable<BigInteger> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7479676334700393160L;

  /**
   * Instantiates a new big integer variable.
   */
  public BigIntegerVariable() {
    super("", false, BigInteger.ONE);
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
  public BigIntegerVariable(String name, boolean ratio, BigInteger sw) {
    super(name, ratio, sw);
  }

  @Override
  public BigInteger modifyVariable(double factor) {
    BigDecimal sw = new BigDecimal(getStepWidth().toString());
    BigDecimal factVal = new BigDecimal(Double.valueOf(factor).toString());
    BigDecimal addVal = sw.multiply(factVal);
    return getValue().add(addVal.toBigInteger());
  }

  @Override
  public void setRandomValue() {
    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    double rand1 = r1.nextDouble();

    if (getLowerBound() == null) {
      setLowerBound(new BigInteger(String.valueOf(Long.MIN_VALUE)));
    }

    if (getUpperBound() == null) {
      setUpperBound(new BigInteger(String.valueOf(Long.MAX_VALUE)));
    }

    BigInteger lowB = new BigInteger(getLowerBound().toByteArray());
    BigInteger upB = new BigInteger(getUpperBound().toByteArray());

    upB = Calc.subtract(upB, lowB);
    Object upBtmp = Calc.multiply(upB, rand1);

    upB = (BigInteger) upBtmp;

    BigInteger newValue = lowB.add(upB);

    setValue(newValue);
  }
}
