/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.math.parsetree.INode;

/**
 * Modifies a double value with a mathematical expression.
 * 
 * @author Stefan Rybacki
 */
public class MathModifierDouble extends MathModifier<Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8905719335807845362L;

  public MathModifierDouble(INode mathExpression) {
    super(mathExpression);
  }

  @Override
  protected boolean isFinished(Double value) {
    return false;
  }

}
