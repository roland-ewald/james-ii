/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

/**
 * Simple bound description for real-valued parameters.
 * 
 * @author Roland Ewald
 * 
 */
public class DoubleParameterBounds implements IParameterBounds<Double> {

  /** The lower bound. */
  private Double lowerBound = 0.0;

  /** The upper bound. */
  private Double upperBound = Double.MAX_VALUE;

  @Override
  public Double getLowerBound() {
    return lowerBound;
  }

  @Override
  public Double getUpperBound() {
    return upperBound;
  }

  @Override
  public void setLowerBound(Double bound) {
    lowerBound = bound;
  }

  @Override
  public void setUpperBound(Double bound) {
    upperBound = bound;
  }

}
