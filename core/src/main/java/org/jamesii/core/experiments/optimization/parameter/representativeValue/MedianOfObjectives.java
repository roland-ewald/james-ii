/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.Median;

/**
 * Calculates the median of optimisation objectives.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class MedianOfObjectives extends AbstractRepresentativeValueCalulator {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8574166364216386545L;

  @Override
  protected Double calculateRepresentativeValue(List<Double> objectiveValues) {
    return Median.median(objectiveValues);
  }
}
