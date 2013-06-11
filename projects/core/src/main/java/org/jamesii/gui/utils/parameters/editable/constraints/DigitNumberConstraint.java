/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

import org.jamesii.core.util.IConstraint;

/**
 * Constraint on the number's number of digits.
 * 
 * Date: 06.07.2007
 * 
 * @author Roland Ewald
 * 
 * 
 */
public class DigitNumberConstraint implements IConstraint<Number> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3123557118424035439L;

  /** Maximum number of total digits. */
  private int numOfDigits = 0;

  /**
   * Default constructor.
   * 
   * @param num
   *          number of total digits
   */
  public DigitNumberConstraint(int num) {
    this.numOfDigits = num;
  }

  /**
   * Gets the copy.
   * 
   * @return the copy
   * 
   * @see org.jamesii.core.util.IConstraint#getCopy()
   */
  @Override
  public IConstraint<Number> getCopy() {
    return new DigitNumberConstraint(numOfDigits);
  }

  /**
   * Gets the num of digits.
   * 
   * @return the num of digits
   */
  public int getNumOfDigits() {
    return numOfDigits;
  }

  /**
   * Checks if is fulfilled.
   * 
   * @param value
   *          the value
   * 
   * @return true, if checks if is fulfilled
   * 
   * @see org.jamesii.core.util.IConstraint#isFulfilled(java.lang.Object)
   */
  @Override
  public boolean isFulfilled(Number value) {

    int count = 0;
    String stringValue = value.toString();
    for (int i = 0; i < stringValue.length(); i++) {
      if (Character.isDigit(stringValue.charAt(i))) {
        count++;
      }
      if (count > numOfDigits) {
        return false;
      }
    }

    return true;
  }

  /**
   * Sets the number of digits.
   * 
   * @param num
   *          the new number of digits
   */
  public void setNumOfDigits(int num) {
    this.numOfDigits = num;
  }
}
