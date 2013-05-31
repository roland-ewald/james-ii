/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.util.List;

/**
 * Returns the worst objective. This is the maximum objective (i.e..,
 * optimisation minimises objective function).
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class WorstOfObjectives extends AbstractRepresentativeValueCalulator {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2698386956838009223L;

  @Override
  protected Double calculateRepresentativeValue(List<Double> objectives) {
    double result = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < objectives.size() - 1; i++) {
      if (result < objectives.get(i)) {
        result = objectives.get(i);
      }
    }
    return result;
  }

}
