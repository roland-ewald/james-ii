/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.regression;

import org.jamesii.core.math.regression.MultivariateLinearRegressionLeastSquares;

/**
 * Test for {@link MultivariateLinearRegressionLeastSquares}
 * 
 * @author Rene Schulz
 * 
 */
public class TestMultivariateLinearRegressionLeastSquares extends
    AbstractTestMultivariateLinearRegression {

  public TestMultivariateLinearRegressionLeastSquares() {
    reg = new MultivariateLinearRegressionLeastSquares();
  }
}
