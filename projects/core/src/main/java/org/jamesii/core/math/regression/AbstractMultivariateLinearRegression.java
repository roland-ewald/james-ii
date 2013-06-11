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
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;

/**
 * Abstract super class for linear regression with multiple independent
 * variables.
 * 
 * see http://en.wikipedia.org/wiki/Linear_regression
 * 
 * The static method here were moved form Jans MultivariateRegression class,
 * they are EXPERIMENTAL!!!!!!!!!!!!!!!!!!!!!!!!!! Do not use without having
 * fixed the implementation first ;-).
 * 
 * Formula to calculate standard error per coefficient taken from: J.
 * Bleymüller, G. Gehlert, "Statistische Formeln, Tabellen und Programme", 11.
 * Edition, pp 57-61, ISBN: 978 3 8006 3371 X
 * 
 * Note: If the number of observation is equal to the number of independent
 * variables (i.e. X is an n-n-matrix), there is an exact regression line.
 * Therefore the calculated standard error is zero. Nevertheless the calculated
 * regression line/coefficients may be not exactly due to precision errors.
 * 
 * Note: The classes creates defensive copies of the input data.
 * 
 * @author Rene Schulz
 * @author Jan Himmelspach
 * 
 */
public abstract class AbstractMultivariateLinearRegression implements
    IMultivariateLinearRegression {

  /** Stores the n known values for the dependent variable Y. */
  private Matrix mY;

  /**
   * Stores the p*n known values for the p independent variables x_1,..,x_p
   * (p>=1). Each list contain the n corresponding values for one variable.
   */
  private Matrix mX;

  /** Stores the p calculated coefficients in one column. */
  private Matrix mb;

  /** Value needed for calculation of standard error for each coefficient. */
  private Double standardErrorSESquare;

  /** Matrix needed for calculation of standard error for each coefficient. */
  private Matrix mStandardErrorMatrix;

  /** Matrix containing the standard errors per coefficient. */
  private Matrix mStandardErrorsPerCoefficient;

  /** Matrix containing the residuals. */
  private Matrix mResiduals;

  @Override
  public void setInputVariables(Matrix input) {
    reset();
    setX(input.copy());
  }

  @Override
  public void setInputVariables(List<List<Double>> input) {
    reset();
    setX(new Matrix(input.get(0).size(), input.size()));
    for (int row = 0; row < getX().getRows(); row++) {
      for (int col = 0; col < getX().getColumns(); col++) {
        double val = input.get(col).get(row);
        getX().setElement(row, col, val);
      }
    }
  }

  @Override
  public void setResponseVariable(Matrix response) {
    reset();
    setY(response.copy());
  }

  @Override
  public void setResponseVariable(List<Double> response) {
    reset();
    setY(new Matrix(response.toArray(new Double[response.size()])));
  }

  /**
   * Reset all calculated results in case of new input or response variables.
   * 
   */
  protected void reset() {
    setB(null);
    mResiduals = null;
    setStandardErrorMatrix(null);
    standardErrorSESquare = null;
    mStandardErrorsPerCoefficient = null;
  }

  @Override
  public Matrix getResiduals() {
    if (mResiduals == null) {
      if (getB() == null) {
        getCoefficients();
      }
      Matrix estimations = getX().mult(getB());
      mResiduals = getY().copy();
      mResiduals.sub(estimations);
    }
    return mResiduals.copy();
  }

  @Override
  public List<Double> getResidualsList() {
    getResiduals();
    List<Double> result = new LinkedList<>();
    for (int i = 0; i < mResiduals.getRows(); i++) {
      result.add(mResiduals.getElement(i, 0));
    }
    return result;
  }

  @Override
  public List<Double> getStandardErrorForCoefficientsList() {
    getStandardErrorForCoefficients();
    List<Double> result = new LinkedList<>();
    for (int i = 0; i < getB().getRows(); i++) {
      result.add(mStandardErrorsPerCoefficient.getElement(i, 0));
    }
    return result;
  }

  @Override
  public Matrix getStandardErrorForCoefficients() {
    if (mStandardErrorsPerCoefficient == null) {
      if (getB() == null) {
        getCoefficients();
      }

      // calculate standardErrorSESquare.
      standardErrorSESquare = 0.0;
      int n = getY().getRows();
      int p = getX().getColumns();

      // If there are no free degrees, there should be no error in the
      // regression.
      if (n - p == 0) {
        mStandardErrorsPerCoefficient = new Matrix(p, 1);
        mStandardErrorsPerCoefficient.flood(0.0);
        // Free the possibly huge standardErrorMatrix.
        setStandardErrorMatrix(null);
        return mStandardErrorsPerCoefficient.copy();
      }

      for (int i = 0; i < n; i++) {
        double temp = getY().getElement(i, 0);
        standardErrorSESquare += temp * temp;
      }
      for (int j = 0; j < p; j++) {
        double temp = 0.0;
        for (int i = 0; i < n; i++) {
          temp += getX().getElement(i, j) * getY().getElement(i, 0);
        }
        temp *= getB().getElement(j, 0);
        standardErrorSESquare -= temp;
      }
      standardErrorSESquare /= n - p;

      if (getStandardErrorMatrix() == null) {
        setStandardErrorMatrix(getX().transpose().mult(getX()).solve());
      }

      // Calculate standard errors.
      mStandardErrorsPerCoefficient = new Matrix(p, 1);
      for (int i = 0; i < p; i++) {
        mStandardErrorsPerCoefficient.setElement(
            i,
            0,
            Math.sqrt(standardErrorSESquare
                * getStandardErrorMatrix().getElement(i, i)));
      }
    }
    // Free the possibly huge standardErrorMatrix.
    setStandardErrorMatrix(null);
    return mStandardErrorsPerCoefficient.copy();
  }

  /**
   * Computes the corrected quality of a regression result (R�) (R� - k/(n-1)) *
   * ((n -1) / (n-k-1))
   * 
   * @param r
   *          R? value
   * @param x
   *          Matrix of independent variables
   * @return corrected R?
   */
  public static double correctedRegressVariance(double r, Matrix x) {

    double k = x.getColumns();
    double n = x.getRows();

    return (r - (k / (n - 1))) * ((n - 1) / (n - k - 1));
  }

  /**
   * Computes the corrected quality of a regression result (R�) (R� - k/(n-1)) *
   * ((n -1) / (n-k-1)).
   * 
   * @param x
   *          Matrix of independent variables
   * @param b
   *          Matrix of coefficients
   * @param y
   *          the y
   * 
   * @return corrected R?
   */
  public static double correctedRegressVariance(Matrix x, Matrix y, Matrix b) {

    double R = regressVariance(x, y, b);

    return correctedRegressVariance(R, x);
  }

  /**
   * Computes the quality of a regression result (R�).
   * 
   * @param x
   *          Matrix of independent variables
   * @param b
   *          Matrix of coefficients
   * @param y
   *          the y
   * 
   * @return R?
   */
  public static double regressVariance(Matrix x, Matrix y, Matrix b) {
    Matrix bT = b.transpose();
    Matrix xT = x.transpose();
    Matrix yT = y.transpose();

    double meanY = ArithmeticMean.arithmeticMean(y.getColumn(0));
    meanY *= meanY;
    meanY *= y.getRows();

    Matrix top = bT.mult(xT).mult(y);
    Matrix bottom = yT.mult(y);

    return (top.getElement(0, 0) - meanY) / (bottom.getElement(0, 0) - meanY);
  }

  /**
   * Significance.
   * 
   * @param r
   *          R? value
   * @param x
   *          matrix of independent values
   * 
   * @return the double
   */
  public static double significance(double r, Matrix x) {
    double k = x.getColumns();
    double n = x.getRows();
    return (r / (k - 1)) / ((1 - r) / (n - k));
  }

  /**
   * Significance.
   * 
   * @param x
   *          the x
   * @param y
   *          the y
   * @param b
   *          the b
   * 
   * @return the double
   */
  public static double significance(Matrix x, Matrix y, Matrix b) {
    double r = regressVariance(x, y, b);

    return significance(r, x);
  }

  /**
   * @return the b
   */
  protected Matrix getB() {
    return mb;
  }

  /**
   * @param b
   *          the b to set
   */
  protected void setB(Matrix b) {
    this.mb = b;
  }

  /**
   * @return the x
   */
  protected Matrix getX() {
    return mX;
  }

  /**
   * @param x
   *          the x to set
   */
  protected void setX(Matrix x) {
    mX = x;
  }

  /**
   * @return the standardErrorMatrix
   */
  protected Matrix getStandardErrorMatrix() {
    return mStandardErrorMatrix;
  }

  /**
   * @param standardErrorMatrix
   *          the standardErrorMatrix to set
   */
  protected void setStandardErrorMatrix(Matrix standardErrorMatrix) {
    this.mStandardErrorMatrix = standardErrorMatrix;
  }

  /**
   * @return the y
   */
  protected Matrix getY() {
    return mY;
  }

  /**
   * @param y
   *          the y to set
   */
  protected void setY(Matrix y) {
    mY = y;
  }
}
