/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

import org.jamesii.core.util.IConstraint;

/**
 * Constraints the format of a string.
 * 
 * Created on June 2, 2004
 * 
 * @author Roland Ewald
 */
public class StringConstraint implements IConstraint<String> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7942858242373965582L;

  /** Maximum length of a string. */
  private int maxLength = Integer.MAX_VALUE;

  /** Flag that indicates that a maximum length constraint will be checked. */
  private boolean maxLengthChecking = false;

  /** Minimum length of a string. */
  private int minLength = 0;

  /** Flag that indicates that a minimum length constraint will be checked. */
  private boolean minLengthChecking = false;

  @Override
  public IConstraint<String> getCopy() {
    StringConstraint sConstr = new StringConstraint();
    sConstr.setMaxLength(getMaxLength());
    sConstr.setMinLength(getMinLength());
    sConstr.setMaxLengthChecking(isMaxLengthChecking());
    sConstr.setMinLengthChecking(isMinLengthChecking());
    return sConstr;
  }

  /**
   * Gets the max length.
   * 
   * @return the max length
   */
  public int getMaxLength() {
    return maxLength;
  }

  /**
   * Gets the min length.
   * 
   * @return the min length
   */
  public int getMinLength() {
    return minLength;
  }

  /**
   * Check constraint.
   * 
   * @param value
   *          value to be checked
   * 
   * @return true if string fulfills constraint
   */
  @Override
  public boolean isFulfilled(String value) {

    if (value == null) {
      value = "";
    }

    // Testing all constraints
    if (isMaxLengthChecking() && getMaxLength() < value.length()) {
      return false;
    }

    if (isMinLengthChecking() && getMinLength() > value.length()) {
      return false;
    }

    return true;
  }

  /**
   * Checks if is max length checking.
   * 
   * @return true, if is max length checking
   */
  public boolean isMaxLengthChecking() {
    return maxLengthChecking;
  }

  /**
   * Checks if is min length checking.
   * 
   * @return true, if is min length checking
   */
  public boolean isMinLengthChecking() {
    return minLengthChecking;
  }

  /**
   * Sets the max length.
   * 
   * @param maxLen
   *          the new max length
   */
  public void setMaxLength(int maxLen) {
    maxLength = maxLen;
  }

  /**
   * Sets the max length checking.
   * 
   * @param maxLCheck
   *          the new max length checking
   */
  public void setMaxLengthChecking(boolean maxLCheck) {
    maxLengthChecking = maxLCheck;
  }

  /**
   * Sets the min length.
   * 
   * @param minLen
   *          the new min length
   */
  public void setMinLength(int minLen) {
    minLength = minLen;
  }

  /**
   * Sets the min length checking.
   * 
   * @param minLCheck
   *          the new min length checking
   */
  public void setMinLengthChecking(boolean minLCheck) {
    minLengthChecking = minLCheck;
  }
}
