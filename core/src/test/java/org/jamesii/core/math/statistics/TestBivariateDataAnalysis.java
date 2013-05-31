/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import org.jamesii.core.math.statistics.BivariateDataAnalysis;
import org.jamesii.core.math.statistics.univariate.Variance;

/**
 * Some simple tests for {@link BivariateDataAnalysis}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestBivariateDataAnalysis extends StatisticsTest {

  public void testCovarianceCalculation() {

    Double[] v1 = new Double[] { 1., 0. };
    Double[] v2 = new Double[] { 0., 1. };
    Double[] constV = new Double[] { 1., 1. };
    Double cov = BivariateDataAnalysis.covariance(v1, v2);
    assertNotNull(cov);
    assertEquals(-0.25, cov);
    assertEquals(0., BivariateDataAnalysis.covariance(v1, constV));
    assertEquals(0., BivariateDataAnalysis.covariance(constV, v2));

    // Variance estimation divides by N-1, has to be corrected here for checking
    // if Var(X)=Cov(X,X)
    assertEquals(Variance.variance(new double[] { 0., 1. }) / 2,
        BivariateDataAnalysis.covariance(v2, v2));
  }

  public void testCovarianceMatrixCalculation() {
    Double[][] values =
        new Double[][] { { 1., 0., 0.5 }, { 1., 1., 1. }, { 1., 0., 0.5 } };
    Double[][] covMatrix = BivariateDataAnalysis.covarianceMatrix(values);
    assertEquals(0., covMatrix[1][1]);
    assertEquals(0.5 / 3, covMatrix[0][0], EPSILON);
  }
}
