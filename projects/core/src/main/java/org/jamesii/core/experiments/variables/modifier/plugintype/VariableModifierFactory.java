/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier.plugintype;

import org.jamesii.core.experiments.variables.modifier.IVariableModifier;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base class for all factories creating variable modifiers.
 * 
 * @author Jan Himmelspach
 */
public abstract class VariableModifierFactory extends
    Factory<IVariableModifier<?>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3178944851541277144L;

  /**
   * The constant expression. Use this to forward an expression to the modifier,
   * if the modifier supports arbitrary expressions.
   */
  public static final String EXPRESSION = "expression";

  /**
   * The constant VALUES. Use this as ident in the parameter block based to the
   * {@link #create(ParameterBlock, Context)} method to pass on the vector of values.
   */
  public static final String VALUES = "values";

  /**
   * Return a new instance of the variable modifier to be used.
 * @param parameter
   *          configuration parameters
 * @return variable modifier
   */
  @Override
  public abstract IVariableModifier<?> create(ParameterBlock parameter, Context context);

}
