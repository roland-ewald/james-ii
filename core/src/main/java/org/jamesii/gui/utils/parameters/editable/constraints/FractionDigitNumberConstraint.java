/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

import org.jamesii.core.util.IConstraint;

/**
 * Constraint of the number of digits in the fraction of a number.
 * 
 * @author Roland Ewald
 * 
 *         06.07.2007
 */
public class FractionDigitNumberConstraint extends DigitNumberConstraint {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5466821490003640359L;

  /**
   * Default constructor.
   * 
   * @param totalNum
   *          the total num
   */
  public FractionDigitNumberConstraint(int totalNum) {
    super(totalNum);
  }

  @Override
  public IConstraint<Number> getCopy() {
    return new FractionDigitNumberConstraint(getNumOfDigits());
  }

  @Override
  public boolean isFulfilled(Number value) {
    String stringValue = value.toString();
    String fraction = stringValue.substring(stringValue.indexOf('.'));
    int count = 0;
    for (int i = 0; i < fraction.length(); i++) {
      if (Character.isDigit(fraction.charAt(i))) {
        count++;
      }
      if (count > getNumOfDigits()) {
        return false;
      }
    }
    return true;
  }
}
