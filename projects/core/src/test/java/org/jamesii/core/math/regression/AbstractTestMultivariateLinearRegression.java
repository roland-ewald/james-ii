/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.regression;

import java.util.LinkedList;
import java.util.List;

import org.jamesii.core.math.regression.IMultivariateLinearRegression;
import org.jamesii.core.math.statistics.StatisticsTest;

import junit.framework.TestCase;

/**
 * Unit test for all classes of {@link IMultivariateLinearRegression}.
 * 
 * Test data are validated with R (http://cran.r-project.org/).
 * 
 * @author Rene Schulz
 */
public abstract class AbstractTestMultivariateLinearRegression extends TestCase {

  /** Object to test */
  protected IMultivariateLinearRegression reg;

  /** Allowed difference between the results and the exact solution. */
  private double epsilon = StatisticsTest.EPSILON;

  /**
   * Simple test with Y=0.5+X1 and 3 datapoints. Also checks the calculated
   * residuals.
   */
  public void testSimpleLinearRegressionExcact() {
    List<Double> X0 = new LinkedList<>();
    for (int i = 0; i < 3; i++) {
      X0.add(1.0);
    }

    List<Double> Y = new LinkedList<>();
    List<Double> X1 = new LinkedList<>();
    Y.add(-0.5);
    X1.add(-1.0);
    Y.add(2.5);
    X1.add(2.0);
    Y.add(3.5);
    X1.add(3.0);
    List<List<Double>> X = new LinkedList<>();
    X.add(X0);
    X.add(X1);
    reg.setInputVariables(X);
    reg.setResponseVariable(Y);
    List<Double> coef = reg.getCoefficientsList();
    List<Double> errors = reg.getStandardErrorForCoefficientsList();
    List<Double> res = reg.getResidualsList();

    assertEquals("Wrong number of coefficients!", 2, coef.size());
    assertEquals("Wrong coefficient!", 0.5, coef.get(0), epsilon);
    assertEquals("Wrong coefficient!", 1.0, coef.get(1), epsilon);

    assertEquals("Wrong number of standard errors per coefficient!", 2,
        errors.size());
    assertEquals("Wrong standard error per coefficient!", 0.0, errors.get(0),
        epsilon);
    assertEquals("Wrong tandard error per coefficient!", 0.0, errors.get(1),
        epsilon);

    assertEquals("Wrong number of residuals!", 3, res.size());
    assertEquals("Wrong residual!", 0.0, res.get(0), epsilon);
    assertEquals("Wrong residual!", 0.0, res.get(1), epsilon);
    assertEquals("Wrong residual!", 0.0, res.get(2), epsilon);
  }

  /**
   * Simple test with Y=0.25+2*X1+4.5*X2-3*X3+4*X4 and 5 datapoints, so standard
   * error per coefficient should be exactly 0.0.
   */
  public void testLinearRegressionExact() {
    List<Double> X0 = new LinkedList<>();
    for (int i = 0; i < 5; i++) {
      X0.add(1.0);
    }

    List<Double> X1 = new LinkedList<>();
    X1.add(-1.0);
    X1.add(2.0);
    X1.add(0.1);
    X1.add(3.0);
    X1.add(3.0);

    List<Double> X2 = new LinkedList<>();
    X2.add(-1.0);
    X2.add(3.0);
    X2.add(2.0);
    X2.add(2.0);
    X2.add(4.0);

    List<Double> X3 = new LinkedList<>();
    X3.add(-1.0);
    X3.add(4.0);
    X3.add(8.0);
    X3.add(6.0);
    X3.add(5.0);

    List<Double> X4 = new LinkedList<>();
    X4.add(-1.0);
    X4.add(5.0);
    X4.add(9.0);
    X4.add(8.0);
    X4.add(5.0);

    List<Double> Y = new LinkedList<>();
    Y.add(-11.25);
    Y.add(45.75);
    Y.add(57.45);
    Y.add(61.25);
    Y.add(49.25);

    List<List<Double>> X = new LinkedList<>();
    X.add(X0);
    X.add(X1);
    X.add(X2);
    X.add(X3);
    X.add(X4);

    reg.setInputVariables(X);
    reg.setResponseVariable(Y);
    List<Double> coef = reg.getCoefficientsList();
    List<Double> errors = reg.getStandardErrorForCoefficientsList();
    assertEquals("Wrong number of coefficients!", 5, coef.size());
    assertEquals("Wrong coefficient!", 0.25, coef.get(0), epsilon);
    assertEquals("Wrong coefficient!", 2.0, coef.get(1), epsilon);
    assertEquals("Wrong coefficient!", 4.5, coef.get(2), epsilon);
    assertEquals("Wrong coefficient!", -3.0, coef.get(3), epsilon);
    assertEquals("Wrong coefficient!", 8.0, coef.get(4), epsilon);

    assertEquals("Wrong number of standard errors per coefficient!", 5,
        errors.size());
    for (int i = 0; i < errors.size(); i++) {
      assertEquals("Wrong standard error per coefficient!", 0.0, errors.get(i));
    }
  }

  /** Simple test with Y=3+2*X1+noise and 3 datapoints. */
  public void testSimpleLinearRegression() {
    List<Double> X0 = new LinkedList<>();
    for (int i = 0; i < 3; i++) {
      X0.add(1.0);
    }

    List<Double> Y = new LinkedList<>();
    List<Double> X1 = new LinkedList<>();
    Y.add(3.10);
    X1.add(0.0);
    Y.add(4.95);
    X1.add(1.0);
    Y.add(11.90);
    X1.add(4.0);
    List<List<Double>> X = new LinkedList<>();
    X.add(X0);
    X.add(X1);
    reg.setInputVariables(X);
    reg.setResponseVariable(Y);
    List<Double> coef = reg.getCoefficientsList();
    List<Double> errors = reg.getStandardErrorForCoefficientsList();

    assertEquals("Wrong number of coefficients!", 2, coef.size());
    assertEquals("Wrong coefficient!", 2.93846, coef.get(0), epsilon);
    assertEquals("Wrong coefficient!", 2.22692, coef.get(1), epsilon);

    assertEquals("Wrong number of standard errors per coefficient!", 2,
        errors.size());
    assertEquals("Wrong standard error per coefficient!", 0.22201,
        errors.get(0), epsilon);
    assertEquals("Wrong tandard error per coefficient!", 0.09326,
        errors.get(1), epsilon);
  }

  /**
   * Simple test with Y=0.25+2*X1+4.5*X2-3*X3+noise and 5 datapoints. Also
   * checks the calculated residuals.
   */
  public void testLinearRegression() {
    List<Double> X0 = new LinkedList<>();
    for (int i = 0; i < 5; i++) {
      X0.add(1.0);
    }

    List<Double> X1 = new LinkedList<>();
    X1.add(-1.0);
    X1.add(2.0);
    X1.add(0.1);
    X1.add(3.0);
    X1.add(3.0);

    List<Double> X2 = new LinkedList<>();
    X2.add(-1.0);
    X2.add(3.0);
    X2.add(2.0);
    X2.add(2.0);
    X2.add(4.0);

    List<Double> X3 = new LinkedList<>();
    X3.add(-1.0);
    X3.add(4.0);
    X3.add(8.0);
    X3.add(6.0);
    X3.add(5.0);

    List<Double> Y = new LinkedList<>();
    Y.add(-3.15);
    Y.add(5.78);
    Y.add(-14.45);
    Y.add(-2.75);
    Y.add(9.15);

    List<List<Double>> X = new LinkedList<>();
    X.add(X0);
    X.add(X1);
    X.add(X2);
    X.add(X3);

    reg.setInputVariables(X);
    reg.setResponseVariable(Y);
    List<Double> coef = reg.getCoefficientsList();
    List<Double> errors = reg.getStandardErrorForCoefficientsList();
    List<Double> res = reg.getResidualsList();

    assertEquals("Wrong number of coefficients!", 4, coef.size());
    assertEquals("Wrong coefficient!", 0.31735, coef.get(0), epsilon);
    assertEquals("Wrong coefficient!", 1.97063, coef.get(1), epsilon);
    assertEquals("Wrong coefficient!", 4.47635, coef.get(2), epsilon);
    assertEquals("Wrong coefficient!", -2.98917, coef.get(3), epsilon);

    assertEquals("Wrong number of standard errors per coefficient!", 4,
        errors.size());
    assertEquals("Wrong standard error per coefficient!", 0.05089,
        errors.get(0), epsilon);
    assertEquals("Wrong standard error per coefficient!", 0.03112,
        errors.get(1), epsilon);
    assertEquals("Wrong standard error per coefficient!", 0.03563,
        errors.get(2), epsilon);
    assertEquals("Wrong standard error per coefficient!", 0.01300,
        errors.get(3), epsilon);

    assertEquals("Wrong number of residuals!", 5, res.size());
    assertEquals("Wrong residual!", -0.009532, res.get(0), epsilon);
    assertEquals("Wrong residual!", 0.049011, res.get(1), epsilon);
    assertEquals("Wrong residual!", -0.003753, res.get(2), epsilon);
    assertEquals("Wrong residual!", 0.003077, res.get(3), epsilon);
    assertEquals("Wrong residual!", -0.038804, res.get(4), epsilon);
  }

  /**
   * Test with values from testLinearRegressionExact with additional noise and 1
   * variable left out. So the given input data do not form a line.
   * 
   * Note: here is a greater epsilon from test results and calculated results
   * allowed due to the "chaotic" input.
   */
  public void testLinearRegressionNonLinearData() {
    List<Double> X0 = new LinkedList<>();
    for (int i = 0; i < 5; i++) {
      X0.add(1.0);
    }

    List<Double> X1 = new LinkedList<>();
    X1.add(-1.0);
    X1.add(2.0);
    X1.add(0.1);
    X1.add(3.0);
    X1.add(3.0);

    List<Double> X2 = new LinkedList<>();
    X2.add(-1.0);
    X2.add(3.0);
    X2.add(2.0);
    X2.add(2.0);
    X2.add(4.0);

    List<Double> X3 = new LinkedList<>();
    X3.add(-1.0);
    X3.add(4.0);
    X3.add(8.0);
    X3.add(6.0);
    X3.add(5.0);

    List<Double> Y = new LinkedList<>();
    Y.add(-11.30);
    Y.add(45.85);
    Y.add(57.48);
    Y.add(61.25);
    Y.add(49.15);

    List<List<Double>> X = new LinkedList<>();
    X.add(X0);
    X.add(X1);
    X.add(X2);
    X.add(X3);

    reg.setInputVariables(X);
    reg.setResponseVariable(Y);
    List<Double> coef = reg.getCoefficientsList();
    List<Double> errors = reg.getStandardErrorForCoefficientsList();
    assertEquals("Wrong number of coefficients!", 4, coef.size());
    // Allow greater Epsilon for this test.
    assertEquals("Wrong coefficient!", 2.1758, coef.get(0), 100 * epsilon);
    assertEquals("Wrong coefficient!", 5.8685, coef.get(1), 100 * epsilon);

    assertEquals("Wrong coefficient!", -0.3704, coef.get(2), 100 * epsilon);
    assertEquals("Wrong coefficient!", 6.9813, coef.get(3), 100 * epsilon);

    assertEquals("Wrong number of standard errors per coefficient!", 4,
        errors.size());
    assertEquals("Wrong standard error per coefficient!", 5.3201,
        errors.get(0), 10 * epsilon);
    assertEquals("Wrong standard error per coefficient!", 3.2529,
        errors.get(1), 10 * epsilon);
    assertEquals("Wrong standard error per coefficient!", 3.7245,
        errors.get(2), 10 * epsilon);
    assertEquals("Wrong standard error per coefficient!", 1.3588,
        errors.get(3), 10 * epsilon);
  }
}
