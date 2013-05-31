/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import java.util.List;

import org.jamesii.core.experiments.variables.modifier.plugintype.VariableModifierFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating SequenceModifier objects.
 * 
 * @author Jan Himmelspach
 */
public class SequenceModifierFactory extends VariableModifierFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6747349675906103692L;

  @Override
  public IVariableModifier<?> create(ParameterBlock parameter) {
    List<?> values = ParameterBlocks.getSubBlockValue(parameter, VALUES);
    return createModifier(values);
  }

  /**
   * Creates a new SequenceModifier object.
   * 
   * @param values
   *          the values of the sequence
   * 
   * @return the sequence modifier
   */
  private <T> SequenceModifier<T> createModifier(List<T> values) {
    return new SequenceModifier<>(values);
  }
}
