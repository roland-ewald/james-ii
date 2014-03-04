/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

/**
 * Bounds of integer parameters.
 * 
 * @author Roland Ewald
 */
public class IntParameterBounds implements IParameterBounds<Integer> {

  /** The lower bound. */
  private Integer lowerBound = 0;

  /** The upper bound. */
  private Integer upperBound = Integer.MAX_VALUE;

  @Override
  public Integer getLowerBound() {
    return lowerBound;
  }

  @Override
  public Integer getUpperBound() {
    return upperBound;
  }

  @Override
  public void setLowerBound(Integer bound) {
    lowerBound = bound;
  }

  @Override
  public void setUpperBound(Integer bound) {
    upperBound = bound;
  }

}
