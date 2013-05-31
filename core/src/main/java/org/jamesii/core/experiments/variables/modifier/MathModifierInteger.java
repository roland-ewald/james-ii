/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.math.parsetree.INode;

/**
 * Modifies an integer value with a mathematical expression.
 * 
 * @author Jan Himmelspach
 */
public class MathModifierInteger extends MathModifier<Integer> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8904719335807845362L;

  /**
   * Default constructor.
   * 
   * @param firstValue
   *          the start value
   * @param inc
   *          the increment
   * @param lastValue
   *          the last value (inclusive)
   */
  public MathModifierInteger(INode mathExpression) {
    super(mathExpression);
  }

  @Override
  protected boolean isFinished(Integer value) {
    return false;
  }

}
