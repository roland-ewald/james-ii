/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Variable of type Double See ancestor (BaseVariable) for further details.
 * 
 * @author Jan Himmelspach
 */
public class DoubleVariable extends QuantitativeBaseVariable<Double> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5038754447791087936L;

  /**
   * Instantiates a new double variable.
   */
  public DoubleVariable() {
    super("", false, 1.0);
  }

  /**
   * Instantiates a new double variable.
   * 
   * @param name
   *          the name
   */
  public DoubleVariable(String name) {
    this();
    setName(name);
  }

  /**
   * Instantiates a new double variable.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   */
  public DoubleVariable(String name, boolean ratio, Double sw) {
    this(name, ratio, sw, 0.0);
  }

  /**
   * Instantiates a new double variable.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   * @param value
   *          the value
   */
  public DoubleVariable(String name, boolean ratio, Double sw, Double value) {
    super(name, ratio, sw);
    setValue(value);
  }

  /**
   * Instantiates a new double variable.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   * @param value
   *          the value
   */
  public DoubleVariable(String name, boolean ratio, Double value,
      Double lowBound, Double upBound, Double sw) {
    super(name, ratio, sw);
    setValue(value);
    setLowerBound(lowBound);
    setUpperBound(upBound);
  }

  @Override
  public Double modifyVariable(double factor) {
    return getValue() + factor * getStepWidth();
  }

  @Override
  public void setRandomValue() {

    if (getLowerBound() == null) {
      setLowerBound(Double.MIN_VALUE / 2);
    }

    if (getUpperBound() == null) {
      setUpperBound(Double.MAX_VALUE / 2);
    }

    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    double rand1 = r1.nextDouble();

    rand1 = getLowerBound() + (getUpperBound() - getLowerBound()) * rand1;

    this.setValue(rand1);
  }

}
