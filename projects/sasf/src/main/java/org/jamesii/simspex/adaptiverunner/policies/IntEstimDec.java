/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * Same as (@link IntEstim) but parameter alpha increases with time so that the
 * influence of the deviation decreases.. Does not work with alpha > 0.5!
 * 
 * @author Rene Schulz
 * 
 */
public class IntEstimDec extends IntEstim {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4979625143354453292L;

  @Override
  protected double getCurrentAlpha() {
    if (getOverallPullCount() < getInitPhaseArm()) {
      return getAlpha();
    }

    int i = (getOverallPullCount() - getInitPhaseArm()) / 10;
    double a = getAlpha() * (i + 1);
    if (a > 0.5) {
      return 0.5;
    }

    return a;
  }

  /**
   * Set the exploration parameter called alpha. Has to be in (0,0.5], other
   * vales will be ignored.
   * 
   * @param alpha
   *          has to be in (0,1).
   */
  @Override
  protected void setAlpha(double alpha) {
    if ((0 < alpha) && (alpha <= 0.5)) {
      super.setAlpha(alpha);
    }
  }

}
