/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.rootfinding;

import java.io.Serializable;

import org.jamesii.core.math.IFunction;

/**
 * AbstractRootFindingAlgorithm.
 * 
 * Abstract base class for root finding algorithms.
 * 
 * 
 * @author Matthias Jeschke
 * 
 */
public abstract class AbstractRootFindingAlgorithm implements
    IRootFindingAlgorithm, Serializable {

  /** Serialization ID. */
  private static final long serialVersionUID = 8126813072913551571L;

  /** Error control parameter. */
  private double epsilon = 1e-6;

  /** Maximum number of iterations. */
  private int maxIter = 100;

  /** Function to analyze. */
  private IFunction function = null;

  public AbstractRootFindingAlgorithm(IFunction function) {
    this.function = function;
  }

  public AbstractRootFindingAlgorithm(IFunction function, double epsilon,
      int maxIter) {
    this(function);
    this.epsilon = epsilon;
    this.setMaxIter(maxIter);
  }

  @Override
  public abstract double[] findRoot(double[]... startPoints);

  @Override
  public double getEpsilon() {
    return this.epsilon;
  }

  @Override
  public IFunction getFunction() {
    return this.function;
  }

  @Override
  public int getMaxIterations() {
    return this.getMaxIter();
  }

  @Override
  public void setEpsilon(double epsilon) {
    this.epsilon = epsilon;
  }

  @Override
  public void setMaxIterations(int maxIter) {
    this.setMaxIter(maxIter);
  }

  protected boolean checkAbortCondition(double[] xn) {
    for (int i = 0; i < xn.length; i++) {
      if (Math.abs(xn[i]) >= epsilon) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return the maxIter
   */
  protected int getMaxIter() {
    return maxIter;
  }

  /**
   * @param maxIter
   *          the maxIter to set
   */
  protected final void setMaxIter(int maxIter) {
    this.maxIter = maxIter;
  }
}
