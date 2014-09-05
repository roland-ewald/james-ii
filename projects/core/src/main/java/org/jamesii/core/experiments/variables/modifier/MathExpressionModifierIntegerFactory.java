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
 * A factory for creating MathModifierInteger objects.
 * 
 * @author Jan Himmelspach
 */
public class MathExpressionModifierIntegerFactory extends
    VariableModifierFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4130229097689040777L;

  @Override
  public IVariableModifier<?> create(ParameterBlock parameter, Context context) {
    INode mathExpression =
        ParameterBlocks.getSubBlockValue(parameter, EXPRESSION);
    return createModifier(mathExpression);
  }

  /**
   * Creates a new MathModifier for integer expressions.
   * 
   * @param values
   *          the values of the sequence
   * 
   * @return the sequence modifier
   */
  private <T> MathModifierInteger createModifier(INode mathExpression) {
    return new MathModifierInteger(mathExpression);
  }
}
