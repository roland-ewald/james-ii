/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

/**
 * Auxiliary class to save the occurrence info of a parameter comfortably.
 * 
 * @author Roland Ewald
 */
public class OccurrenceInfo {

  /** Maximum occurrence. */
  private int max = Integer.MAX_VALUE;

  /** Minimum occurrence. */
  private int min = -1;

  /**
   * Default constructor.
   * 
   * @param minOccur
   *          minimum occurrence
   * @param maxOccur
   *          maximum occurrence
   */
  public OccurrenceInfo(int minOccur, int maxOccur) {
    min = minOccur;
    max = maxOccur;
  }

  /**
   * Gets the max.
   * 
   * @return the max
   */
  public int getMax() {
    return max;
  }

  /**
   * Gets the min.
   * 
   * @return the min
   */
  public int getMin() {
    return min;
  }

  /**
   * Checks for maximum.
   * 
   * @return true, if successful
   */
  public boolean hasMaximum() {
    return max < Integer.MAX_VALUE;
  }

  /**
   * Checks for minimum.
   * 
   * @return true, if successful
   */
  public boolean hasMinimum() {
    return min > -1;
  }

  /**
   * Sets the max.
   * 
   * @param max
   *          the new max
   */
  public void setMax(int max) {
    this.max = max;
  }

  /**
   * Sets the min.
   * 
   * @param min
   *          the new min
   */
  public void setMin(int min) {
    this.min = min;
  }
}
