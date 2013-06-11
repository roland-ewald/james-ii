/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate.bootstrapping.sampling;

import java.util.List;

import org.jamesii.core.math.random.generators.IRandom;

public class SmoothBootstrapper extends CaseResamplingBootstrapper {

  private double h = 0.01;

  @Override
  protected double resample(List<? extends Number> list, IRandom random) {
    int r = random.nextInt(list.size());
    double value = list.get(r).doubleValue();
    double factor = (random.nextDouble() - 0.5) * 2;
    factor *= h * value;
    return value + factor;
  }

}
