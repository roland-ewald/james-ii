/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.regression;

import java.util.LinkedList;
import java.util.List;

import org.jamesii.core.math.Matrix;

/**
 * Method of ordinary least squares for linear regression.
 * 
 * see http://en.wikipedia.org/wiki/Ordinary_least_squares
 * 
 * Algorithm: vector_of_coefficients = Inv(XT*X)*XT*Y
 * 
 * X - Matrix of input variables. XT - X transposed. Inv() - calculate the
 * inverse matrix. Y - vector of output variables.
 * 
 * Note: the input matrix X must have full column rank, i.e. the columns of this
 * matrix must be linearly independent!
 * 
 * Note: To calculate the standard error for each coefficient a matrix already
 * used to calculate the coefficients is needed. Therefore this implementation
 * stores a possibly huge matrix after getCoefficients() until
 * getStandardErrorForEachCoefficient() is called.
 * 
 * 
 * @author Rene Schulz
 * 
 */
public class MultivariateLinearRegressionLeastSquares extends
    AbstractMultivariateLinearRegression {

  @Override
  public List<Double> getCoefficientsList() {
    getCoefficients();
    List<Double> coef = new LinkedList<>();
    for (int i = 0; i < getB().getRows(); i++) {
      coef.add(getB().getElement(i, 0));
    }
    return coef;
  }

  @Override
  public Matrix getCoefficients() {
    if (getX() == null || getY() == null) {
      throw new RuntimeException(
          "Input and response variables must be set before regression can be calculated!");
    }
    if (getB() == null) {
      // Calculate coefficients.
      Matrix xT = getX().transpose();
      setB(xT.mult(getX()).solve());
      // Save this result for the calculation of the standard error for each
      // coefficient.
      setStandardErrorMatrix(getB().copy());
      setB(getB().mult(xT).mult(getY()));
    }
    return getB().copy();
  }
}
