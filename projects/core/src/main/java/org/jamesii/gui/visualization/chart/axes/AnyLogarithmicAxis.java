/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Class implementing a logarithmic axis where the logarithmic scale could start at any point on the axis between nin and max.
 * 
 */
package org.jamesii.gui.visualization.chart.axes;

/**
 * {@link IAxis} implementation providing a two sided logarithmic axis where one
 * can specify the point of interest which is being sample very high. Which
 * means the point of interest will be the value from where a logarithmic scale
 * in both directions is constructed.
 * 
 * @author Enrico Seib
 */
public class AnyLogarithmicAxis extends AbstractAxis {

  /**
   * Value where the logarithmic scale starts; default: Axis behaves like a
   * SimpleLogarithmicAxis
   */
  private double basePointValue = 0;

  /**
   * Standard constructor for this axis
   */
  public AnyLogarithmicAxis() {
  }

  /**
   * Constructor.
   * 
   * @param min
   *          Minumum value of the axis
   * @param max
   *          Maximum value of the axis
   * @param basePoint
   *          Value where logarithmic scale starts
   */
  public AnyLogarithmicAxis(double min, double max, double basePoint) {
    setMinimumMaximum(min, max);
    setLogStartPointValue(basePoint);
  }

  @Override
  public double transform(double value) {
    // all other cases
    /**
     * computes the logarithmic value of the base point of the axis domain
     */
    double logBasePointValue = Math.log(basePointValue - getMinimum() + 1);

    double scaleFactor =
        1 / (Math.log(getMaximum() - basePointValue + 1) + logBasePointValue);

    if (value >= basePointValue) {
      return (Math.log(value - basePointValue + 1) + logBasePointValue)
          * scaleFactor;
    }

    return (logBasePointValue - Math.log(basePointValue - value + 1))
        * scaleFactor;
  }

  /**
   * Returns the value where the logarithmic scale starts
   * 
   * @return value where logarithmic scale starts
   */
  public final double getLogStartPointValue() {
    return this.basePointValue;
  }

  /**
   * Sets value where the logarithmic scale should start
   * 
   * @param val
   *          value (between min and max of the axis) where the logarithmic
   *          scale should start
   */
  public final void setLogStartPointValue(double val) {
    if (val < this.getMinimum()) {
      throw new IllegalArgumentException("value should be >= min");
    }
    if (val > this.getMaximum()) {
      throw new IllegalArgumentException("value should be <= max");
    }

    this.basePointValue = val;

  }

  @Override
  public double transformInv(double value) {
    if (value < 0) {
      return getMinimum() - 1;
    }
    if (value > 1) {
      return getMaximum() + 1;
    }
    double logBasePointValue = Math.log(basePointValue - getMinimum() + 1);

    double scaleFactor =
        1 / (Math.log(getMaximum() - basePointValue + 1) + logBasePointValue);

    double x1 =
        Math.pow(Math.E, value / scaleFactor - logBasePointValue) - 1
            + basePointValue;

    double x2 =
        -Math.pow(Math.E, logBasePointValue - value / scaleFactor) + 1
            + basePointValue;

    if (Math.abs(transform(x1) - value) < 0.000001) {
      return x1;
    }

    if (Math.abs(transform(x2) - value) < 0.000001) {
      return x2;
    }

    return basePointValue;
  }

}
