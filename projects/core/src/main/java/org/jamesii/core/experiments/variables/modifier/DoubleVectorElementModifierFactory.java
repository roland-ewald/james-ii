/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import java.util.List;

import org.jamesii.core.experiments.variables.modifier.plugintype.VariableModifierFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating DoubleVectorElementModifier objects.
 * 
 * @author Jan Himmelspach
 */
public class DoubleVectorElementModifierFactory extends VariableModifierFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3826422949131823387L;

  /**
   * The constant DIM. Use this as ident in the parameter block based to the
   * {@link #create(ParameterBlock, Context)} method to pass on the dimension.
   */
  public static final String DIM = "dim";

  @Override
  public IVariableModifier<?> create(ParameterBlock parameter, Context context) {
    List<Double[]> values = ParameterBlocks.getSubBlockValue(parameter, VALUES);
    Integer dim = ParameterBlocks.getSubBlockValue(parameter, DIM);
    if (dim == null) {
      dim = 0;
    }
    return createModifier(values, dim);
  }

  /**
   * Creates a new DoubleVectorElementModifier.
   * 
   * @param values
   *          the values of the sequence
   * 
   * @return the sequence modifier
   */
  private DoubleVectorElementModifier createModifier(List<Double[]> values,
      int dim) {
    return new DoubleVectorElementModifier(values, dim);
  }
}
