/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.regression;

import java.util.List;

import org.jamesii.core.math.Matrix;

/**
 * Interface for linear regression with multiple independent variables.
 * 
 * Note: if there is only one input (dependent) variable, a simple linear
 * regression might produce more accurate solutions.
 * 
 * Input:
 * 
 * - a vector y=(y_1,...,y_n) of n observations of an independent/response
 * variable Y
 * 
 * - an n-p-matrix x=[X_1|...|X_p] containing the n observations
 * X_i=(x_i1,...,x_in) for each of the p dependent/input variables.
 * 
 * Output:
 * 
 * - p coefficients b_1,...,b_p so that x*b is as close as possible to y.
 * 
 * @author Rene Schulz
 */
public interface IMultivariateLinearRegression {

  /**
   * Apply the linear regression and return the approximative coefficients.
   * 
   * @return the approximative coefficients b_1,...,b_p.
   */
  Matrix getCoefficients();

  /**
   * Apply the linear regression and return the approximative coefficients.
   * 
   * @return the approximative coefficients b_1,...,b_p.
   */
  List<Double> getCoefficientsList();

  /**
   * Return the residuals (i.e. observed value - estimated value).
   * 
   * @return the residuals
   */
  Matrix getResiduals();

  /**
   * Return the residuals (i.e. observed value - estimated value).
   * 
   * @return the residuals
   */
  List<Double> getResidualsList();

  /**
   * Calculate the standard errors for each coefficient b.
   * 
   * @return standard error for the given coefficient.
   */
  Matrix getStandardErrorForCoefficients();

  /**
   * Calculate the standard errors for each coefficient b.
   * 
   * @return standard error for the given coefficient.
   */
  List<Double> getStandardErrorForCoefficientsList();

  /**
   * Set the inputVariables.
   * 
   * @param input
   *          the p*n known values for the p independent variables x_1,..,x_p
   *          (p>=1). Each column contain the n corresponding values for one
   *          variable.
   */
  void setInputVariables(Matrix input);

  /**
   * Set the input variables.
   * 
   * @param input
   *          the p*n known values for the p independent variables x_1,..,x_p
   *          (p>=1). Each list contain the n corresponding values for one
   *          variable.
   */
  void setInputVariables(List<List<Double>> input);

  /**
   * Set the response variable.
   * 
   * @param response
   *          the n known values for the dependent variable Y
   */
  void setResponseVariable(List<Double> response);

  /**
   * Set the response variable.
   * 
   * @param response
   *          the n known values for the dependent variable Y (in one column)
   */
  void setResponseVariable(Matrix response);

}
