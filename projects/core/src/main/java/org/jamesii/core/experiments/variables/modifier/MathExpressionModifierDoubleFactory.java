/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.modifier.plugintype.VariableModifierFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating MathModifierDouble objects.
 * 
 * @author Jan Himmelspach
 */
public class MathExpressionModifierDoubleFactory extends
    VariableModifierFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7080751306938363931L;

  @Override
  public IVariableModifier<?> create(ParameterBlock parameter, Context context) {
    INode mathExpression =
        ParameterBlocks.getSubBlockValue(parameter, EXPRESSION);
    return createModifier(mathExpression);
  }

  /**
   * Creates a new MathModifier for double expressions.
   * 
   * @param values
   *          the values of the sequence
   * 
   * @return the sequence modifier
   */
  private MathModifierDouble createModifier(INode mathExpression) {
    return new MathModifierDouble(mathExpression);
  }
}
