package org.jamesii.core.math.random.generators;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents the period of a random number generator. Such a period is only
 * approximated and should only be used for a rough measure on how long the
 * period is and to compare different generators. The period consists of a
 * multiplier <i>m</i>, a base <i>b</i> and an exponent <i>e</i> in the
 * following form:
 * <p>
 * <i>m</i> ∙ <i>b</i><sup><i>e</i></sup>.
 */
public class RNGPeriod implements Comparable<RNGPeriod>, Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1121037085435360683L;

  /** The multiplier of the period, i. e., <b>3.138</b> ∙ 10<sup>57</sup>. */
  private double multiplier;

  /** The base of the period, i. e., 3.138 ∙ <b>10</b><sup>57</sup>. */
  private int base;

  /** The exponent of the period, i. e., 3.138 ∙ 10<sup><b>57</b></sup>. */
  private int exponent;

  /**
   * Convenience constructor for periods like 2<sup>42</sup> or 10<sup>23</sup>.
   * 
   * @param base
   *          The base, usually 2 or 10.
   * @param exponent
   *          The exponent.
   */
  public RNGPeriod(int base, int exponent) {
    this(1, base, exponent);
  }

  /**
   * Constructor for periods like 3.14 ∙ 10<sup>31</sup>.
   * 
   * @param multiplier
   *          The multiplier. 3.14 in the above example.
   * @param base
   *          The base. 10 in the above example.
   * @param exponent
   *          The exponent. 31 in the above example.
   */
  public RNGPeriod(double multiplier, int base, int exponent) {
    super();
    this.multiplier = multiplier;
    this.base = base;
    this.exponent = exponent;
  }

  /**
   * Get the multiplier.
   * 
   * @return the multiplier
   */
  public double getMultiplier() {
    return multiplier;
  }

  /**
   * Set the new multiplier.
   * 
   * @param multiplier
   *          the multiplier to set
   */
  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  /**
   * Get the base.
   * 
   * @return the base
   */
  public int getBase() {
    return base;
  }

  /**
   * Sets the new base.
   * 
   * @param base
   *          the base to set
   */
  public void setBase(int base) {
    this.base = base;
  }

  /**
   * Get the exponent.
   * 
   * @return the exponent
   */
  public int getExponent() {
    return exponent;
  }

  /**
   * Set the new exponent.
   * 
   * @param exponent
   *          the exponent to set
   */
  public void setExponent(int exponent) {
    this.exponent = exponent;
  }

  @Override
  public String toString() {
    return (multiplier != 1 ? String.format("%.0f", multiplier) + " × " : "")
        + base + "^" + exponent;
  }

  @Override
  public int compareTo(RNGPeriod y) {
    BigDecimal a =
        new BigDecimal(getBase()).pow(getExponent()).multiply(
            new BigDecimal(getMultiplier()));
    BigDecimal b =
        new BigDecimal(y.getBase()).pow(y.getExponent()).multiply(
            new BigDecimal(y.getMultiplier()));
    return a.compareTo(b);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RNGPeriod)) {
      return false;
    }
    RNGPeriod y = (RNGPeriod) obj;
    BigDecimal a =
        new BigDecimal(getBase()).pow(getExponent()).multiply(
            new BigDecimal(getMultiplier()));
    BigDecimal b =
        new BigDecimal(y.getBase()).pow(y.getExponent()).multiply(
            new BigDecimal(y.getMultiplier()));
    return a.equals(b);
  }

  @Override
  public int hashCode() {
    BigDecimal a =
        new BigDecimal(getBase()).pow(getExponent()).multiply(
            new BigDecimal(getMultiplier()));
    return a.hashCode();
  }
}