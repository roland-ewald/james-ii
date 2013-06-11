/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.ArithmeticMean;

/**
 * Calculates the arithmetic mean of optimisation objectives.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class ArithmeticMeanOfObjectives extends
    AbstractRepresentativeValueCalulator {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9035341570411329788L;

  @Override
  protected Double calculateRepresentativeValue(List<Double> objectiveValues) {
    return ArithmeticMean.arithmeticMean(objectiveValues);
  }

}
